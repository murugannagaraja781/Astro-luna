package com.astroluna.app.ui.chat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.astroluna.app.R
import com.astroluna.app.data.local.TokenManager
import com.astroluna.app.data.remote.SocketManager
import com.astroluna.app.ui.theme.CosmicAppTheme
import com.astroluna.app.utils.SoundManager
import org.json.JSONObject
import java.util.UUID
import coil.compose.AsyncImage
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.foundation.lazy.LazyListScope

// --- Visual Constants ---
private val ColorPrimary = Color(0xFF673AB7) // Deep Purple
private val ColorSecondary = Color(0xFF9575CD) // Lighter Purple
private val ColorBackground = Color(0xFFF7F9FC)
private val ColorSurface = Color(0xFFFFFFFF)
private val ColorTextPrimary = Color(0xFF1A1C1E)
private val ColorTextSecondary = Color(0xFF757575)
private val ColorBubbleMe = ColorPrimary
private val ColorBubbleOther = ColorSurface
private val ColorTextMe = Color.White
private val ColorTextOther = ColorTextPrimary
private val ColorDivider = Color(0xFFEEEEEE)
private val ColorStatusBlue = Color(0xFF2196F3)

private fun formatTime(timestamp: Long): String {
    if (timestamp == 0L) return ""
    return java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date(timestamp))
}

data class ChatMessage(val id: String, val text: String, val isSent: Boolean, var status: String = "sent", val timestamp: Long = 0)

class ChatActivity : ComponentActivity() {

    private val viewModel: ChatViewModel by viewModels()
    private var toUserId: String? = null
    private var toUserImage: String? = null
    private var sessionId: String? = null
    private var clientBirthData by mutableStateOf<JSONObject?>(null)
    private var sessionDuration by mutableStateOf("00:00")
    private var remainingTime by mutableStateOf("")
    private var chatDurationSeconds = 0
    private var remainingSeconds = 0
    private var timerHandler = android.os.Handler(android.os.Looper.getMainLooper())
    private val timerRunnable = object : Runnable {
        override fun run() {
            chatDurationSeconds++
            val minutes = chatDurationSeconds / 60
            val seconds = chatDurationSeconds % 60
            sessionDuration = String.format("%02d:%02d", minutes, seconds)

            if (remainingSeconds > 0) {
                remainingSeconds--
                val remMins = remainingSeconds / 60
                val remSecs = remainingSeconds % 60
                remainingTime = String.format("%02d:%02d", remMins, remSecs)
            } else if (remainingSeconds == 0 && remainingTime.isNotEmpty()) {
                remainingTime = "00:00"
            }

            timerHandler.postDelayed(this, 1000)
        }
    }

    private val editIntakeLauncher = registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
             val dataStr = result.data?.getStringExtra("birthData")
             if (dataStr != null) {
                 try {
                     val newData = JSONObject(dataStr)
                     clientBirthData = newData
                     Toast.makeText(this, "Details Updated", Toast.LENGTH_SHORT).show()
                     SocketManager.getSocket()?.emit("client-birth-chart", JSONObject().apply {
                         put("sessionId", sessionId)
                         put("toUserId", toUserId)
                         put("birthData", newData)
                     })
                 } catch (e: Exception) { e.printStackTrace() }
             }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ensure socket is initialized and connected
        SocketManager.init()
        SocketManager.ensureConnection()
        handleIntent(intent)

        // --- GLOBAL STATE FIX: Mark chat as active to prevent incoming calls during session ---
        com.astroluna.app.utils.CallState.isCallActive = true
        com.astroluna.app.utils.CallState.currentSessionId = sessionId
        setContent {
            CosmicAppTheme {
                ChatScreen(
                    viewModel = viewModel,
                    sessionDuration = sessionDuration,
                    title = intent?.getStringExtra("toUserName") ?: "Chat",
                    onBack = { finish() },
                    onEndChat = { endChat() },
                    onEditIntake = {
                        val intent = Intent(this, com.astroluna.app.ui.intake.IntakeActivity::class.java)
                        intent.putExtra("isEditMode", true)
                        intent.putExtra("existingData", clientBirthData?.toString())
                        if (TokenManager(this).getUserSession()?.role == "astrologer") {
                            intent.putExtra("targetUserId", toUserId)
                        }
                        editIntakeLauncher.launch(intent)
                    },
                    onViewChart = {
                        if (clientBirthData != null) {
                            val intent = Intent(this, com.astroluna.app.ui.chart.VipChartActivity::class.java)
                            intent.putExtra("birthData", clientBirthData.toString())
                            startActivity(intent)
                        } else {
                             Toast.makeText(this, "Waiting for Client Data...", Toast.LENGTH_SHORT).show()
                        }
                    },
                    isAstrologer = TokenManager(this).getUserSession()?.role == "astrologer",
                    toUserId = toUserId,
                    toUserImage = toUserImage,
                    sessionId = sessionId,
                    remainingTime = remainingTime,
                    clientBirthData = clientBirthData
                )
            }
        }
        setupObservers()
        timerHandler.post(timerRunnable)

        // Listen for client birth data updates during session
        SocketManager.getSocket()?.on("client-birth-chart") { args ->
            if (args != null && args.isNotEmpty()) {
                val data = args[0] as? JSONObject
                val updatedData = data?.optJSONObject("birthData")
                if (updatedData != null) {
                    runOnUiThread {
                        clientBirthData = updatedData
                        val myRole = TokenManager(this@ChatActivity).getUserSession()?.role
                        if (myRole == "client") {
                            Toast.makeText(this@ChatActivity, "Astrologer updated your birth details", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@ChatActivity, "Client updated their birth details", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.let {
            setIntent(it)
            handleIntent(it)
        }
    }

    private var pendingAccept = false

    private fun handleIntent(intent: Intent?) {
        toUserId = intent?.getStringExtra("toUserId")
        toUserImage = intent?.getStringExtra("toUserImage")
        sessionId = intent?.getStringExtra("sessionId")
        val birthDataStr = intent?.getStringExtra("birthData")
        if (!birthDataStr.isNullOrEmpty()) {
             try {
                val obj = JSONObject(birthDataStr)
                if (obj.length() > 0) clientBirthData = obj
             } catch (e: Exception) { e.printStackTrace() }
        }
        if (sessionId == null) {
            finish()
            return
        }
        val isNewRequest = intent?.getBooleanExtra("isNewRequest", false) == true
        if (isNewRequest && sessionId != null && toUserId != null) {
            SoundManager.playAcceptSound()
            pendingAccept = true // Will emit in onResume after socket registration
        }
        if (sessionId != null) {
              viewModel.loadHistory(sessionId!!)
              viewModel.joinSessionSafe(sessionId!!)
        }
    }

    private fun setupObservers() {
        viewModel.sessionSummary.observe(this) { summary ->
            timerHandler.removeCallbacks(timerRunnable)
            val minutes = summary.duration / 60
            val seconds = summary.duration % 60
            val durationStr = String.format("%02d:%02d", minutes, seconds)
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Chat Summary")
                .setMessage("Duration: $durationStr\nDeducted: ₹${String.format("%.2f", summary.deducted)}")
                .setPositiveButton("OK") { _, _ -> finish() }
                .setCancelable(false)
                .show()
        }
        viewModel.sessionEnded.observe(this) { ended ->
            if (ended && viewModel.sessionSummary.value == null) {
                Toast.makeText(this, "Chat Ended by Partner", Toast.LENGTH_SHORT).show()

                // Clear all notifications
                val notificationManager = getSystemService(android.content.Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
                notificationManager.cancelAll()

                // Navigate to appropriate dashboard
                val userSession = TokenManager(this).getUserSession()
                if (userSession?.role == "astrologer") {
                    val intent = android.content.Intent(this, com.astroluna.app.ui.astro.AstrologerDashboardActivity::class.java)
                    intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    val intent = android.content.Intent(this, com.astroluna.app.MainActivity::class.java)
                    intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                finish()
            }
        }
        viewModel.availableMinutes.observe(this) { mins ->
            remainingSeconds = (mins * 60)
            val remMins = remainingSeconds / 60
            val remSecs = remainingSeconds % 60
            remainingTime = String.format("%02d:%02d", remMins, remSecs)
        }
    }

    private fun endChat() {
        android.util.Log.d("ChatActivity", "endChat clicked. SessionId: $sessionId")
        if (sessionId != null) {
            Toast.makeText(this, "Ending Chat...", Toast.LENGTH_SHORT).show()
            viewModel.endSession(sessionId!!)

            // Clear all notifications
            val notificationManager = getSystemService(android.content.Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            notificationManager.cancelAll()

            // Delay to ensure socket emit completes, then navigate
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                // Check if user is Astrologer
                val userSession = TokenManager(this).getUserSession()
                if (userSession?.role == "astrologer") {
                    // Navigate to Astrologer Dashboard
                    val intent = android.content.Intent(this, com.astroluna.app.ui.astro.AstrologerDashboardActivity::class.java)
                    intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    // Client - go to MainActivity
                    val intent = android.content.Intent(this, com.astroluna.app.MainActivity::class.java)
                    intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                finish()
            }, 500)
        } else {
             Toast.makeText(this, "Error: Session ID is null", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        // Re-synchronize on resume to catch any messages missed during multitasking
        sessionId?.let {
            viewModel.loadHistory(it)
            viewModel.joinSessionSafe(it)
        }

        viewModel.startListeners()
        val myUserId = TokenManager(this).getUserSession()?.userId
        if (myUserId != null) {
            SocketManager.registerUser(myUserId) {
                // Socket registered - now emit pending accept if any
                if (pendingAccept && sessionId != null && toUserId != null) {
                    pendingAccept = false
                    viewModel.acceptSession(sessionId!!, toUserId!!)
                    android.util.Log.d("ChatActivity", "Emitted acceptSession after socket registration")
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // We no longer stop listeners here to allow background reception while multi-tasking
    }

    override fun finish() {
        // Reset CallState
        com.astroluna.app.utils.CallState.isCallActive = false
        com.astroluna.app.utils.CallState.currentSessionId = null
        super.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        timerHandler.removeCallbacks(timerRunnable)
        viewModel.stopListeners()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    sessionDuration: String,
    title: String,
    onBack: () -> Unit,
    onEndChat: () -> Unit,
    onEditIntake: () -> Unit,
    onViewChart: () -> Unit,
    isAstrologer: Boolean,
    toUserId: String?,
    toUserImage: String?,
    sessionId: String?,
    remainingTime: String,
    clientBirthData: JSONObject? = null
) {
    val messages by viewModel.history.observeAsState(emptyList())
    val isTyping by viewModel.typingStatus.observeAsState(false)
    val listState = rememberLazyListState()
    var inputText by remember { mutableStateOf("") }
    var replyingTo by remember { mutableStateOf<ChatMessage?>(null) }
    val displayedMessages = remember(messages) { messages }

    LaunchedEffect(displayedMessages.size) {
        if (displayedMessages.isNotEmpty()) listState.animateScrollToItem(displayedMessages.size - 1)
    }

    Scaffold(
        containerColor = ColorBackground,
        topBar = {
            Column(modifier = Modifier.background(ColorPrimary)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }

                    AsyncImage(
                        model = toUserImage ?: R.drawable.ic_person_placeholder,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp).clip(CircleShape).border(1.dp, ColorPrimary, CircleShape)
                    )

                    Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                        Text(
                            title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            "Vedic Astrologer • Online",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }

                    Surface(
                        onClick = onEndChat,
                        shape = RoundedCornerShape(50),
                        color = Color.Red.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CallEnd, null, tint = Color.Red, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("End Chat", color = Color.Red, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }

                // Stats Bar
                Row(
                    modifier = Modifier.fillMaxWidth().background(Color(0xFF000000).copy(alpha = 0.2f)).padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.AccessTime, null, tint = Color(0xFFFF9800), modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            if (isAstrologer && remainingTime.isNotEmpty()) "Rem: $remainingTime" else "Time: $sessionDuration",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFFF9800),
                            maxLines = 1
                        )
                    }

                    Text(
                        "Rate: ₹20/min",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.padding(horizontal = 8.dp),
                        maxLines = 1
                    )

                    Text(
                        "₹84 deducted",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFF9800),
                        maxLines = 1,
                        modifier = Modifier.weight(1f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.End
                    )
                }
            }
        },
        bottomBar = {
            ChatInputBar(
                text = inputText,
                replyingTo = replyingTo,
                onTextChange = {
                    inputText = it
                    if (toUserId != null) viewModel.sendTyping(toUserId)
                },
                onCancelReply = { replyingTo = null },
                onSend = {
                    if (inputText.isNotBlank() && toUserId != null && sessionId != null) {
                         var finalText = inputText
                         if (replyingTo != null) {
                             val snippet = replyingTo!!.text.take(50).replace("\n", " ")
                             finalText = "> Replying to: $snippet\n$inputText"
                         }

                         val payload = JSONObject().apply {
                            put("toUserId", toUserId)
                            put("sessionId", sessionId)
                            put("messageId", UUID.randomUUID().toString())
                            put("timestamp", System.currentTimeMillis())
                            put("content", JSONObject().put("text", finalText))
                         }
                         viewModel.sendMessage(payload)
                         SoundManager.playSentSound()
                         inputText = ""
                         replyingTo = null
                         viewModel.sendStopTyping(toUserId)
                    }
                },
                onViewChart = if (isAstrologer) onViewChart else null,
                clientBirthData = clientBirthData
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(ColorBackground)
        ) {

            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {

                if (displayedMessages.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                             Text(
                                 text = "Start the consultation safely...",
                                 color = ColorTextSecondary,
                                 style = MaterialTheme.typography.bodyMedium
                             )
                        }
                    }
                }

                items(displayedMessages) { msg ->
                    ChatBubble(msg, isAstrologer, toUserImage, onReply = { replyingTo = msg })
                }
                if (isTyping) item { TypingBubble() }
            }

            // WATERMARK: Remaining Time for Astrologer (Big overlay if needed, or subtle)
            if (isAstrologer && remainingTime.isNotEmpty() && remainingTime != "00:00") {
                 // Already detailed in TopBar, but can keep watermark if critical
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatBubble(msg: ChatMessage, amIAstrologer: Boolean, otherUserImage: String?, onReply: () -> Unit) {
    val isMe = msg.isSent
    val bubbleColor = if (isMe) Color.White else ColorPrimary
    val textColor = if (isMe) ColorTextPrimary else Color.White
    val shape = if (isMe) RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp) else RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
    val align = if (isMe) Alignment.End else Alignment.Start

    Row(
        modifier = Modifier.fillMaxWidth().combinedClickable(onClick = {}, onLongClick = onReply),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isMe) {
            AsyncImage(
                model = otherUserImage ?: R.drawable.ic_person_placeholder,
                contentDescription = null,
                modifier = Modifier.size(24.dp).clip(CircleShape).background(ColorDivider)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }

        Column(horizontalAlignment = align) {
            Surface(
                color = bubbleColor,
                shape = shape,
                shadowElevation = 1.dp,
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                    if (msg.text.contains("> Replying to:")) {
                        val parts = msg.text.split("\n", limit = 2)
                        if (parts.size >= 1 && parts[0].startsWith("> Replying to:")) {
                            val quoteText = parts[0].removePrefix("> Replying to: ").trim()
                            val actualText = if (parts.size > 1) parts[1] else ""

                            Surface(
                                color = Color.Black.copy(alpha = 0.05f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                            ) {
                               Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                                    Box(modifier = Modifier.fillMaxHeight().width(4.dp).background(if(isMe) ColorPrimary else Color.White.copy(alpha=0.5f)))
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("Replying to", style = MaterialTheme.typography.labelSmall, color = if(isMe) ColorPrimary else Color.White.copy(alpha=0.8f))
                                        Text(quoteText, style = MaterialTheme.typography.bodySmall, maxLines = 2, color = if(isMe) ColorTextSecondary else Color.White.copy(alpha=0.7f))
                                    }
                               }
                            }
                            Text(actualText, style = MaterialTheme.typography.bodyLarge, color = textColor)
                        } else {
                            Text(msg.text, style = MaterialTheme.typography.bodyLarge, color = textColor)
                        }
                    } else {
                        Text(msg.text, style = MaterialTheme.typography.bodyLarge, color = textColor)
                    }
                }
            }
            Text(
                text = buildString {
                    append(formatTime(msg.timestamp))
                },
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = ColorTextSecondary),
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )

            if (isMe) {
                Icon(
                    imageVector = if (msg.status == "read") Icons.Default.DoneAll else if (msg.status == "delivered") Icons.Default.DoneAll else Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp).padding(start = 2.dp),
                    tint = if (msg.status == "read") ColorStatusBlue else ColorTextSecondary
                )
            }
        }
    }
}

@Composable
fun TypingBubble() {
    Surface(
        color = ColorSurface,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 1.dp,
        modifier = Modifier.padding(8.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Typing...", style = MaterialTheme.typography.bodySmall, color = ColorTextSecondary)
        }
    }
}

@Composable
fun ChatInputBar(
    text: String,
    replyingTo: ChatMessage?,
    onTextChange: (String) -> Unit,
    onCancelReply: () -> Unit,
    onSend: () -> Unit,
    onViewChart: (() -> Unit)?,
    clientBirthData: JSONObject? = null
) {
    val quickChips = listOf("My birth time is...", "When will I get promoted?", "Share my career details")

    Column(modifier = Modifier.navigationBarsPadding().background(Color.White).shadow(8.dp).padding(vertical = 4.dp)) {
        // Quick Action Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 16.dp),
            spacing = Arrangement.spacedBy(8.dp)
        ) {
            items(quickChips) { chipText ->
                Surface(
                    onClick = { onTextChange(chipText) },
                    shape = RoundedCornerShape(12.dp),
                    color = Color.DarkGray.copy(alpha = 0.8f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                ) {
                    Text(
                        chipText,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Surface(
            color = Color.Transparent,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Column {
                if (replyingTo != null) {
                    Row(
                        Modifier.fillMaxWidth().background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp)).padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                       Column {
                           Text("Replying to", style = MaterialTheme.typography.labelSmall, color = ColorPrimary)
                           Text(replyingTo!!.text.take(30), style = MaterialTheme.typography.bodySmall, color = ColorTextSecondary)
                       }
                       IconButton(onClick = onCancelReply) {
                           Icon(Icons.Default.Close, "Cancel", tint = ColorTextSecondary)
                       }
                    }
                    Spacer(Modifier.height(8.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Left Plus Icon
                    IconButton(
                        onClick = { /* attachments */ },
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(Icons.Default.Add, null, tint = Color.White)
                    }

                    Spacer(Modifier.width(8.dp))

                    OutlinedTextField(
                        value = text,
                        onValueChange = onTextChange,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        placeholder = { Text("Type your message...", color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ColorPrimary.copy(alpha = 0.5f),
                            unfocusedBorderColor = Color.LightGray,
                            focusedContainerColor = ColorBackground,
                            unfocusedContainerColor = ColorBackground,
                            focusedTextColor = ColorTextPrimary,
                            unfocusedTextColor = ColorTextPrimary
                        ),
                        trailingIcon = {
                            Icon(Icons.Default.Mic, null, tint = Color.Gray)
                        },
                        maxLines = 4
                    )

                    Spacer(Modifier.width(8.dp))

                    FloatingActionButton(
                        onClick = onSend,
                        containerColor = ColorPrimary,
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Default.Send, null)
                    }
                }
            }
        }
    }
}

@Composable
private fun LazyRow(modifier: Modifier, spacing: Arrangement.HorizontalOrVertical, content: LazyListScope.() -> Unit) {
    androidx.compose.foundation.lazy.LazyRow(modifier = modifier, horizontalArrangement = spacing, content = content)
}
