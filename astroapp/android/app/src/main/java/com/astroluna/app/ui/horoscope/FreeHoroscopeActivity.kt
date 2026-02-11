package com.astroluna.app.ui.horoscope

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.astroluna.app.data.api.ApiClient
import com.astroluna.app.ui.theme.CosmicAppTheme
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import java.util.*

// --- Visual Constants for Consistency (Matching HomeScreen) ---
private val CornerRadiusLarge = 24.dp
private val CornerRadiusMedium = 16.dp
private val CornerRadiusSmall = 12.dp
private val PaddingScreen = 16.dp
private val SpacingSection = 24.dp

// Premium Colors
private val ColorSurface = Color(0xFFFFFFFF)
private val ColorBackground = Color(0xFFF7F9FC)
private val ColorPrimary = Color(0xFF673AB7) // Deep Purple
private val ColorTextPrimary = Color(0xFF1A1C1E)
private val ColorTextSecondary = Color(0xFF757575)
private val ColorAccent = Color(0xFF2E7D32)
private val ColorDivider = Color(0xFFEEEEEE)

class FreeHoroscopeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CosmicAppTheme {
                FreeHoroscopeScreen(
                    onBackClick = { finish() },
                    onGenerateChart = { data -> generateRasiChart(data) }
                )
            }
        }
    }

    private fun generateRasiChart(data: BirthData) {
        lifecycleScope.launch {
            try {
                // Prepare payload
                val payload = JsonObject().apply {
                    addProperty("name", data.name)
                    addProperty("dob", data.dob) // DD/MM/YYYY
                    addProperty("time", data.time) // HH:MM
                    addProperty("country", data.country)
                    addProperty("state", data.state)
                    addProperty("city", data.city)
                    addProperty("birthPlace", data.birthPlace)
                    addProperty("timezone", data.timezone)
                    addProperty("latitude", data.latitude)
                    addProperty("longitude", data.longitude)
                }

                // Call API to generate chart
                val response = ApiClient.api.generateRasiChart(payload)

                if (response.isSuccessful && response.body()?.get("ok")?.asBoolean == true) {
                    val chartData = response.body()?.get("chart")?.asJsonObject
                    runOnUiThread {
                        Toast.makeText(this@FreeHoroscopeActivity, "Chart Generated Successfully!", Toast.LENGTH_SHORT).show()
                        // TODO: Navigate to chart display screen with chartData
                         finish()
                    }
                } else {
                    val error = response.body()?.get("error")?.asString ?: "Failed to generate chart. Please check inputs."
                    runOnUiThread {
                        Toast.makeText(this@FreeHoroscopeActivity, error, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@FreeHoroscopeActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

data class BirthData(
    val name: String,
    val dob: String,
    val time: String,
    val country: String,
    val state: String,
    val city: String,
    val birthPlace: String,
    val timezone: String,
    val latitude: Double,
    val longitude: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreeHoroscopeScreen(
    onBackClick: () -> Unit,
    onGenerateChart: (BirthData) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var birthPlace by remember { mutableStateOf("") }
    var timezone by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Date Picker
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            dob = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Time Picker
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            time = String.format("%02d:%02d", hourOfDay, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    Scaffold(
        containerColor = ColorBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Free Horoscope",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = ColorTextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = ColorTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = ColorBackground
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = PaddingScreen, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(SpacingSection)
        ) {
            // Header Section
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Unlock Your Destiny",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp),
                    color = ColorTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Enter your precise birth details to generate an accurate Vedic Rasi chart and predictions.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ColorTextSecondary
                )
            }

            // Form Card
            Card(
                colors = CardDefaults.cardColors(containerColor = ColorSurface),
                shape = RoundedCornerShape(CornerRadiusMedium),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ColorDivider),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Personal Information Only Group
                    SectionHeader("Personal Details")

                    PremiumTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Full Name",
                        placeholder = "e.g. John Doe",
                        icon = null // No icon for name to keep it clean, or Person icon
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f).clickable { datePickerDialog.show() }) {
                             PremiumTextField(
                                value = dob,
                                onValueChange = {},
                                label = "Date of Birth",
                                placeholder = "DD/MM/YYYY",
                                icon = Icons.Default.CalendarToday,
                                readOnly = true,
                                enabled = false // Handle click via Box
                            )
                        }
                        Box(modifier = Modifier.weight(1f).clickable { timePickerDialog.show() }) {
                            PremiumTextField(
                                value = time,
                                onValueChange = {},
                                label = "Time of Birth",
                                placeholder = "HH:MM",
                                icon = Icons.Default.AccessTime,
                                readOnly = true,
                                enabled = false
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = ColorDivider)
                    Spacer(modifier = Modifier.height(8.dp))

                    SectionHeader("Birth Place")

                    PremiumTextField(
                        value = birthPlace,
                        onValueChange = { birthPlace = it },
                        label = "Place of Birth",
                        placeholder = "Hospital / Locality",
                        icon = Icons.Default.LocationOn
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        PremiumTextField(value = city, onValueChange = { city = it }, label = "City", modifier = Modifier.weight(1f))
                        PremiumTextField(value = state, onValueChange = { state = it }, label = "State", modifier = Modifier.weight(1f))
                    }

                    PremiumTextField(
                        value = country,
                        onValueChange = { country = it },
                        label = "Country",
                        placeholder = "India"
                    )

                    // Expandable / Advanced (Could be hidden, but keeping visible for now as per requirements)
                    // Grouping less common fields
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Advanced Coordinates (Optional)", style = MaterialTheme.typography.labelSmall, color = ColorTextSecondary)

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        PremiumTextField(value = latitude, onValueChange = { latitude = it }, label = "Latitude", keyboardType = KeyboardType.Decimal, modifier = Modifier.weight(1f))
                        PremiumTextField(value = longitude, onValueChange = { longitude = it }, label = "Longitude", keyboardType = KeyboardType.Decimal, modifier = Modifier.weight(1f))
                    }

                    PremiumTextField(
                        value = timezone,
                        onValueChange = { timezone = it },
                        label = "Timezone",
                        placeholder = "Asia/Kolkata"
                    )
                }
            }

            // Action Button
            Button(
                onClick = {
                    if (validateInputs(name, dob, time, country, state, city, birthPlace, timezone, latitude, longitude)) {
                        isLoading = true
                        val birthData = BirthData(
                            name = name,
                            dob = dob,
                            time = time,
                            country = country,
                            state = state,
                            city = city,
                            birthPlace = birthPlace,
                            timezone = timezone,
                            latitude = latitude.toDoubleOrNull() ?: 0.0,
                            longitude = longitude.toDoubleOrNull() ?: 0.0
                        )
                        onGenerateChart(birthData)
                    } else {
                         Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(50), spotColor = ColorPrimary.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorPrimary,
                    contentColor = Color.White
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Generate Horoscope",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = ColorPrimary),
    )
}

@Composable
fun PremiumTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    readOnly: Boolean = false,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.bodySmall) },
        placeholder = { Text(placeholder, style = MaterialTheme.typography.bodySmall, color = Color.Gray.copy(alpha = 0.5f)) },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = ColorTextPrimary),
        shape = RoundedCornerShape(CornerRadiusSmall),
        trailingIcon = if (icon != null) {
            { Icon(icon, contentDescription = null, tint = ColorTextSecondary, modifier = Modifier.size(20.dp)) }
        } else null,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            keyboardType = keyboardType,
            imeAction = ImeAction.Next
        ),
        readOnly = readOnly,
        enabled = enabled,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ColorPrimary,
            unfocusedBorderColor = ColorDivider,
            focusedLabelColor = ColorPrimary,
            unfocusedLabelColor = ColorTextSecondary,
            cursorColor = ColorPrimary,
            focusedContainerColor = ColorSurface,
            unfocusedContainerColor = ColorSurface,
            disabledContainerColor = ColorSurface, // Make it look active even if pseudo-disabled for click
            disabledBorderColor = ColorDivider,
            disabledTextColor = ColorTextPrimary,
            disabledLabelColor = ColorTextSecondary
        )
    )
}

private fun validateInputs(
    name: String,
    dob: String,
    time: String,
    country: String,
    state: String,
    city: String,
    birthPlace: String,
    timezone: String,
    latitude: String,
    longitude: String
): Boolean {
    // Latitude/Longitude optional if simplified, but business logic requires them?
    // The previous code required them. We'll keep strict validation for now but user might want auto-fetch later.
    return name.isNotBlank() &&
            dob.isNotBlank() &&
            time.isNotBlank() &&
            country.isNotBlank() &&
            state.isNotBlank() &&
            city.isNotBlank() &&
            birthPlace.isNotBlank() &&
            timezone.isNotBlank()
            // Simplified validation to prevent crashes on partial inputs if user is typing
}
