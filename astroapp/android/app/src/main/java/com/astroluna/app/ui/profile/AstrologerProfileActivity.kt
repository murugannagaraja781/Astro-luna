package com.astroluna.app.ui.profile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.rounded.VideoCall
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.astroluna.app.R
import com.astroluna.app.ui.theme.CosmicAppTheme

// --- Visual Constants ---
private val ColorPrimary = Color(0xFF673AB7) // Deep Purple
private val ColorSecondary = Color(0xFF9575CD) // Lighter Purple
private val ColorBackground = Color(0xFFF7F9FC)
private val ColorSurface = Color(0xFFFFFFFF)
private val ColorTextPrimary = Color(0xFF1A1C1E)
private val ColorTextSecondary = Color(0xFF757575)
private val ColorDivider = Color(0xFFEEEEEE)
private val ColorCall = Color(0xFF43A047)
private val ColorVideo = Color(0xFFE53935)
private val ColorChat = Color(0xFF039BE5)
private val ColorGold = Color(0xFFFFC107)

class AstrologerProfileActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val astroName = intent.getStringExtra("astro_name") ?: "Astrologer"
        val astroExp = intent.getStringExtra("astro_exp") ?: "5"
        val astroSkills = intent.getStringExtra("astro_skills") ?: "Vedic, Tarot"
        val astroId = intent.getStringExtra("astro_id") ?: ""
        val astroImage = intent.getStringExtra("astro_image") ?: ""
        val astroPrice = intent.getIntExtra("astro_price", 15)
        val isChatOnline = intent.getBooleanExtra("is_chat_online", false)
        val isAudioOnline = intent.getBooleanExtra("is_audio_online", false)
        val isVideoOnline = intent.getBooleanExtra("is_video_online", false)

        setContent {
            CosmicAppTheme {
                AstrologerProfileScreen(
                    id = astroId,
                    name = astroName,
                    exp = astroExp,
                    skills = astroSkills,
                    image = astroImage,
                    price = astroPrice,
                    isChatOnline = isChatOnline,
                    isAudioOnline = isAudioOnline,
                    isVideoOnline = isVideoOnline,
                    onBack = { finish() },
                    onAction = { type ->
                        val intent = android.content.Intent(this, com.astroluna.app.ui.intake.IntakeActivity::class.java).apply {
                            putExtra("partnerId", astroId)
                            putExtra("partnerName", astroName)
                            putExtra("partnerImage", astroImage)
                            putExtra("type", type)
                        }
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AstrologerProfileScreen(
    id: String,
    name: String,
    exp: String,
    skills: String,
    image: String,
    price: Int,
    isChatOnline: Boolean,
    isAudioOnline: Boolean,
    isVideoOnline: Boolean,
    onBack: () -> Unit,
    onAction: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = ColorBackground,
        topBar = {
            // Using a transparent top bar over the profile header
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Surface(shape = CircleShape, color = Color.Black.copy(alpha=0.3f)) {
                            Icon(Icons.Default.ArrowBack, "Back", tint = Color.White, modifier = Modifier.padding(8.dp))
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                         Surface(shape = CircleShape, color = Color.Black.copy(alpha=0.3f)) {
                            Icon(Icons.Default.Share, "Share", tint = Color.White, modifier = Modifier.padding(8.dp))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(ColorBackground)
        ) {
            // New Header Design
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                 // Background Gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(ColorPrimary, ColorSecondary)
                            )
                        )
                ) {
                     // Decor
                     Box(modifier = Modifier.size(200.dp).offset(x = (-50).dp, y = (-50).dp).background(Color.White.copy(alpha=0.1f), CircleShape))
                     Box(modifier = Modifier.size(150.dp).align(Alignment.BottomEnd).offset(x = 50.dp, y = 50.dp).background(Color.White.copy(alpha=0.1f), CircleShape))
                }

                // Profile Image & Info
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .shadow(8.dp, CircleShape, spotColor = Color.Black.copy(alpha=0.5f))
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(4.dp, Color.White, CircleShape)
                    ) {
                         Image(
                            painter = painterResource(id = R.drawable.ic_person_placeholder),
                            contentDescription = "Avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = name,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(skills, color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            // Content Body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-30).dp) // Overlap header
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(ColorBackground)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Key Stats Row
                Card(
                     shape = RoundedCornerShape(16.dp),
                     colors = CardDefaults.cardColors(containerColor = ColorSurface),
                     elevation = CardDefaults.cardElevation(2.dp),
                     modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem(icon = Icons.Default.CheckCircle, value = "$exp Years", label = "Experience")
                        StatDivider()
                        StatItem(icon = Icons.Default.Call, value = "30k+", label = "Consultations")
                        StatDivider()
                        StatItem(icon = Icons.Default.Star, value = "4.9", label = "Rating")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Pricing
                Text(
                     text = "₹$price/min",
                     style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                     color = ColorPrimary
                )
                Text(
                    text = "Consultation Charge",
                    style = MaterialTheme.typography.labelMedium,
                    color = ColorTextSecondary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Bio
                Text(
                    text = "About Me",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.fillMaxWidth(),
                    color = ColorTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$name is an expert in Vedic Astrology and Tarot Reading with over $exp years of experience. She specializes in relationship and career counseling.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ColorTextSecondary,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Action Buttons
                Text(
                    text = "Connect Now",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.fillMaxWidth(),
                    color = ColorTextPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (isChatOnline) {
                        ActionButton(
                            icon = Icons.Default.Chat,
                            label = "Chat",
                            color = ColorChat,
                            modifier = Modifier.weight(1f),
                            onClick = { onAction("chat") }
                        )
                    }
                    if (isAudioOnline) {
                         ActionButton(
                            icon = Icons.Default.Call,
                            label = "Call",
                            color = ColorCall,
                             modifier = Modifier.weight(1f),
                            onClick = { onAction("call") }
                        )
                    }
                    if (isVideoOnline) {
                         ActionButton(
                            icon = Icons.Rounded.VideoCall,
                            label = "Video",
                            color = ColorVideo,
                             modifier = Modifier.weight(1f),
                            onClick = { onAction("video") }
                        )
                    }

                    if(!isChatOnline && !isAudioOnline && !isVideoOnline) {
                         Text("Astrologer is currently offline.", style = MaterialTheme.typography.bodyMedium, color = ColorTextSecondary)
                    }
                }
            }
        }
     }
}

@Composable
fun StatDivide() {
    Divider(modifier = Modifier.height(40.dp).width(1.dp), color = ColorDivider)
}
@Composable
fun StatDivider() {
    Box(modifier = Modifier.height(40.dp).width(1.dp).background(ColorDivider))
}

@Composable
fun StatItem(icon: ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 8.dp)) {
        Icon(icon, null, tint = ColorPrimary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.height(4.dp))
        Text(value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = ColorTextPrimary)
        Text(label, style = MaterialTheme.typography.labelSmall, color = ColorTextSecondary)
    }
}

@Composable
fun ActionButton(icon: ImageVector, label: String, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f)),
        contentPadding = PaddingValues(0.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(label, color = color, fontWeight = FontWeight.Bold)
        }
    }
}
