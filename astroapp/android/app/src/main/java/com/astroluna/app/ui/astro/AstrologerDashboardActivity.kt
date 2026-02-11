package com.astroluna.app.ui.astro

import android.content.Intent
import android.media.MediaPlayer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.io.File
import android.net.Uri
import androidx.core.content.FileProvider
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.astroluna.app.data.local.TokenManager
import com.astroluna.app.data.remote.SocketManager
import com.astroluna.app.ui.guest.GuestDashboardActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject
import com.astroluna.app.utils.CallState
import okhttp3.MediaType.Companion.toMediaType
import com.astroluna.app.ui.theme.CosmicAppTheme

class AstrologerDashboardActivity : ComponentActivity() {

    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenManager = TokenManager(this)
        val session = tokenManager.getUserSession()

        setupSocket(session?.userId)

        setContent {
            CosmicAppTheme {
                AstrologerDashboardScreen(
                    sessionName = session?.name ?: "Astrologer",
                    sessionId = session?.userId ?: "",
                    initialWallet = session?.walletBalance ?: 0.0,
                    onLogout = { performLogout() }
                )
            }
        }

        checkAndRegisterFcmToken()
    }

    private fun checkAndRegisterFcmToken() {
        com.google.firebase.messaging.FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                val session = tokenManager.getUserSession()
                if (token != null && session?.userId != null) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            com.astroluna.app.data.api.ApiService.register(
                                com.astroluna.app.utils.Constants.SERVER_URL,
                                session.userId!!,
                                token
                            )
                        } catch (e: Exception) { e.printStackTrace() }
                    }
                }
            }
        }
    }

    private fun performLogout() {
        tokenManager.clearSession()
        SocketManager.disconnect()
        val intent = Intent(this, GuestDashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupSocket(userId: String?) {
        SocketManager.init()
        if (userId != null) {
            SocketManager.registerUser(userId)
        }
        val socket = SocketManager.getSocket()
        socket?.connect()

        SocketManager.onIncomingSession { data ->
            val sessionId = data.optString("sessionId", "")
            val fromUserId = data.optString("fromUserId", "Unknown")
            val type = data.optString("type", "audio")
            val birthDataStr = data.optString("birthData", null)

            if (!CallState.canReceiveCall(sessionId)) return@onIncomingSession

            val callerName = data.optString("callerName")
                .takeIf { !it.isNullOrEmpty() }
                ?: data.optString("userName")
                .takeIf { !it.isNullOrEmpty() } ?: fromUserId

            CallState.currentSessionId = sessionId

            runOnUiThread {
                val intent = Intent(this@AstrologerDashboardActivity, com.astroluna.app.IncomingCallActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra("callerId", fromUserId)
                    putExtra("callerName", callerName)
                    putExtra("callId", sessionId)
                    putExtra("callType", type)
                    if (birthDataStr != null) putExtra("birthData", birthDataStr)
                }
                startActivity(intent)
            }
        }

        socket?.on("session-request") { args ->
            runOnUiThread {
                try {
                    val data = args[0] as? JSONObject ?: return@runOnUiThread
                    val sessionId = data.optString("sessionId", "")
                    val fromUserId = data.optString("fromUserId", "")
                    val type = data.optString("type", "chat")
                    val callerName = data.optString("callerName")
                        .takeIf { !it.isNullOrEmpty() } ?: fromUserId

                    if (!CallState.canReceiveCall(sessionId)) return@runOnUiThread

                    if (type == "chat") {
                        val intent = Intent(this@AstrologerDashboardActivity, com.astroluna.app.ui.chat.ChatActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            putExtra("sessionId", sessionId)
                            putExtra("toUserId", fromUserId)
                            putExtra("toUserName", callerName)
                            putExtra("isNewRequest", true)
                        }
                        startActivity(intent)
                    }
                } catch (e: Exception) { e.printStackTrace() }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SocketManager.offIncomingSession()
    }
}

suspend fun updateServiceStatus(userId: String, service: String, enabled: Boolean) {
    try {
        val client = okhttp3.OkHttpClient()
        val body = okhttp3.RequestBody.create(
            "application/json".toMediaType(),
            JSONObject().apply {
                put("userId", userId)
                put("service", service)
                put("enabled", enabled)
            }.toString()
        )
        val request = okhttp3.Request.Builder()
            .url("https://astroluna.in/api/astrologer/service-toggle")
            .post(body)
            .build()
        client.newCall(request).execute()
        if (enabled) {
            SocketManager.init()
            SocketManager.registerUser(userId)
        }
    } catch (e: Exception) { e.printStackTrace() }
}

@Composable
fun AstrologerDashboardScreen(
    sessionName: String,
    sessionId: String,
    initialWallet: Double,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var walletBalance by remember { mutableDoubleStateOf(initialWallet) }

    // Service States
    var isChatOnline by remember { mutableStateOf(false) }
    var isAudioOnline by remember { mutableStateOf(false) }
    var isVideoOnline by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            try {
                val client = okhttp3.OkHttpClient()
                val request = okhttp3.Request.Builder()
                    .url("https://astroluna.in/api/user/$sessionId")
                    .build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val json = JSONObject(response.body?.string() ?: "{}")
                    walletBalance = json.optDouble("walletBalance", initialWallet)
                    isChatOnline = json.optBoolean("isChatOnline", false)
                    isAudioOnline = json.optBoolean("isAudioOnline", false)
                    isVideoOnline = json.optBoolean("isVideoOnline", false)
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    Scaffold(
        containerColor = Color(0xFF0F0F2D), // Deep Midnight Blue
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                containerColor = Color(0xFFFFD700), // Gold
                contentColor = Color.Black,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(Icons.Default.SupportAgent, null)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Premium Animated Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF1A1A40), Color(0xFF0F0F2D))
                        )
                    )
            ) {
                // Background Glow
                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .offset(x = (-100).dp, y = (-100).dp)
                        .background(Color(0xFF7B42F6).copy(alpha = 0.2f), CircleShape)
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp, start = 24.dp, end = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Namaste,", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                            Text(sessionName, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            IconButton(
                                onClick = { },
                                modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.1f), CircleShape)
                            ) {
                                Icon(Icons.Default.Notifications, null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            IconButton(
                                onClick = onLogout,
                                modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.1f), CircleShape)
                            ) {
                                Icon(Icons.Default.Logout, null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    // Premium Glass Metrics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MetricCard(
                            label = "Earnings",
                            value = "₹${String.format("%.0f", walletBalance)}",
                            icon = Icons.Default.AccountBalanceWallet,
                            color = Color(0xFFFFD700),
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            label = "Rating",
                            value = "4.9 ★",
                            icon = Icons.Default.Star,
                            color = Color(0xFF00FFCC),
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            label = "Calls",
                            value = "1.2k",
                            icon = Icons.Default.Call,
                            color = Color(0xFF7B42F6),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 2. Service Visibility (Fixed Offset)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .offset(y = (-40).dp)
            ) {
                Text(
                    "LIVE AVAILABILITY",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
                )

                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E3F)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(28.dp))
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        PremiumToggleRow(
                            label = "Chat",
                            icon = Icons.AutoMirrored.Filled.Chat,
                            checked = isChatOnline,
                            activeColor = Color(0xFFA162F7),
                            onCheckedChange = {
                                isChatOnline = it
                                scope.launch(Dispatchers.IO) { updateServiceStatus(sessionId, "chat", it) }
                            }
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color.White.copy(alpha = 0.05f))
                        PremiumToggleRow(
                            label = "Audio Call",
                            icon = Icons.Default.Call,
                            checked = isAudioOnline,
                            activeColor = Color(0xFF42A5F5),
                            onCheckedChange = {
                                isAudioOnline = it
                                scope.launch(Dispatchers.IO) { updateServiceStatus(sessionId, "audio", it) }
                            }
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color.White.copy(alpha = 0.05f))
                        PremiumToggleRow(
                            label = "Video Call",
                            icon = Icons.Default.VideoCall,
                            checked = isVideoOnline,
                            activeColor = Color(0xFFF06292),
                            onCheckedChange = {
                                isVideoOnline = it
                                scope.launch(Dispatchers.IO) { updateServiceStatus(sessionId, "video", it) }
                            }
                        )
                    }
                }
            }

            // 3. Quick Actions
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .offset(y = (-20).dp)
            ) {
                Text(
                    "QUICK ACTIONS",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
                )

                val actions = listOf(
                    DashboardAction("Recordings", Icons.Default.Mic, Color(0xFF7B42F6).copy(alpha = 0.1f), Color(0xFF7B42F6)),
                    DashboardAction("Chat History", Icons.AutoMirrored.Filled.Chat, Color(0xFF42A5F5).copy(alpha = 0.1f), Color(0xFF42A5F5)),
                    DashboardAction("Earnings", Icons.Default.AccountBalanceWallet, Color(0xFFFFD700).copy(alpha = 0.1f), Color(0xFFFFD700)),
                    DashboardAction("Reviews", Icons.Default.AutoAwesome, Color(0xFF00FFCC).copy(alpha = 0.1f), Color(0xFF00FFCC)),
                    DashboardAction("History", Icons.Default.Timeline, Color(0xFFF06292).copy(alpha = 0.1f), Color(0xFFF06292)),
                    DashboardAction("Settings", Icons.Default.ManageAccounts, Color.White.copy(alpha = 0.05f), Color.White)
                )

                actions.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowItems.forEach { action ->
                            PremiumActionCard(
                                action = action,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    when(action.label) {
                                        "Earnings" -> context.startActivity(Intent(context, EarningsActivity::class.java))
                                        "History" -> context.startActivity(Intent(context, AstrologerHistoryActivity::class.java))
                                        "Settings" -> context.startActivity(Intent(context, com.astroluna.app.ui.astro.AstrologerEditProfileActivity::class.java))
                                        "Recordings" -> showRecordingsDialog(context)
                                        else -> Toast.makeText(context, "${action.label} Clicked", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Version 1.2.0 (Premium Tier)", color = Color.White.copy(alpha = 0.2f), fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun MetricCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier
            .height(100.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(label, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
        }
    }
}

@Composable
fun PremiumToggleRow(label: String, icon: ImageVector, checked: Boolean, activeColor: Color, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(44.dp),
            shape = RoundedCornerShape(14.dp),
            color = if (checked) activeColor.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = if (checked) activeColor else Color.White.copy(alpha = 0.4f), modifier = Modifier.size(22.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 16.sp)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = activeColor,
                uncheckedThumbColor = Color.White.copy(alpha = 0.4f),
                uncheckedTrackColor = Color.White.copy(alpha = 0.1f)
            )
        )
    }
}

@Composable
fun PremiumActionCard(action: DashboardAction, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF1E1E3F),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(action.icon, null, tint = action.tint, modifier = Modifier.size(26.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = action.label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

data class DashboardAction(val label: String, val icon: ImageVector, val bg: Color, val tint: Color)

fun showRecordingsDialog(context: android.content.Context) {
    val dir = File(context.getExternalFilesDir(null), "Recordings")
    if (!dir.exists() || dir.listFiles()?.isEmpty() == true) {
        Toast.makeText(context, "No recordings found", Toast.LENGTH_SHORT).show()
        return
    }
    val files = dir.listFiles()?.sortedByDescending { it.lastModified() } ?: emptyList()
    val fileNames = files.map { it.name }
    android.app.AlertDialog.Builder(context).apply {
        setTitle("Recent Recordings")
        setItems(fileNames.toTypedArray()) { _, which -> showFileOptions(context, files[which]) }
        setNegativeButton("Cancel", null)
        show()
    }
}

private var mediaPlayer: android.media.MediaPlayer? = null
fun playRecording(context: android.content.Context, file: File) {
    try {
        mediaPlayer?.release()
        mediaPlayer = android.media.MediaPlayer().apply {
            setDataSource(file.absolutePath)
            prepare()
            start()
        }
        mediaPlayer?.setOnCompletionListener { it.release(); mediaPlayer = null }
    } catch (e: Exception) { e.printStackTrace() }
}

fun showFileOptions(context: android.content.Context, file: File) {
    android.app.AlertDialog.Builder(context).apply {
        setTitle("Options: ${file.name}")
        setItems(arrayOf("Play Recording", "Open in Social App", "Share Recording")) { _, which ->
            when (which) {
                0 -> playRecording(context, file)
                1 -> openFileInExplorer(context, file)
                2 -> shareRecording(context, file)
            }
        }
        setNegativeButton("Back", null)
        show()
    }
}

fun openFileInExplorer(context: android.content.Context, file: File) {
    try {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        context.startActivity(Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "audio/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        })
    } catch (e: Exception) { e.printStackTrace() }
}

fun shareRecording(context: android.content.Context, file: File) {
    try {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
            type = "audio/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }, "Share Recording"))
    } catch (e: Exception) { e.printStackTrace() }
}
