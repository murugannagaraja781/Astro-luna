package com.astroluna.app.ui.rasipalan

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.astroluna.app.data.api.ApiClient
import com.astroluna.app.data.model.RasipalanItem
import com.astroluna.app.ui.theme.CosmicAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// --- Visual Constants ---
private val CornerRadiusLarge = 24.dp
private val CornerRadiusMedium = 16.dp
private val CornerRadiusSmall = 12.dp
private val PaddingScreen = 16.dp

// Premium Colors (Standardized)
private val ColorSurface = Color(0xFFFFFFFF)
private val ColorBackground = Color(0xFFF7F9FC)
private val ColorPrimary = Color(0xFF673AB7) // Deep Purple
private val ColorTextPrimary = Color(0xFF1A1C1E)
private val ColorTextSecondary = Color(0xFF757575)
private val ColorDivider = Color(0xFFEEEEEE)
private val ColorGold = Color(0xFFFFC107) // Gold for stars/accents
private val ColorSuccess = Color(0xFF43A047)
private val ColorError = Color(0xFFE53935)
private val ColorWarning = Color(0xFFFFA000)

class RasipalanActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val signId = intent.getIntExtra("signId", -1)
        val signName = intent.getStringExtra("signName") ?: "Daily Horoscope"

        setContent {
            CosmicAppTheme {
                RasipalanScreen(
                    targetSignId = signId,
                    displayTitle = signName,
                    onBack = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RasipalanScreen(targetSignId: Int, displayTitle: String, onBack: () -> Unit) {
    var dataList by remember { mutableStateOf<List<RasipalanItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val response = withContext(Dispatchers.IO) {
                ApiClient.api.getRasipalan()
            }
            if (response.isSuccessful && response.body() != null) {
                val fullList = response.body()!!
                dataList = if (targetSignId != -1) {
                    fullList.filter { it.signId == targetSignId }
                } else {
                    fullList
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        containerColor = ColorBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = displayTitle,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = ColorTextPrimary
                        )
                        Text(
                            text = "Daily Insights",
                            style = MaterialTheme.typography.labelSmall,
                            color = ColorTextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = ColorTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                     containerColor = ColorBackground,
                     scrolledContainerColor = ColorSurface
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = ColorPrimary
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp, start = PaddingScreen, end = PaddingScreen),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    items(dataList) { item ->
                        PremiumRasipalanCard(item)
                    }

                    if (dataList.isEmpty()) {
                        item {
                           Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                               Text("Horoscope data currently unavailable.", color = ColorTextSecondary)
                           }
                        }
                    } else {
                         item {
                            Text(
                                text = "More Predictions",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = ColorTextPrimary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        // Locked Sections
                        item { ComingSoonCard("Weekly Forecast") }
                        item { ComingSoonCard("Monthly Forecast") }
                        item { ComingSoonCard("Yearly Overview") }
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumRasipalanCard(item: RasipalanItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadiusMedium),
        colors = CardDefaults.cardColors(containerColor = ColorSurface),
        border = BorderStroke(1.dp, ColorDivider),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                         text = item.signNameEn ?: item.signNameTa ?: "Horoscope",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = ColorTextPrimary
                    )
                     Text(
                        text = item.date ?: "Today",
                        style = MaterialTheme.typography.bodySmall,
                        color = ColorTextSecondary
                    )
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = ColorPrimary.copy(alpha = 0.1f),
                    contentColor = ColorPrimary
                ) {
                    Icon(Icons.Default.Star, null, tint = ColorPrimary, modifier = Modifier.padding(8.dp).size(20.dp))
                }
            }

            Divider(color = ColorDivider)

            // Prediction
            Text(
                text = item.prediction?.ta ?: item.prediction?.en ?: "No prediction available.",
                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
                color = ColorTextPrimary
            )

            // Stats
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                StatusIndicatorRow("Career / தொழில்", item.details?.career)
                StatusIndicatorRow("Finance / நிதி", item.details?.finance)
                StatusIndicatorRow("Health / ஆரோக்கியம்", item.details?.health)
            }

            Surface(
                color = ColorBackground,
                shape = RoundedCornerShape(CornerRadiusSmall)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    LuckyStat("Lucky Number", item.lucky?.number ?: "-")
                    LuckyStat("Lucky Color", item.lucky?.color?.en ?: "-")
                }
            }
        }
    }
}

@Composable
fun StatusIndicatorRow(label: String, status: String?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = ColorTextSecondary)
        StatusChip(status ?: "Moderate")
    }
}

@Composable
fun StatusChip(status: String) {
    val (color, label) = when {
        status.contains("Good", ignoreCase = true) ||
        status.contains("High", ignoreCase = true) ||
        status.contains("Excellent", ignoreCase = true) -> ColorSuccess to status

        status.contains("Weak", ignoreCase = true) ||
        status.contains("Low", ignoreCase = true) ||
        status.contains("Bad", ignoreCase = true) -> ColorError to status

        else -> ColorWarning to status
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = color
        )
    }
}

@Composable
fun LuckyStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = ColorTextSecondary)
        Text(text = value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = ColorTextPrimary)
    }
}

@Composable
fun ComingSoonCard(title: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadiusMedium),
        colors = CardDefaults.cardColors(containerColor = ColorSurface.copy(alpha=0.6f)),
        border = BorderStroke(1.dp, ColorDivider)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = ColorTextSecondary)
                Text(text = "Premium Feature", style = MaterialTheme.typography.labelSmall, color = ColorTextSecondary.copy(alpha = 0.7f))
            }
            Icon(Icons.Default.Lock, contentDescription = null, tint = ColorTextSecondary, modifier = Modifier.size(20.dp))
        }
    }
}
