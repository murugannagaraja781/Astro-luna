package com.astroluna.app.ui.call

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import com.astroluna.app.R
import com.astroluna.app.data.remote.SocketManager
import com.astroluna.app.data.local.TokenManager
import com.astroluna.app.data.model.AuthResponse
import com.astroluna.app.ui.theme.CosmicAppTheme
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import com.astroluna.app.utils.CallState
import org.json.JSONObject
import org.webrtc.*
import java.util.LinkedList

// --- Visual Constants ---
private val ColorPrimary = Color(0xFF673AB7) // Deep Purple
private val ColorSecondary = Color(0xFF9575CD) // Lighter Purple
private val ColorBackground = Color(0xFFF7F9FC)
private val ColorSurface = Color(0xFFFFFFFF)
private val ColorTextPrimary = Color(0xFF1A1C1E)
private val ColorTextSecondary = Color(0xFF757575)
private val ColorDestructive = Color(0xFFE53935)
private val ColorSuccess = Color(0xFF43A047)

class CallActivity : ComponentActivity() {

    companion object {
        private const val TAG = "CallActivity"
        private const val PERMISSION_REQ_CODE = 101
    }

    // Views (WebRTC Renderers) - Created programmatically
    private lateinit var remoteView: SurfaceViewRenderer
    private lateinit var localView: SurfaceViewRenderer

    private lateinit var peerConnectionFactory: PeerConnectionFactory
    private lateinit var peerConnection: PeerConnection
    private lateinit var eglBase: EglBase

    private var localVideoTrack: VideoTrack? = null
    private var localAudioTrack: AudioTrack? = null
    private var videoCapturer: VideoCapturer? = null

    private var isInitiator = false
    private var partnerId: String? = null
    private var sessionId: String? = null
    private var clientBirthData: JSONObject? = null

    private lateinit var tokenManager: TokenManager
    private var session: AuthResponse? = null

    // Compose State
    private var callDurationSeconds by mutableStateOf(0)
    private var statusText by mutableStateOf("Connecting...")
    private var isBillingActive by mutableStateOf(false)
    private var isMutedState by mutableStateOf(false)
    private var isVideoEnabledState by mutableStateOf(true) // For camera toggle
    private var isSpeakerOnState by mutableStateOf(false) // For audio toggle
    private var isWebRTCConnected by mutableStateOf(false)
    private var isSessionEnded by mutableStateOf(false)
    private var isEditingIntake by mutableStateOf(false) // Track when edit form is open
    private var remainingTime by mutableStateOf("") // Available time from wallet
    private var isRecordingState by mutableStateOf(false)
    private var mediaRecorder: MediaRecorder? = null
    private var audioFile: File? = null

    private var isWebRTCInitialized = false
    private var pendingCreateOffer = false

    // Proximity Sensor for Audio Calls
    private var proximityWakeLock: android.os.PowerManager.WakeLock? = null
    private var sensorManager: android.hardware.SensorManager? = null
    private val sensorListener = object : android.hardware.SensorEventListener {
        override fun onSensorChanged(event: android.hardware.SensorEvent) {
            if (callType == "audio" && !isSpeakerOnState) {
                val distance = event.values[0]
                val isNear = distance < event.sensor.maximumRange
                if (isNear) {
                    // Turn screen off
                    if (proximityWakeLock?.isHeld == false) proximityWakeLock?.acquire()
                } else {
                    // Turn screen on
                    if (proximityWakeLock?.isHeld == true) proximityWakeLock?.release()
                }
            }
        }
        override fun onAccuracyChanged(sensor: android.hardware.Sensor?, accuracy: Int) {}
    }

    // Helper state for formatted time
    private val formattedDuration: String
        get() {
            val minutes = callDurationSeconds / 60
            val seconds = callDurationSeconds % 60
            return String.format("%02d:%02d", minutes, seconds)
        }

    private val editIntakeLauncher = registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()) { result ->
        // Delay resetting isEditingIntake to give socket time to stabilize after foreground switch
        timerHandler.postDelayed({
            isEditingIntake = false
        }, 3000)

        // Ensure socket is connected after returning from edit
        ensureSocketConnected()

        if (result.resultCode == RESULT_OK) {
             val dataStr = result.data?.getStringExtra("birthData")
             if (dataStr != null) {
                 try {
                     val newData = JSONObject(dataStr)
                     clientBirthData = newData
                     Toast.makeText(this, "Details Updated", Toast.LENGTH_SHORT).show()
                     SocketManager.getSocket()?.emit("client-birth-chart", JSONObject().apply {
                         put("sessionId", sessionId)
                         put("toUserId", partnerId)
                         put("birthData", newData)
                     })
                 } catch (e: Exception) { e.printStackTrace() }
             }
        }

        // Check ICE connection state and restart if needed
        checkAndRestoreConnection()
    }

    private val pendingIceCandidates = LinkedList<IceCandidate>()

    private val iceServers = listOf(
        PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer(),
        PeerConnection.IceServer.builder("stun:stun1.l.google.com:19302").createIceServer(),
        PeerConnection.IceServer.builder("stun:stun2.l.google.com:19302").createIceServer(),
        PeerConnection.IceServer.builder("turn:turn.astroluna.in:3478?transport=udp")
            .setUsername("webrtcuser").setPassword("strongpassword123").createIceServer(),
        PeerConnection.IceServer.builder("turn:turn.astroluna.in:3478?transport=tcp")
            .setUsername("webrtcuser").setPassword("strongpassword123").createIceServer(),
        PeerConnection.IceServer.builder("turns:turn.astroluna.in:5349")
            .setUsername("webrtcuser").setPassword("strongpassword123").createIceServer()
    )

    // Logic internal state
    private var callType: String = "video"
    private var partnerName: String? = null

    private val timerHandler = android.os.Handler(android.os.Looper.getMainLooper())
    private var listenersInitialized = false

    private val timerRunnable = object : Runnable {
        override fun run() {
            callDurationSeconds++
            timerHandler.postDelayed(this, 1000)
        }
    }



    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isEditingIntake", isEditingIntake)
        outState.putString("clientBirthData", clientBirthData?.toString())
        outState.putInt("callDurationSeconds", callDurationSeconds)
        outState.putString("sessionId", sessionId)
        outState.putString("partnerId", partnerId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            isEditingIntake = savedInstanceState.getBoolean("isEditingIntake")
            val birthDataStr = savedInstanceState.getString("clientBirthData")
            if (!birthDataStr.isNullOrEmpty()) {
                clientBirthData = JSONObject(birthDataStr)
            }
            callDurationSeconds = savedInstanceState.getInt("callDurationSeconds")
            sessionId = savedInstanceState.getString("sessionId")
            partnerId = savedInstanceState.getString("partnerId")
        }

        // --- GLOBAL STATE FIX: Mark call as active to prevent duplicate starts ---
        CallState.isCallActive = true
        CallState.currentSessionId = intent.getStringExtra("sessionId")

        // Initialize WebRTC Views Programmatically
        localView = SurfaceViewRenderer(this)
        remoteView = SurfaceViewRenderer(this)

        // Params
        partnerId = intent.getStringExtra("partnerId")
        partnerName = intent.getStringExtra("partnerName") ?: partnerId
        sessionId = intent.getStringExtra("sessionId")
        isInitiator = intent.getBooleanExtra("isInitiator", false)
        val rawType = (intent.getStringExtra("type") ?: intent.getStringExtra("callType") ?: "video").lowercase()
        // Map 'call' and 'voice' to 'audio' for internal logic
        callType = if (rawType == "audio" || rawType == "voice" || rawType == "call") "audio" else "video"

        // Initial state sync
        isVideoEnabledState = (callType == "video")
        isSpeakerOnState = (callType == "video") // Default speaker on for video, off for audio (earpiece)

        val birthDataStr = intent.getStringExtra("birthData")
        if (!birthDataStr.isNullOrEmpty()) {
             try {
                val obj = JSONObject(birthDataStr)
                if (obj.length() > 0) clientBirthData = obj
             } catch (e: Exception) { e.printStackTrace() }
        }

        tokenManager = TokenManager(this)
        session = tokenManager.getUserSession()
        val role = session?.role

        // Set Content
        setContent {
            CosmicAppTheme {
                CallScreen(
                    remoteRenderer = remoteView,
                    localRenderer = localView,
                    partnerName = partnerName ?: "Unknown",
                    duration = formattedDuration,
                    statusText = statusText,
                    isBillingActive = isBillingActive,
                    callType = callType,
                    isMuted = isMutedState,
                    isVideoEnabled = isVideoEnabledState,
                    isSpeakerOn = isSpeakerOnState,
                    role = role ?: "user",
                    remainingTime = remainingTime,
                    onToggleMic = { toggleMic() },
                    onToggleCamera = { toggleCamera() },
                    onToggleSpeaker = { toggleSpeaker() },
                    onEndCall = { endCall() },
                    onEditIntake = { openEditIntake() },
                    onShowRasi = { showRasiChart() },
                    isRecording = isRecordingState,
                    onToggleRecording = { toggleRecording() },
                    isReady = isWebRTCInitialized
                )
            }
        }

        // --- Socket Init ---
        try {
            SocketManager.init()
            session?.userId?.let { uid ->
                SocketManager.registerUser(uid)
                if (SocketManager.getSocket()?.connected() != true) {
                    SocketManager.getSocket()?.connect()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Socket init failed", e)
        }

        // Initialize Proximity WakeLock for Audio Calls
        try {
            val powerManager = getSystemService(android.content.Context.POWER_SERVICE) as android.os.PowerManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // PROXIMITY_SCREEN_OFF_WAKE_LOCK is the standard way to turn off screen during calls
                if (powerManager.isWakeLockLevelSupported(android.os.PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK)) {
                    proximityWakeLock = powerManager.newWakeLock(android.os.PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "astroluna:ProximityLock")
                }
            }
            sensorManager = getSystemService(android.content.Context.SENSOR_SERVICE) as android.hardware.SensorManager
        } catch (e: Exception) {
            Log.e(TAG, "Proximity lock init failed", e)
        }

        // Check Permissions
        if (checkPermissions()) {
            startCallLimit()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                PERMISSION_REQ_CODE
            )
        }

        // Start Timer Delay
        timerHandler.postDelayed(timerRunnable, 1000)

        // Start Remaining Time Countdown (for astrologers only)
        if (role == "astrologer") {
            lifecycleScope.launch {
                while (isActive) {
                    delay(1000)
                    if (remainingTime.isNotEmpty() && remainingTime != "00:00") {
                        val parts = remainingTime.split(":")
                        if (parts.size == 2) {
                            val mins = parts[0].toIntOrNull() ?: 0
                            val secs = parts[1].toIntOrNull() ?: 0
                            val totalSecs = mins * 60 + secs - 1
                            if (totalSecs > 0) {
                                remainingTime = String.format("%02d:%02d", totalSecs / 60, totalSecs % 60)
                            } else {
                                remainingTime = "00:00"
                                endCall() // Auto-end when time exhausted
                            }
                        }
                    }
                }
            }

            // Fetch wallet and calculate initial remaining time
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val client = okhttp3.OkHttpClient()
                    val request = okhttp3.Request.Builder()
                        .url("https://astroluna.in/api/user/${partnerId}")
                        .build()
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        val json = JSONObject(response.body?.string() ?: "{}")
                        val walletBalance = json.optDouble("walletBalance", 0.0)
                        val ratePerMin = 10.0 // Default rate, ideally from partner data
                        val totalMinutes = (walletBalance / ratePerMin).toInt()
                        remainingTime = String.format("%02d:%02d", totalMinutes, 0)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to fetch wallet balance", e)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (callType == "audio") {
            sensorManager?.getDefaultSensor(android.hardware.Sensor.TYPE_PROXIMITY)?.let {
                sensorManager?.registerListener(sensorListener, it, android.hardware.SensorManager.SENSOR_DELAY_NORMAL)
            }
        }
        ensureSocketConnected()
    }

    override fun onPause() {
        super.onPause()
        if (proximityWakeLock?.isHeld == true) {
            proximityWakeLock?.release()
        }
        sensorManager?.unregisterListener(sensorListener)
    }

    private fun toggleMic() {
        val newMute = !isMutedState
        isMutedState = newMute
        localAudioTrack?.setEnabled(!newMute)
        Toast.makeText(this, if (newMute) "Muted" else "Unmuted", Toast.LENGTH_SHORT).show()
    }

    private fun toggleCamera() {
        val enabled = localVideoTrack?.enabled() ?: true
        val newEnabled = !enabled
        localVideoTrack?.setEnabled(newEnabled)
        isVideoEnabledState = newEnabled
        Toast.makeText(this, if (newEnabled) "Camera ON" else "Camera OFF", Toast.LENGTH_SHORT).show()
    }

    private fun toggleSpeaker() {
        val newSpeaker = !isSpeakerOnState
        isSpeakerOnState = newSpeaker
        setSpeakerphoneOn(newSpeaker)
        Toast.makeText(this, if (newSpeaker) "Speaker ON" else "Speaker OFF", Toast.LENGTH_SHORT).show()
    }

    private fun setSpeakerphoneOn(on: Boolean) {
        val audioManager = getSystemService(android.content.Context.AUDIO_SERVICE) as android.media.AudioManager
        audioManager.mode = android.media.AudioManager.MODE_IN_COMMUNICATION
        audioManager.isSpeakerphoneOn = on
    }

    private fun openEditIntake() {
        isEditingIntake = true // Mark that we're editing
        val intent = android.content.Intent(this, com.astroluna.app.ui.intake.IntakeActivity::class.java)
        intent.putExtra("isEditMode", true)
        intent.putExtra("existingData", clientBirthData?.toString())
        if (tokenManager.getUserSession()?.role == "astrologer") {
            intent.putExtra("targetUserId", partnerId)
        }
        editIntakeLauncher.launch(intent)
    }

    private fun toggleRecording() {
        if (isRecordingState) {
            stopRecording()
        } else {
            startRecording()
        }
    }

    private fun startRecording() {
        try {
            val dir = File(getExternalFilesDir(null), "Recordings")
            if (!dir.exists()) dir.mkdirs()
            val safeSessionId = sessionId ?: "unknown_session"
            audioFile = File(dir, "Rec_${safeSessionId}_${System.currentTimeMillis()}.mp3")

            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(this)
            } else {
                MediaRecorder()
            }

            mediaRecorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(audioFile?.absolutePath ?: throw Exception("Failed to create file path"))
                prepare()
                start()
            }
            isRecordingState = true
            Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(TAG, "Recording failed", e)
            Toast.makeText(this, "Recording failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecordingState = false
            Toast.makeText(this, "Saved to ${audioFile?.name}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e(TAG, "Stop recording failed", e)
            isRecordingState = false
            mediaRecorder = null
        }
    }

    private fun startBackgroundService() {
        val serviceIntent = android.content.Intent(this, com.astroluna.app.CallForegroundService::class.java).apply {
            action = "ACTION_START_CALL"
            putExtra("partnerName", partnerName)
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    private fun stopBackgroundService() {
        val serviceIntent = android.content.Intent(this, com.astroluna.app.CallForegroundService::class.java).apply {
            action = "ACTION_STOP_SERVICE"
        }
        startService(serviceIntent)
    }

    /**
     * Ensure socket is connected after returning from background activity
     */
    private fun ensureSocketConnected() {
        val socket = SocketManager.getSocket()
        if (socket == null || !socket.connected()) {
            Log.d(TAG, "Socket disconnected - reconnecting...")
            SocketManager.init()
            // Re-setup listeners after reconnect
            setupSocketListeners()
            // Re-join session room
            SocketManager.getSocket()?.emit("rejoin-session", JSONObject().apply {
                put("sessionId", sessionId)
            })
        } else {
            Log.d(TAG, "Socket still connected")
        }
    }

    /**
     * Check ICE connection state and attempt restart if connection is unstable
     */
    private fun checkAndRestoreConnection() {
        try {
            val iceState = peerConnection.iceConnectionState()
            Log.d(TAG, "ICE Connection State after edit: $iceState")

            when (iceState) {
                PeerConnection.IceConnectionState.DISCONNECTED,
                PeerConnection.IceConnectionState.FAILED -> {
                    Log.w(TAG, "ICE connection unstable - requesting restart")
                    statusText = "Reconnecting..."
                    // Request ICE restart by creating a new offer with iceRestart option
                    if (isInitiator) {
                        restartIce()
                    }
                }
                PeerConnection.IceConnectionState.CONNECTED,
                PeerConnection.IceConnectionState.COMPLETED -> {
                    Log.d(TAG, "ICE connection stable")
                    statusText = ""
                }
                else -> {
                    Log.d(TAG, "ICE state: $iceState - monitoring...")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking connection state", e)
        }
    }

    /**
     * Restart ICE connection if it becomes unstable
     */
    private fun restartIce() {
        try {
            val constraints = MediaConstraints().apply {
                mandatory.add(MediaConstraints.KeyValuePair("IceRestart", "true"))
                mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
                if (callType == "video") {
                    mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
                }
            }

            peerConnection.createOffer(object : SdpObserver {
                override fun onCreateSuccess(desc: SessionDescription?) {
                    desc?.let {
                        peerConnection.setLocalDescription(object : SdpObserver {
                            override fun onSetSuccess() {
                                val signalData = JSONObject().apply {
                                    put("type", "offer")
                                    put("sdp", desc.description)
                                }
                                val payload = JSONObject().apply {
                                    put("toUserId", partnerId)
                                    put("signal", signalData)
                                }
                                sendSignal(payload)
                                Log.d(TAG, "ICE restart offer sent")
                            }
                            override fun onSetFailure(s: String?) { Log.e(TAG, "ICE restart setLocal fail: $s") }
                            override fun onCreateSuccess(p0: SessionDescription?) {}
                            override fun onCreateFailure(p0: String?) {}
                        }, desc)
                    }
                }
                override fun onSetSuccess() {}
                override fun onCreateFailure(s: String?) { Log.e(TAG, "ICE restart create fail: $s") }
                override fun onSetFailure(s: String?) {}
            }, constraints)
        } catch (e: Exception) {
            Log.e(TAG, "ICE restart failed", e)
        }
    }

    private fun checkPermissions(): Boolean {
         val hasAudio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        val hasCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        return if (callType == "audio") hasAudio else (hasAudio && hasCamera)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQ_CODE) {
             var allGranted = true
             if (grantResults.isNotEmpty()) {
                 for (result in grantResults) {
                     if (result != PackageManager.PERMISSION_GRANTED) {
                         allGranted = false
                         break
                     }
                 }
             } else {
                 allGranted = false
             }
             if (allGranted) {
                 startCallLimit()
             } else {
                 Toast.makeText(this, "Permissions required for call", Toast.LENGTH_LONG).show()
                 finish()
             }
        }
    }

    private fun startCallLimit() {
        // Initialize status
        statusText = if (isInitiator) "Ringing..." else "Connecting..."



        // Start WebRTC initialization
        if (!initWebRTC()) {
            Toast.makeText(this, "Camera/Microphone Error", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val myUserId = session?.userId
        if (myUserId == null) {
            Log.e(TAG, "Cannot start call: userId is null")
            finish()
            return
        }

        if (isInitiator) {
            SocketManager.registerUser(myUserId) { success ->
                if (success) {
                    runOnUiThread {
                        val connectPayload = JSONObject().apply {
                             put("sessionId", sessionId)
                             put("role", "client")
                        }
                        SocketManager.getSocket()?.emit("session-connect", connectPayload)


                    }
                }
            }
        } else {
            statusText = "Connecting..."
            SocketManager.registerUser(myUserId) { success ->
                if (success) {
                    runOnUiThread {
                        val payload = JSONObject().apply {
                            put("sessionId", sessionId)
                            put("toUserId", partnerId)
                            put("accept", true)
                        }
                        SocketManager.getSocket()?.emit("answer-session", payload)

                        val connectPayload = JSONObject().apply {
                            put("sessionId", sessionId)
                        }
                        SocketManager.getSocket()?.emit("session-connect", connectPayload)
                    }
                }
            }
        }

        startBackgroundService()
        setupSocketListeners()
        timerHandler.post(timerRunnable)
    }

    private fun initWebRTC(): Boolean {
        if (isWebRTCInitialized) return true
        try {
            eglBase = EglBase.create()
            val options = PeerConnectionFactory.InitializationOptions.builder(this).createInitializationOptions()
            PeerConnectionFactory.initialize(options)

            peerConnectionFactory = PeerConnectionFactory.builder()
                .setVideoEncoderFactory(DefaultVideoEncoderFactory(eglBase.eglBaseContext, true, true))
                .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglBase.eglBaseContext))
                .createPeerConnectionFactory()
        } catch (t: Throwable) {
            Log.e(TAG, "CRITICAL: WebRTC Factory init failed", t)
            runOnUiThread {
                Toast.makeText(this, "Camera/Audio engine failed. Please restart app.", Toast.LENGTH_LONG).show()
            }
            return false
        }

        if (callType == "video") {
            remoteView.init(eglBase.eglBaseContext, null)
            remoteView.setEnableHardwareScaler(true)
            remoteView.setScalingType(org.webrtc.RendererCommon.ScalingType.SCALE_ASPECT_FILL)

            localView.init(eglBase.eglBaseContext, null)
            localView.setEnableHardwareScaler(true)
            localView.setMirror(true)
            localView.setZOrderMediaOverlay(true)
            localView.setScalingType(org.webrtc.RendererCommon.ScalingType.SCALE_ASPECT_FILL)
        } else {
             setSpeakerphoneOn(false) // Audio call default
        }

        val audioSource = peerConnectionFactory.createAudioSource(MediaConstraints())
        localAudioTrack = peerConnectionFactory.createAudioTrack("101", audioSource)

        if (callType == "video") {
            videoCapturer = try {
                createCameraCapturer(Camera2Enumerator(this))
            } catch (e: Exception) {
                try {
                    createCameraCapturer(Camera1Enumerator(true))
                } catch (e1: Exception) {
                    null
                }
            }

            if (videoCapturer != null) {
                try {
                    val surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBase.eglBaseContext)
                    val videoSource = peerConnectionFactory.createVideoSource(videoCapturer!!.isScreencast)
                    videoCapturer!!.initialize(surfaceTextureHelper, this, videoSource.capturerObserver)
                    videoCapturer!!.startCapture(640, 480, 30)

                    localVideoTrack = peerConnectionFactory.createVideoTrack("100", videoSource)
                    localVideoTrack?.setEnabled(true)
                    localVideoTrack?.addSink(localView)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to start camera capture", e)
                }
            }
        }

        val rtcConfig = PeerConnection.RTCConfiguration(iceServers).apply {
            sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
            iceTransportsType = PeerConnection.IceTransportsType.ALL
            continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
        }
        val pc = peerConnectionFactory.createPeerConnection(rtcConfig, object : PeerConnection.Observer {
            override fun onSignalingChange(p0: PeerConnection.SignalingState?) {}

            override fun onIceConnectionChange(newState: PeerConnection.IceConnectionState?) {
                runOnUiThread {
                    when (newState) {
                        PeerConnection.IceConnectionState.CONNECTED -> {
                            statusText = "" // Hide status
                            isWebRTCConnected = true
                            // Auto-start recording for astrologers
                            val myRole = TokenManager(this@CallActivity).getUserSession()?.role
                            if (myRole == "astrologer" && !isRecordingState) {
                                startRecording()
                            }
                        }
                        PeerConnection.IceConnectionState.DISCONNECTED -> {
                            if (!isEditingIntake) {
                                Toast.makeText(this@CallActivity, "Connection Unstable", Toast.LENGTH_SHORT).show()
                            }
                        }
                        PeerConnection.IceConnectionState.FAILED -> {
                            if (!isEditingIntake) {
                                Toast.makeText(this@CallActivity, "Connection Failed", Toast.LENGTH_SHORT).show()
                                endCall()
                            } else {
                                Log.d(TAG, "ICE Failed while editing intake - ignoring to allow reconnect")
                                statusText = "Reconnecting..."
                            }
                        }
                        else -> {}
                    }
                }
            }
            override fun onIceConnectionReceivingChange(p0: Boolean) {}
            override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {}

            override fun onIceCandidate(candidate: IceCandidate?) {
                if (candidate != null) {
                    Log.d(TAG, "onIceCandidate: ${candidate.sdp}")
                    val signalData = JSONObject().apply {
                         put("type", "candidate")
                         put("candidate", JSONObject().apply {
                             put("candidate", candidate.sdp)
                             put("sdpMid", candidate.sdpMid)
                             put("sdpMLineIndex", candidate.sdpMLineIndex)
                         })
                    }
                    val payload = JSONObject().apply {
                        put("toUserId", partnerId)
                        put("signal", signalData)
                    }
                    sendSignal(payload)
                }
            }

            override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {}

            override fun onAddStream(stream: MediaStream?) {
                if (stream != null && stream.videoTracks.isNotEmpty() && callType == "video") {
                    val remoteVideoTrack = stream.videoTracks[0]
                    runOnUiThread {
                        remoteVideoTrack.setEnabled(true)
                        remoteVideoTrack.addSink(remoteView)
                    }
                }
            }

            override fun onTrack(transceiver: RtpTransceiver?) {
                val track = transceiver?.receiver?.track()
                if (track is VideoTrack && callType == "video") {
                    runOnUiThread {
                        track.setEnabled(true)
                        track.addSink(remoteView)
                    }
                }
            }

            override fun onRemoveStream(p0: MediaStream?) {}
            override fun onDataChannel(p0: DataChannel?) {}
            override fun onRenegotiationNeeded() {}
        })

        if (pc == null) {
            Log.e(TAG, "Failed to create PeerConnection")
            runOnUiThread {
                Toast.makeText(this, "Failed to initialize call. Please try again.", Toast.LENGTH_LONG).show()
            }
            finish()
            return false
        }

        peerConnection = pc

        localAudioTrack?.let {
            it.setEnabled(true)
            peerConnection.addTrack(it, listOf("mediaStream"))
        }
        localVideoTrack?.let {
            it.setEnabled(true)
            peerConnection.addTrack(it, listOf("mediaStream"))
        }

        isWebRTCInitialized = true

        // FIX RACE CONDITION: If billing started before we were ready, create the offer now
        if (pendingCreateOffer) {
            Log.d(TAG, "Executing pending createOffer now that WebRTC is initialized")
            pendingCreateOffer = false
            createOffer()
        }
        return true
    }

    private fun setupSocketListeners() {
        if (listenersInitialized) return
        listenersInitialized = true

        SocketManager.onSignal { data ->
            runOnUiThread {
                handleSignal(data)
            }
        }

        SocketManager.getSocket()?.on("client-birth-chart") { args ->
            try {
                val data = args[0] as JSONObject
                val bData = data.optJSONObject("birthData")
                if (bData != null) {
                    clientBirthData = bData
                    runOnUiThread {
                        val myRole = TokenManager(this@CallActivity).getUserSession()?.role
                        if (myRole == "client") {
                            Toast.makeText(this@CallActivity, "Astrologer updated your birth details", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@CallActivity, "Client updated their birth details", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) { e.printStackTrace() }
        }

        SocketManager.onBillingStarted { info ->
            runOnUiThread {
                Log.d(TAG, "Billing started - initiator? $isInitiator, ready? $isWebRTCInitialized")
                statusText = "Call Active"
                isBillingActive = true


                if (isInitiator) {
                    if (isWebRTCInitialized) {
                        createOffer()
                    } else {
                        Log.d(TAG, "WebRTC not initialized yet. Queuing offer creation.")
                        pendingCreateOffer = true
                    }
                }
                androidx.core.os.HandlerCompat.postDelayed(android.os.Handler(android.os.Looper.getMainLooper()), {
                   if(statusText == "Call Active") statusText = "" // Hide after valid
                }, null, 3000)
            }
        }

        SocketManager.onSessionEndedWithSummary { reason, deducted, earned, duration ->
            isSessionEnded = true
            runOnUiThread {
                timerHandler.removeCallbacks(timerRunnable)
                val minutes = duration / 60
                val seconds = duration % 60
                val durationStr = String.format("%02d:%02d", minutes, seconds)

                val message = when {
                    session?.role == "astrologer" -> "Duration: $durationStr\n\nYou earned: ₹${String.format("%.2f", earned)}"
                    reason == "insufficient_funds" -> "Call ended due to insufficient balance.\n\nDuration: $durationStr\nDeducted: ₹${String.format("%.2f", deducted)}"
                    else -> "Duration: $durationStr\nDeducted: ₹${String.format("%.2f", deducted)}"
                }

                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle(if (reason == "insufficient_funds") "Low Balance" else "Call Summary")
                    .setMessage(message)
                    .setPositiveButton("OK") { _, _ -> finish() }
                    .setCancelable(false)
                    .show()
            }
        }

        SocketManager.getSocket()?.on(io.socket.client.Socket.EVENT_DISCONNECT) {
             runOnUiThread {
                 // Don't end call if user is editing intake form
                 if (!isEditingIntake) {
                     statusText = "Reconnecting..."
                     // Don't finish immediately, let it attempt reconnection
                     // Only finish if session is explicitly ended by server
                     Log.d(TAG, "Socket disconnected - waiting for reconnect or session end")
                 } else {
                     Log.d(TAG, "Socket disconnected while editing - will reconnect")
                 }
             }
        }
    }

    private fun drainRemoteCandidates() {
        if (pendingIceCandidates.isNotEmpty()) {
            for (candidate in pendingIceCandidates) {
                peerConnection.addIceCandidate(candidate)
            }
            pendingIceCandidates.clear()
        }
    }

    private fun handleSignal(data: JSONObject) {
        Log.d(TAG, "Incoming signal: $data")
        val signal = data.optJSONObject("signal") ?: data
        var type = signal.optString("type")
        if (type.isEmpty() && signal.has("candidate")) type = "candidate"

        Log.d(TAG, "Processing signal type: $type")

        when (type) {
            "offer" -> {
                val descriptionStr = signal.optJSONObject("sdp")?.optString("sdp") ?: signal.optString("sdp")
                if (descriptionStr.isNotEmpty() && ::peerConnection.isInitialized) {
                    Log.d(TAG, "Handling Offer...")
                    peerConnection.setRemoteDescription(object : SimpleSdpObserver() {
                        override fun onSetSuccess() {
                            Log.d(TAG, "Remote description set (Offer), creating answer")
                            createAnswer()
                            drainRemoteCandidates()
                        }
                    }, SessionDescription(SessionDescription.Type.OFFER, descriptionStr))
                } else {
                    Log.w(TAG, "Received empty offer or PC not init")
                }
            }
            "answer" -> {
                val descriptionStr = signal.optJSONObject("sdp")?.optString("sdp") ?: signal.optString("sdp")
                if (descriptionStr.isNotEmpty() && ::peerConnection.isInitialized) {
                    Log.d(TAG, "Handling Answer...")
                    peerConnection.setRemoteDescription(object : SimpleSdpObserver() {
                        override fun onSetSuccess() {
                            Log.d(TAG, "Remote description set (Answer)")
                            drainRemoteCandidates()
                        }
                    }, SessionDescription(SessionDescription.Type.ANSWER, descriptionStr))
                } else {
                    Log.w(TAG, "Received empty answer or PC not init")
                }
            }
            "candidate" -> {
                val candidateJson = signal.optJSONObject("candidate") ?: signal
                val sdpMid = candidateJson.optString("sdpMid")
                val sdpMLineIndex = candidateJson.optInt("sdpMLineIndex", -1)
                val sdp = candidateJson.optString("candidate")

                if (sdp.isNotEmpty() && sdpMLineIndex != -1 && ::peerConnection.isInitialized) {
                    val candidate = IceCandidate(sdpMid, sdpMLineIndex, sdp)
                    if (peerConnection.remoteDescription == null) {
                        Log.d(TAG, "Queuing remote candidate (waiting for remote description)")
                        pendingIceCandidates.add(candidate)
                    } else {
                        Log.d(TAG, "Adding remote candidate immediately")
                        peerConnection.addIceCandidate(candidate)
                    }
                } else {
                    Log.w(TAG, "Invalid candidate data: sdp=$sdp, index=$sdpMLineIndex")
                }
            }
            else -> {
                Log.d(TAG, "Unknown signal type: $type")
            }
        }
    }

    private fun createOffer() {
        if (!::peerConnection.isInitialized) return
        val constraints = MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", if(callType == "video") "true" else "false"))
        }

        peerConnection.createOffer(object : SimpleSdpObserver() {
            override fun onCreateSuccess(desc: SessionDescription?) {
                if (!::peerConnection.isInitialized) return
                Log.d(TAG, "Offer created, setting local description")
                peerConnection.setLocalDescription(SimpleSdpObserver(), desc)
                val signalData = JSONObject().apply {
                    put("type", "offer")
                    put("sdp", desc?.description)
                }
                val payload = JSONObject().apply {
                    put("toUserId", partnerId)
                    put("signal", signalData)
                }
                sendSignal(payload)
            }
        }, constraints)
    }

    private fun createAnswer() {
        val constraints = MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", if(callType == "video") "true" else "false"))
        }

        peerConnection.createAnswer(object : SimpleSdpObserver() {
            override fun onCreateSuccess(desc: SessionDescription?) {
                if (!::peerConnection.isInitialized) return
                Log.d(TAG, "Answer created, setting local description")
                peerConnection.setLocalDescription(SimpleSdpObserver(), desc)
                val signalData = JSONObject().apply {
                    put("type", "answer")
                    put("sdp", desc?.description)
                }
                val payload = JSONObject().apply {
                    put("toUserId", partnerId)
                    put("signal", signalData)
                }
                sendSignal(payload)
            }
        }, constraints)
    }

    private fun sendSignal(payload: JSONObject) {
        // Standardize: ensure sessionId and toUserId are at top level
        payload.put("sessionId", sessionId)
        payload.put("toUserId", partnerId)

        val type = payload.optJSONObject("signal")?.optString("type") ?: "unknown"
        Log.d(TAG, "[Signal] Sent: type=$type, to=$partnerId")
        SocketManager.getSocket()?.emit("signal", payload)
    }

    private fun endCall() {
        stopBackgroundService()
        SocketManager.endSession(sessionId)
        finish()
    }

    override fun finish() {
        // Ensure state is cleared even if finished via system back or other means
        CallState.isCallActive = false
        CallState.currentSessionId = null
        stopBackgroundService()
        super.finish()
    }

    override fun onDestroy() {
        if (isRecordingState) {
            try { stopRecording() } catch (e: Exception) { e.printStackTrace() }
        }
        super.onDestroy()
        timerHandler.removeCallbacks(timerRunnable)
        SocketManager.off("signal")
        SocketManager.off("session-ended")
        SocketManager.off("billing-started")
        SocketManager.off("client-birth-chart")
        SocketManager.getSocket()?.off(io.socket.client.Socket.EVENT_DISCONNECT)
        try {
            if (proximityWakeLock?.isHeld == true) proximityWakeLock?.release()
            proximityWakeLock = null
        } catch (e: Exception) {}

        try {
            if (::peerConnection.isInitialized) peerConnection.close()
            videoCapturer?.stopCapture()
            videoCapturer?.dispose()
            if (::localView.isInitialized) localView.release()
            if (::remoteView.isInitialized) remoteView.release()
            if (::peerConnectionFactory.isInitialized) peerConnectionFactory.dispose()
            if (::eglBase.isInitialized) eglBase.release()
        } catch (e: Throwable) {
            Log.e(TAG, "Error destroying WebRTC resources", e)
        }
        stopBackgroundService()
    }

    private fun createCameraCapturer(enumerator: CameraEnumerator): VideoCapturer? {
        val deviceNames = enumerator.deviceNames
        for (deviceName in deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                return enumerator.createCapturer(deviceName, null)
            }
        }
        for (deviceName in deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                return enumerator.createCapturer(deviceName, null)
            }
        }
        return null
    }

    private fun showRasiChart() {
        if (clientBirthData != null) {
            val intent = android.content.Intent(this, com.astroluna.app.ui.chart.VipChartActivity::class.java)
            intent.putExtra("birthData", clientBirthData.toString())
            startActivity(intent)
        } else {
            Toast.makeText(this, "Waiting for Client Data...", Toast.LENGTH_SHORT).show()
        }
    }
}

// openHelper for simplified observer
open class SimpleSdpObserver : SdpObserver {
    override fun onCreateSuccess(p0: SessionDescription?) {
        Log.d("SimpleSdpObserver", "onCreateSuccess")
    }
    override fun onSetSuccess() {
        Log.d("SimpleSdpObserver", "onSetSuccess")
    }
    override fun onCreateFailure(p0: String?) {
        Log.e("SimpleSdpObserver", "onCreateFailure: $p0")
    }
    override fun onSetFailure(p0: String?) {
        Log.e("SimpleSdpObserver", "onSetFailure: $p0")
    }
}

@Composable
fun CallScreen(
    remoteRenderer: SurfaceViewRenderer,
    localRenderer: SurfaceViewRenderer,
    partnerName: String,
    duration: String,
    statusText: String,
    isBillingActive: Boolean,
    callType: String,
    isMuted: Boolean,
    isVideoEnabled: Boolean,
    isSpeakerOn: Boolean,
    role: String,
    remainingTime: String,
    onToggleMic: () -> Unit,
    onToggleCamera: () -> Unit,
    onToggleSpeaker: () -> Unit,
    onEndCall: () -> Unit,
    onEditIntake: () -> Unit,
    onShowRasi: () -> Unit,
    isRecording: Boolean = false,
    onToggleRecording: () -> Unit = {},
    isReady: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorTextPrimary) // Dark background for calls
    ) {
        // Remote View Layer (Full Screen)
        if (callType == "video" && isReady) {
            AndroidView(
                factory = { remoteRenderer },
                modifier = Modifier.fillMaxSize()
            )
        } else if (callType == "video" && !isReady) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ColorPrimary)
                Text("Initializing Camera...", color = Color.White, modifier = Modifier.padding(top = 80.dp))
            }
        } else {
            // Audio Call UI Placeholder
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                 Icon(
                     imageVector = Icons.Default.Person,
                     contentDescription = "User",
                     tint = ColorTextSecondary,
                     modifier = Modifier.size(120.dp)
                 )
            }
        }

        // Top Info Overlay
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha=0.6f), Color.Transparent)))
                .padding(top = 40.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = partnerName,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = duration,
                color = Color.White.copy(alpha=0.8f),
                style = MaterialTheme.typography.bodyMedium
            )
            if (role == "astrologer" && remainingTime.isNotEmpty() && remainingTime != "00:00") {
                  Text(
                    text = "Time: $remainingTime",
                    color = ColorDestructive,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            if (statusText.isNotEmpty()) {
                  Text(
                    text = statusText,
                    color = ColorSuccess,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        // Local Video (PIP)
        if (callType == "video") {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 100.dp, end = 16.dp)
                    .size(width = 100.dp, height = 140.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, Color.White.copy(alpha=0.5f), RoundedCornerShape(16.dp))
                    .shadow(4.dp)
                    .background(Color.DarkGray)
            ) {
                if (isReady) {
                    AndroidView(
                        factory = { localRenderer },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // Bottom Controls Container
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha=0.8f))))
                .padding(bottom = 30.dp, top = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Main Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ControlBtnItem(onClick = onToggleMic, icon = if (!isMuted) Icons.Default.Mic else Icons.Default.MicOff, label = "Mute", active = !isMuted)
                    if (callType == "video") {
                        ControlBtnItem(onClick = onToggleCamera, icon = if (isVideoEnabled) Icons.Default.Videocam else Icons.Default.VideocamOff, label = "Video", active = isVideoEnabled)
                    }
                    ControlBtnItem(onClick = onToggleSpeaker, icon = if (isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeOff, label = "Speaker", active = isSpeakerOn)
                }

                // Secondary Actions & End Call
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (role == "astrologer") {
                        ControlBtnItem(onClick = onShowRasi, icon = Icons.Rounded.DateRange, label = "Chart", active = true, isMini = true)
                         Spacer(modifier = Modifier.width(32.dp))
                    } else {
                        // User can edit intake
                         ControlBtnItem(onClick = onEditIntake, icon = Icons.Default.Edit, label = "Edit", active = false, isMini = true)
                         Spacer(modifier = Modifier.width(32.dp))
                    }

                    // End Call
                    IconButton(
                        onClick = onEndCall,
                        modifier = Modifier
                            .size(72.dp)
                            .shadow(8.dp, CircleShape)
                            .background(ColorDestructive, CircleShape)
                    ) {
                        Icon(Icons.Default.CallEnd, "End", tint = Color.White, modifier = Modifier.size(36.dp))
                    }

                    if (role == "astrologer") {
                         Spacer(modifier = Modifier.width(32.dp))
                        ControlBtnItem(
                            onClick = onToggleRecording,
                            icon = if (isRecording) Icons.Default.Stop else Icons.Default.FiberManualRecord,
                            label = if (isRecording) "Stop" else "REC",
                            active = isRecording,
                            isMini = true
                        )
                    } else {
                         Spacer(modifier = Modifier.width(80.dp)) // Balance spacing
                    }
                }
            }
        }
    }
}

@Composable
fun ControlBtnItem(onClick: () -> Unit, icon: ImageVector, label: String, active: Boolean, isMini: Boolean = false) {
    val size = if(isMini) 40.dp else 56.dp
    val iconSize = if(isMini) 20.dp else 24.dp

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        val bgColor = if (active && !isMini) Color.White else Color.White.copy(alpha=0.2f)
        val tintColor = if (active && !isMini) ColorTextPrimary else Color.White

        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(bgColor)
        ) {
             Icon(icon, null, tint = tintColor, modifier = Modifier.size(iconSize))
        }
        if (!isMini) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.White)
        }
    }
}