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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                containerColor = Color(0xFF7B42F6),
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(Icons.Default.Brightness4, null)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF8F9FE))
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Purple Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF7B42F6), Color(0xFF6C3BFF))
                        )
                    )
                    .padding(top = 40.dp, bottom = 60.dp, start = 20.dp, end = 20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box {
                        Surface(
                            modifier = Modifier.size(64.dp),
                            shape = CircleShape,
                            color = Color.White.copy(alpha=0.3f)
                        ) {
                            Icon(Icons.Default.Person, null, modifier = Modifier.padding(12.dp), tint = Color.White)
                        }
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(Color(0xFF21D0B2), CircleShape)
                                .border(2.dp, Color(0xFF7B42F6), CircleShape)
                                .align(Alignment.BottomEnd)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Welcome back,", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                        Text(sessionName, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        IconButton(
                            onClick = { },
                            modifier = Modifier.size(44.dp).background(Color.White.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Icon(Icons.Default.Notifications, null, tint = Color.White)
                        }
                        IconButton(
                            onClick = onLogout,
                            modifier = Modifier.size(44.dp).background(Color.White.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Icon(Icons.Default.Logout, null, tint = Color.White)
                        }
                    }
                }
            }

            // 2. Service Availability Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .offset(y = (-30).dp)
                    .shadow(12.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Wifi, null, tint = Color(0xFF7B42F6), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Service Availability", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF111827))
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    ServiceToggleRow(
                        label = "Chat",
                        icon = Icons.AutoMirrored.Filled.Chat,
                        iconBg = Color(0xFFF3E5F5),
                        iconTint = Color(0xFF7B1FA2),
                        checked = isChatOnline,
                        onCheckedChange = {
                            isChatOnline = it
                            scope.launch(Dispatchers.IO) { updateServiceStatus(sessionId, "chat", it) }
                        }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    ServiceToggleRow(
                        label = "Audio Call",
                        icon = Icons.Default.Call,
                        iconBg = Color(0xFFE3F2FD),
                        iconTint = Color(0xFF1976D2),
                        checked = isAudioOnline,
                        onCheckedChange = {
                            isAudioOnline = it
                            scope.launch(Dispatchers.IO) { updateServiceStatus(sessionId, "audio", it) }
                        }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    ServiceToggleRow(
                        label = "Video Call",
                        icon = Icons.Default.VideoCall,
                        iconBg = Color(0xFFFCE4EC),
                        iconTint = Color(0xFFC2185B),
                        checked = isVideoOnline,
                        onCheckedChange = {
                            isVideoOnline = it
                            scope.launch(Dispatchers.IO) { updateServiceStatus(sessionId, "video", it) }
                        }
                    )
                }
            }

            // 3. Action Grid
            val actions = listOf(
                DashboardAction("CALL", Icons.Default.Call, Color(0xFFE0F2F1), Color(0xFF00897B)),
                DashboardAction("CHAT", Icons.AutoMirrored.Filled.Chat, Color(0xFFE8EAF6), Color(0xFF3F51B5)),
                DashboardAction("EARNINGS", Icons.Default.AccountBalanceWallet, Color(0xFFFFF8E1), Color(0xFFFFA000)),
                DashboardAction("REVIEWS", Icons.Default.Star, Color(0xFFFBE9E7), Color(0xFFD84315)),
                DashboardAction("HISTORY", Icons.Default.History, Color(0xFFE1F5FE), Color(0xFF0288D1)),
                DashboardAction("PROFILE", Icons.Default.Person, Color(0xFFF3E5F5), Color(0xFF8E24AA))
            )

            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                actions.chunked(3).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowItems.forEach { action ->
                            ActionCard(
                                action = action,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    when(action.label) {
                                        "EARNINGS" -> context.startActivity(Intent(context, EarningsActivity::class.java))
                                        "HISTORY" -> context.startActivity(Intent(context, AstrologerHistoryActivity::class.java))
                                        "PROFILE" -> context.startActivity(Intent(context, com.astroluna.app.ui.settings.SettingsActivity::class.java))
                                        "CALL" -> showRecordingsDialog(context)
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
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("TERMS", fontSize = 11.sp, color = Color.Gray)
                    Text("|", fontSize = 11.sp, color = Color.LightGray)
                    Text("REFUNDS", fontSize = 11.sp, color = Color.Gray)
                    Text("|", fontSize = 11.sp, color = Color.LightGray)
                    Text("SHIPPING", fontSize = 11.sp, color = Color.Gray)
                    Text("|", fontSize = 11.sp, color = Color.LightGray)
                    Text("RETURNS", fontSize = 11.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("© 2024 Astro Luna. All Rights Reserved.", fontSize = 11.sp, color = Color.LightGray)
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun ServiceToggleRow(label: String, icon: ImageVector, iconBg: Color, iconTint: Color, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(48.dp).background(iconBg, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
        Text(if (checked) "ON" else "OFF", fontSize = 12.sp, fontWeight = FontWeight.Black, color = if(checked) Color(0xFF21D0B2) else Color.Gray, modifier = Modifier.padding(end = 8.dp))
        Switch(
            checked = checked, onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF21D0B2), uncheckedThumbColor = Color.White)
        )
    }
}

@Composable
fun ActionCard(action: DashboardAction, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .shadow(8.dp, RoundedCornerShape(22.dp), spotColor = action.tint.copy(alpha = 0.3f))
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color.White, action.bg.copy(alpha = 0.5f)),
                        start = Offset(0f, 0f),
                        end = Offset(100f, 100f)
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        Brush.verticalGradient(listOf(action.bg, Color.White.copy(alpha = 0.5f))),
                        CircleShape
                    )
                    .shadow(4.dp, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(action.icon, null, tint = action.tint, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = action.label,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF374151), // Slate-800 equivalent
                letterSpacing = 0.5.sp,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
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
