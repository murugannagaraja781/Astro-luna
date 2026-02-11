package com.astroluna.app.ui.intake

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.astroluna.app.data.local.TokenManager
import com.astroluna.app.data.remote.SocketManager
import com.astroluna.app.ui.chat.ChatActivity
import com.astroluna.app.ui.theme.CosmicAppTheme
import io.socket.client.Socket
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.abs
import kotlin.math.roundToInt

// --- Visual Constants (Consistent with Home/Horoscope) ---
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

class IntakeActivity : ComponentActivity() {

    private var partnerId: String? = null
    private var type: String? = null
    private var partnerName: String? = null
    private var partnerImage: String? = null
    private var isEditMode = false
    private var existingData: JSONObject? = null
    private var targetUserId: String? = null

    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tokenManager = TokenManager(this)

        partnerId = intent.getStringExtra("partnerId")
        type = intent.getStringExtra("type")
        partnerName = intent.getStringExtra("partnerName") ?: "Astrologer"
        partnerImage = intent.getStringExtra("partnerImage")
        isEditMode = intent.getBooleanExtra("isEditMode", false)
        targetUserId = intent.getStringExtra("targetUserId")

        val dataStr = intent.getStringExtra("existingData")
        if (dataStr != null) {
            try { existingData = JSONObject(dataStr) } catch(e: Exception){}
        }

        setContent {
            CosmicAppTheme {
                IntakeScreen(
                    partnerId = partnerId,
                    partnerName = partnerName!!,
                    partnerImage = partnerImage,
                    callType = type,
                    isEditMode = isEditMode,
                    existingData = existingData,
                    targetUserId = targetUserId,
                    tokenManager = tokenManager,
                    onClose = { finish() },
                    onSessionConnected = { sessionId, callType ->
                        navigateToSession(sessionId, callType)
                    },
                    onUnanswered = {
                        Toast.makeText(this, "Astrologer is busy. Please try again later.", Toast.LENGTH_LONG).show()
                        finish()
                    }
                )
            }
        }
    }

    private fun navigateToSession(sessionId: String, type: String) {
        if (type == "chat") {
            val intent = Intent(this, ChatActivity::class.java).apply {
                putExtra("sessionId", sessionId)
                putExtra("toUserId", partnerId)
                putExtra("toUserName", partnerName)
                putExtra("toUserImage", partnerImage)
            }
            startActivity(intent)
        } else {
            val intent = Intent(this, com.astroluna.app.ui.call.CallActivity::class.java).apply {
                putExtra("sessionId", sessionId)
                putExtra("partnerId", partnerId)
                putExtra("partnerName", partnerName)
                putExtra("isInitiator", true)
                putExtra("callType", type)
            }
            startActivity(intent)
        }
        finish()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntakeScreen(
    partnerId: String?,
    partnerName: String,
    partnerImage: String?,
    callType: String?,
    isEditMode: Boolean,
    existingData: JSONObject?,
    targetUserId: String?,
    tokenManager: TokenManager,
    onClose: () -> Unit,
    onSessionConnected: (String, String) -> Unit,
    onUnanswered: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Form State
    var name by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") } // Male, Female

    // Date
    var day by remember { mutableStateOf("") }
    var month by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }

    // Time
    var hour by remember { mutableStateOf("") }
    var minute by remember { mutableStateOf("") }
    var amPm by remember { mutableStateOf("AM") } // AM or PM
    var unknownTime by remember { mutableStateOf(false) }

    // Place
    var countryName by remember { mutableStateOf("") }
    var stateName by remember { mutableStateOf("") }
    var cityName by remember { mutableStateOf("") }
    var timezoneId by remember { mutableStateOf<String?>(null) }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    var timezone by remember { mutableStateOf<Double?>(null) }

    // Additional
    var occupation by remember { mutableStateOf("") }
    var maritalStatus by remember { mutableStateOf("Single") }
    var topic by remember { mutableStateOf("Career / Job") }

    // Partner
    var includePartner by remember { mutableStateOf(false) }
    var pName by remember { mutableStateOf("") }
    var pCountryName by remember { mutableStateOf("") }
    var pStateName by remember { mutableStateOf("") }
    var pCityName by remember { mutableStateOf("") }
    var pLat by remember { mutableStateOf<Double?>(null) }
    var pLon by remember { mutableStateOf<Double?>(null) }
    var pTz by remember { mutableStateOf<Double?>(null) }
    var pTimezoneId by remember { mutableStateOf<String?>(null) }
    var pDay by remember { mutableStateOf("") }
    var pMonth by remember { mutableStateOf("") }
    var pYear by remember { mutableStateOf("") }
    var pHour by remember { mutableStateOf("") }
    var pMinute by remember { mutableStateOf("") }
    var pAmPm by remember { mutableStateOf("AM") }

    // Logic State
    var isWaiting by remember { mutableStateOf(false) }
    var waitTimeLeft by remember { mutableStateOf(30) }
    var waitingSessionId by remember { mutableStateOf<String?>(null) }

    // State to track which city field triggered search
    var activeCitySearchTarget by remember { mutableStateOf("client") } // "client" or "partner"

    val specificCityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
         if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val d = result.data!!
            val fullName = d.getStringExtra("name") ?: ""
            val cityRes = d.getStringExtra("city") ?: ""
            val stateRes = d.getStringExtra("state") ?: ""
            val countryRes = d.getStringExtra("country") ?: ""
            val tzId = d.getStringExtra("timezoneId")
            val latRes = d.getDoubleExtra("lat", 0.0)
            val lonRes = d.getDoubleExtra("lon", 0.0)

            val parsed = if (cityRes.isBlank() && stateRes.isBlank() && countryRes.isBlank()) {
                parsePlaceName(fullName)
            } else {
                Triple(cityRes, stateRes, countryRes)
            }
            val resolvedCity = parsed.first
            val resolvedState = parsed.second
            val resolvedCountry = parsed.third

            if (activeCitySearchTarget == "client") {
                cityName = resolvedCity
                stateName = resolvedState
                countryName = resolvedCountry
                timezoneId = tzId?.takeIf { it.isNotBlank() }
                latitude = latRes
                longitude = lonRes
                val computed = computeTimezoneOffsetHours(timezoneId, day, month, year, hour, minute)
                if (computed != null) timezone = computed
            } else {
                pCityName = resolvedCity
                pStateName = resolvedState
                pCountryName = resolvedCountry
                pTimezoneId = tzId?.takeIf { it.isNotBlank() }
                pLat = latRes
                pLon = lonRes
                val computed = computeTimezoneOffsetHours(pTimezoneId, pDay, pMonth, pYear, pHour, pMinute)
                if (computed != null) pTz = computed
            }
         }
    }

    val placeName = remember(cityName, stateName, countryName) {
        buildPlaceName(cityName, stateName, countryName)
    }

    val computedTimezone = remember(timezoneId, day, month, year, hour, minute) {
        computeTimezoneOffsetHours(timezoneId, day, month, year, hour, minute)
    }
    val timezoneOffset = computedTimezone ?: timezone
    val timezoneDisplay = timezoneOffset?.let { formatUtcOffset(it) } ?: ""

    val pPlaceName = remember(pCityName, pStateName, pCountryName) {
        buildPlaceName(pCityName, pStateName, pCountryName)
    }

    val partnerComputedTimezone = remember(pTimezoneId, pDay, pMonth, pYear, pHour, pMinute) {
        computeTimezoneOffsetHours(pTimezoneId, pDay, pMonth, pYear, pHour, pMinute)
    }
    val partnerTimezoneOffset = partnerComputedTimezone ?: pTz
    val partnerTimezoneDisplay = partnerTimezoneOffset?.let { formatUtcOffset(it) } ?: ""

    val launchLocationPicker = {
        activeCitySearchTarget = "client"
        val intent = Intent(context, com.astroluna.app.ui.city.CitySearchActivity::class.java)
        specificCityLauncher.launch(intent)
    }

    val launchPartnerLocationPicker = {
        activeCitySearchTarget = "partner"
        val intent = Intent(context, com.astroluna.app.ui.city.CitySearchActivity::class.java)
        specificCityLauncher.launch(intent)
    }

    // Prefill
    LaunchedEffect(Unit) {
        if (existingData != null) {
            val d = existingData!!
            name = d.optString("name")
            val placeRaw = d.optString("city")
            val parsed = parsePlaceName(placeRaw)
            cityName = parsed.first
            stateName = d.optString("state", parsed.second)
            countryName = d.optString("country", parsed.third)
            latitude = d.optDouble("latitude", 0.0).takeIf { it != 0.0 }
            longitude = d.optDouble("longitude", 0.0).takeIf { it != 0.0 }
            timezone = d.optDouble("timezone", 5.5)
            timezoneId = d.optString("timezoneId").takeIf { it.isNotBlank() }

            day = d.optInt("day", 0).toString().takeIf { it != "0" } ?: ""
            month = d.optInt("month", 0).toString().takeIf { it != "0" } ?: ""
            year = d.optInt("year", 0).toString().takeIf { it != "0" } ?: ""
            hour = d.optInt("hour", 0).toString()
            minute = d.optInt("minute", 0).toString()

            gender = d.optString("gender", "Male")
            maritalStatus = d.optString("maritalStatus", "Single")
            occupation = d.optString("occupation", "")
            topic = d.optString("topic", "General")

            val pd = d.optJSONObject("partner")
            if (pd != null) {
                includePartner = true
                pName = pd.optString("name")
                val pPlaceRaw = pd.optString("city")
                val pParsed = parsePlaceName(pPlaceRaw)
                pCityName = pParsed.first
                pStateName = pd.optString("state", pParsed.second)
                pCountryName = pd.optString("country", pParsed.third)
                pDay = pd.optInt("day").toString()
                pMonth = pd.optInt("month").toString()
                pYear = pd.optInt("year").toString()
                pHour = pd.optInt("hour").toString()
                pMinute = pd.optInt("minute").toString()
                pLat = pd.optDouble("latitude", 0.0)
                pLon = pd.optDouble("longitude", 0.0)
                pTz = pd.optDouble("timezone", 5.5)
                pTimezoneId = pd.optString("timezoneId").takeIf { it.isNotBlank() }
            }
        } else {
            // Load Defaults
            val prefs = context.getSharedPreferences("AstroIntakeDefaults", Context.MODE_PRIVATE)
            name = prefs.getString("name", "") ?: ""
            val storedCity = prefs.getString("city", "") ?: ""
            val storedState = prefs.getString("state", "") ?: ""
            val storedCountry = prefs.getString("country", "") ?: ""
            if (storedCity.isBlank() && storedState.isBlank() && storedCountry.isBlank()) {
                val storedPlace = prefs.getString("place", "") ?: ""
                val parsed = parsePlaceName(storedPlace)
                cityName = parsed.first
                stateName = parsed.second
                countryName = parsed.third
            } else {
                cityName = storedCity
                stateName = storedState
                countryName = storedCountry
            }
            latitude = prefs.getFloat("latitude", 0f).toDouble().takeIf { it != 0.0 }
            longitude = prefs.getFloat("longitude", 0f).toDouble().takeIf { it != 0.0 }
            timezone = prefs.getFloat("timezone", 5.5f).toDouble()
            timezoneId = prefs.getString("timezoneId", null)
            day = prefs.getInt("day", 0).toString().takeIf { it != "0" } ?: ""
            month = prefs.getInt("month", 0).toString().takeIf { it != "0" } ?: ""
            year = prefs.getInt("year", 0).toString().takeIf { it != "0" } ?: ""
            hour = prefs.getInt("hour", 0).toString()
            minute = prefs.getInt("minute", 0).toString()
            gender = prefs.getString("gender", "Male") ?: "Male"
            occupation = prefs.getString("occupation", "") ?: ""
            maritalStatus = prefs.getString("maritalStatus", "Single") ?: "Single"
            topic = prefs.getString("topic", "General") ?: "General"
        }

        if (callType == "match") {
            includePartner = true
        }
    }

    // Waiting Timer
    LaunchedEffect(isWaiting) {
        if (isWaiting) {
            waitTimeLeft = 30
            while(waitTimeLeft > 0) {
                delay(1000)
                waitTimeLeft--
            }
            if (isWaiting) {
                isWaiting = false
                onUnanswered()
            }
        }
    }

    // Socket Listener for Wait
    DisposableEffect(Unit) {
        val socket = SocketManager.getSocket()
        val listener: (Array<Any>) -> Unit = { args ->
            val data = args[0] as JSONObject
            val accepted = data.optBoolean("accept", false)
            if (isWaiting) {
                if (accepted) {
                    isWaiting = false
                    val sid = waitingSessionId ?: ""
                    onSessionConnected(sid, callType ?: "chat")
                } else {
                     isWaiting = false
                     // Rejected
                     scope.launch { Toast.makeText(context, "Request Rejected by Astrologer", Toast.LENGTH_LONG).show() }
                     onClose()
                }
            }
        }

        socket?.on("session-answered", listener)

        onDispose {
            socket?.off("session-answered", listener)
        }
    }

    fun validateInput(
        d: String, m: String, y: String, h: String, min: String,
        label: String
    ): Boolean {
        val dayInt = d.toIntOrNull() ?: 0
        val monthInt = m.toIntOrNull() ?: 0
        val yearInt = y.toIntOrNull() ?: 0
        val hourInt = h.toIntOrNull() ?: -1
        val minuteInt = min.toIntOrNull() ?: -1

        if (dayInt < 1 || dayInt > 31) {
            Toast.makeText(context, "$label: Invalid Day (1-31)", Toast.LENGTH_SHORT).show()
            return false
        }
        if (monthInt < 1 || monthInt > 12) {
            Toast.makeText(context, "$label: Invalid Month (1-12)", Toast.LENGTH_SHORT).show()
            return false
        }
        if (yearInt < 1900 || yearInt > 2100) {
            Toast.makeText(context, "$label: Invalid Year (1900-2100)", Toast.LENGTH_SHORT).show()
            return false
        }
        if (hourInt < 1 || hourInt > 12) {
            Toast.makeText(context, "$label: Invalid Hour (1-12)", Toast.LENGTH_SHORT).show()
            return false
        }
        if (minuteInt < 0 || minuteInt > 59) {
            Toast.makeText(context, "$label: Invalid Minute (0-59)", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    fun convertTo24Hour(hStr: String, amPm: String): Int {
        val h = hStr.toIntOrNull() ?: 0
        return if (amPm == "PM" && h < 12) h + 12
        else if (amPm == "AM" && h == 12) 0
        else h
    }

    fun submit() {
        if (name.isBlank() || cityName.isBlank() || day.isBlank() || month.isBlank() || year.isBlank()) {
            Toast.makeText(context, "Please fill required fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (!validateInput(day, month, year, hour, minute, "Personal DOB")) return

        val hour24 = convertTo24Hour(hour, amPm)
        val pHour24 = convertTo24Hour(pHour, pAmPm)

        // Validation for Match - Partner details required
        if (callType == "match" || includePartner) {
            if (pName.isBlank()) {
                Toast.makeText(context, "Partner name required", Toast.LENGTH_SHORT).show()
                return
            }
            if (!validateInput(pDay, pMonth, pYear, pHour, pMinute, "Partner DOB")) return
            if (pCityName.isBlank()) {
                Toast.makeText(context, "Partner place required", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val finalTimezone = computeTimezoneOffsetHours(timezoneId, day, month, year, hour24.toString(), minute) ?: timezone ?: 5.5
        val finalPartnerTimezone = computeTimezoneOffsetHours(pTimezoneId, pDay, pMonth, pYear, pHour24.toString(), pMinute) ?: pTz ?: 5.5

        // Save Defaults
        val prefs = context.getSharedPreferences("AstroIntakeDefaults", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("name", name)
            putString("place", placeName)
            putString("city", cityName)
            putString("state", stateName)
            putString("country", countryName)
            putInt("day", day.toIntOrNull() ?: 0)
            putInt("month", month.toIntOrNull() ?: 0)
            putInt("year", year.toIntOrNull() ?: 0)
            putInt("hour", hour24)
            putInt("minute", minute.toIntOrNull() ?: 0)
            putString("gender", gender)
            putString("occupation", occupation)
            putString("maritalStatus", maritalStatus)
            putString("topic", topic)
            if (latitude != null) putFloat("latitude", latitude!!.toFloat())
            if (longitude != null) putFloat("longitude", longitude!!.toFloat())
            putFloat("timezone", finalTimezone.toFloat())
            if (!timezoneId.isNullOrBlank()) {
                putString("timezoneId", timezoneId)
            } else {
                remove("timezoneId")
            }
            apply()
        }

        var partnerData: JSONObject? = null
        if (includePartner) {
            partnerData = JSONObject().apply {
                 put("name", pName)
                 put("day", pDay.toIntOrNull() ?: 0)
                 put("month", pMonth.toIntOrNull() ?: 0)
                 put("year", pYear.toIntOrNull() ?: 0)
                 put("hour", pHour24)
                 put("minute", pMinute.toIntOrNull() ?: 0)
                 put("city", pPlaceName)
                 put("state", pStateName)
                 put("country", pCountryName)
                 put("latitude", pLat ?: latitude ?: 13.0827)
                 put("longitude", pLon ?: longitude ?: 80.2707)
                 put("timezone", finalPartnerTimezone)
                 if (!pTimezoneId.isNullOrBlank()) put("timezoneId", pTimezoneId)
                 put("gender", if (gender == "Male") "Female" else "Male")
            }
        }

        val birthData = JSONObject().apply {
            put("name", name)
            put("gender", gender)
            put("day", day.toIntOrNull() ?: 0)
            put("month", month.toIntOrNull() ?: 0)
            put("year", year.toIntOrNull() ?: 0)
            put("hour", hour24)
            put("minute", minute.toIntOrNull() ?: 0)
            put("city", placeName)
            put("state", stateName)
            put("country", countryName)
            put("latitude", latitude)
            put("longitude", longitude)
            put("timezone", finalTimezone)
            if (!timezoneId.isNullOrBlank()) put("timezoneId", timezoneId)
            put("maritalStatus", maritalStatus)
            put("occupation", occupation)
            put("topic", topic)
            if (partnerData != null) put("partner", partnerData)
        }

        // Save to API
        val userId = targetUserId ?: tokenManager.getUserSession()?.userId
        if (userId != null) {
              val payload = JSONObject().apply {
                  put("userId", userId)
                  put("intakeData", birthData)
              }
              scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                  try {
                       val gsonReq = com.google.gson.JsonParser.parseString(payload.toString()).asJsonObject
                       com.astroluna.app.data.api.ApiClient.api.saveUserIntake(gsonReq)
                  } catch(e: Exception) { e.printStackTrace() }
              }
        }

        if (isEditMode) {
             val intent = Intent()
             intent.putExtra("birthData", birthData.toString())
             (context as? Activity)?.setResult(Activity.RESULT_OK, intent)
             onClose()
        } else if (callType == "free_horoscope") {
             val intent = Intent(context, com.astroluna.app.ui.chart.VipChartActivity::class.java).apply {
                 putExtra("birthData", birthData.toString())
             }
             context.startActivity(intent)
             (context as? Activity)?.finish()
        } else if (callType == "match") {
             val intent = Intent(context, com.astroluna.app.ui.chart.MatchDisplayActivity::class.java).apply {
                 putExtra("birthData", birthData.toString())
             }
             context.startActivity(intent)
             (context as? Activity)?.finish()
        } else {
            // Initiate Session
             if (partnerId != null && callType != null) {
                 SocketManager.init()
                 SocketManager.requestSession(partnerId, callType, birthData) { response ->
                     if (response?.optBoolean("ok") == true) {
                         waitingSessionId = response.optString("sessionId")
                         scope.launch { isWaiting = true }
                     } else {
                         scope.launch {
                             Toast.makeText(context, response?.optString("error") ?: "Failed", Toast.LENGTH_SHORT).show()
                         }
                     }
                 }
             }
        }
    }

    Scaffold(
        containerColor = ColorBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if(isEditMode) "Edit Details" else "New Consultation",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = ColorTextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = PaddingScreen, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(SpacingSection)
        ) {

            // Personal Details Card
            Card(
                colors = CardDefaults.cardColors(containerColor = ColorSurface),
                shape = RoundedCornerShape(CornerRadiusMedium),
                border = androidx.compose.foundation.BorderStroke(1.dp, ColorDivider),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    SectionHeader("Personal Details")

                    PremiumTextField(value = name, onValueChange = { name = it }, label = "Full Name")

                    // Gender
                    Column {
                        Text("Gender", style = MaterialTheme.typography.labelMedium, color = ColorTextSecondary)
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            GenderOption(label = "Male", selected = gender == "Male", onClick = { gender = "Male" })
                            GenderOption(label = "Female", selected = gender == "Female", onClick = { gender = "Female" })
                        }
                    }

                    // Date
                    SectionHeader("Date of Birth")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PremiumTextField(value = day, onValueChange = { if(it.length<=2) day=it }, label = "Day", modifier = Modifier.weight(1f), keyboardType = KeyboardType.Number)
                        PremiumTextField(value = month, onValueChange = { if(it.length<=2) month=it }, label = "Month", modifier = Modifier.weight(1f), keyboardType = KeyboardType.Number)
                        PremiumTextField(value = year, onValueChange = { if(it.length<=4) year=it }, label = "Year", modifier = Modifier.weight(1.5f), keyboardType = KeyboardType.Number)
                    }

                    // Time
                    SectionHeader("Time of Birth")
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PremiumTextField(value = hour, onValueChange = { if(it.length<=2) hour=it }, label = "Hour", modifier = Modifier.weight(1f), keyboardType = KeyboardType.Number)
                        Text(":", style = MaterialTheme.typography.titleLarge, color = ColorTextSecondary)
                        PremiumTextField(value = minute, onValueChange = { if(it.length<=2) minute=it }, label = "Min", modifier = Modifier.weight(1f), keyboardType = KeyboardType.Number)

                        AMPMToggle(selected = amPm, onSelect = { amPm = it }, modifier = Modifier.weight(1.5f))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = unknownTime,
                            onCheckedChange = { unknownTime = it },
                            colors = CheckboxDefaults.colors(checkedColor = ColorPrimary)
                        )
                        Text("Don't know exact time", style = MaterialTheme.typography.bodySmall, color = ColorTextPrimary)
                    }

                    // Place
                    SectionHeader("Birth Place")
                    PremiumTextField(
                        value = cityName, onValueChange = {}, label = "City",
                        readOnly = true, enabled = false,
                        icon = Icons.Default.LocationOn,
                        modifier = Modifier.clickable { launchLocationPicker() }
                    )
                    if (stateName.isNotBlank() || countryName.isNotBlank()) {
                         Text("$stateName, $countryName", style = MaterialTheme.typography.bodySmall, color = ColorTextSecondary)
                    }
                     if (timezoneDisplay.isNotBlank()) {
                         Text("Timezone: $timezoneDisplay", style = MaterialTheme.typography.bodySmall, color = ColorTextSecondary)
                    }

                    SectionHeader("Metadata")
                    PremiumTextField(value = occupation, onValueChange = { occupation = it }, label = "Occupation (Optional)")
                    SpinnerDropdown(label = "Marital Status", selected = maritalStatus, items = listOf("Single", "Married", "Divorced", "Widowed"), onSelect = { maritalStatus = it })
                    SpinnerDropdown(label = "Topic", selected = topic, items = listOf("Career / Job", "Marriage / Relationship", "Health", "Finance", "Legal", "General"), onSelect = { topic = it })
                }
            }

            // Partner Section
            if (callType != "match") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = includePartner, onCheckedChange = { includePartner = it }, colors = CheckboxDefaults.colors(checkedColor = ColorPrimary))
                    Text("Include Details of Partner?", style = MaterialTheme.typography.titleMedium, color = ColorTextPrimary)
                }
            }

            if (includePartner || callType == "match") {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ColorSurface),
                    shape = RoundedCornerShape(CornerRadiusMedium),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ColorDivider),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        SectionHeader("Partner Details")
                        PremiumTextField(value = pName, onValueChange = { pName = it }, label = "Partner Name")

                        SectionHeader("Partner DOB")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            PremiumTextField(value = pDay, onValueChange = { if(it.length<=2) pDay=it }, label = "DD", modifier = Modifier.weight(1f), keyboardType = KeyboardType.Number)
                            PremiumTextField(value = pMonth, onValueChange = { if(it.length<=2) pMonth=it }, label = "MM", modifier = Modifier.weight(1f), keyboardType = KeyboardType.Number)
                            PremiumTextField(value = pYear, onValueChange = { if(it.length<=4) pYear=it }, label = "YYYY", modifier = Modifier.weight(1.5f), keyboardType = KeyboardType.Number)
                        }

                        SectionHeader("Partner Time")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            PremiumTextField(value = pHour, onValueChange = { if(it.length<=2) pHour=it }, label = "HH", modifier = Modifier.weight(1f), keyboardType = KeyboardType.Number)
                            Text(":", style = MaterialTheme.typography.titleLarge, color = ColorTextSecondary)
                            PremiumTextField(value = pMinute, onValueChange = { if(it.length<=2) pMinute=it }, label = "MM", modifier = Modifier.weight(1f), keyboardType = KeyboardType.Number)
                            AMPMToggle(selected = pAmPm, onSelect = { pAmPm = it }, modifier = Modifier.weight(1.5f))
                        }

                        SectionHeader("Partner Place")
                        PremiumTextField(
                            value = pCityName, onValueChange = {}, label = "Partner City",
                            readOnly = true, enabled = false,
                            icon = Icons.Default.LocationOn,
                            modifier = Modifier.clickable { launchPartnerLocationPicker() }
                        )
                        if (pStateName.isNotBlank() || pCountryName.isNotBlank()) {
                            Text("$pStateName, $pCountryName", style = MaterialTheme.typography.bodySmall, color = ColorTextSecondary)
                        }
                    }
                }
            }

            // CTAs
            Button(
                onClick = { submit() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(50), spotColor = ColorPrimary.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary)
            ) {
                Text(
                     if (isEditMode) "Update Details" else "Begin Consultation",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(Modifier.height(32.dp))
        }

        // Waiting Dialog
        if (isWaiting) {
            Dialog(onDismissRequest = {}) {
                Card(
                     shape = RoundedCornerShape(CornerRadiusLarge),
                     colors = CardDefaults.cardColors(containerColor = ColorSurface),
                     modifier = Modifier.padding(16.dp).fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = ColorPrimary, strokeWidth = 5.dp, modifier = Modifier.size(50.dp))
                        Spacer(Modifier.height(20.dp))
                        Text("Connecting with $partnerName...", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Spacer(Modifier.height(8.dp))
                        Text("Please wait while we set up the secure line.", style = MaterialTheme.typography.bodyMedium, color = ColorTextSecondary, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(24.dp))
                        Text("${waitTimeLeft}s", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold, color = ColorPrimary))
                        Spacer(Modifier.height(24.dp))
                        OutlinedButton(
                            onClick = { isWaiting = false },
                            modifier = Modifier.fillMaxWidth(),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red.copy(alpha=0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                        ) {
                            Text("Cancel")
                        }
                    }
                }
            }
        }
    }
}

// --- Helper Components ---

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = ColorPrimary),
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun GenderOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if(selected) ColorPrimary else Color.Transparent,
        border = if(selected) null else androidx.compose.foundation.BorderStroke(1.dp, ColorDivider),
        modifier = Modifier.height(36.dp).clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(label, color = if(selected) Color.White else ColorTextSecondary, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
        }
    }
}

@Composable
fun AMPMToggle(selected: String, onSelect: (String) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(CornerRadiusSmall))
            .border(1.dp, ColorDivider, RoundedCornerShape(CornerRadiusSmall))
            .background(ColorSurface)
    ) {
        Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable{ onSelect("AM") }.background(if(selected=="AM") ColorPrimary else Color.Transparent), contentAlignment = Alignment.Center) {
            Text("AM", color = if(selected=="AM") Color.White else ColorTextSecondary, fontWeight = FontWeight.Bold)
        }
        Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(ColorDivider))
        Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable{ onSelect("PM") }.background(if(selected=="PM") ColorPrimary else Color.Transparent), contentAlignment = Alignment.Center) {
            Text("PM", color = if(selected=="PM") Color.White else ColorTextSecondary, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
        label = { Text(label) },
        placeholder = { Text(placeholder) },
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

@Composable
fun SpinnerDropdown(
    label: String,
    selected: String,
    items: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = ColorTextSecondary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Box {
            Surface(
                shape = RoundedCornerShape(CornerRadiusSmall),
                border = androidx.compose.foundation.BorderStroke(1.dp, ColorDivider),
                color = ColorSurface,
                modifier = Modifier.fillMaxWidth().height(56.dp).clickable { expanded = true }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(selected, style = MaterialTheme.typography.bodyMedium, color = ColorTextPrimary)
                    Icon(Icons.Default.ArrowDropDown, null, tint = ColorTextSecondary)
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(ColorSurface)
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item, color = ColorTextPrimary) },
                        onClick = {
                            onSelect(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}


private fun buildPlaceName(city: String, state: String, country: String): String {
    return listOf(city, state, country).filter { it.isNotBlank() }.joinToString(", ")
}

private fun parsePlaceName(place: String): Triple<String, String, String> {
    if (place.isBlank()) return Triple("", "", "")
    val parts = place.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    val city = parts.getOrNull(0) ?: ""
    val state = parts.getOrNull(1) ?: ""
    val country = parts.getOrNull(2) ?: ""
    return Triple(city, state, country)
}

private fun computeTimezoneOffsetHours(
    timezoneId: String?,
    day: String,
    month: String,
    year: String,
    hour: String,
    minute: String
): Double? {
    if (timezoneId.isNullOrBlank()) return null
    val tz = TimeZone.getTimeZone(timezoneId)
    if (tz.id == "GMT" && timezoneId != "GMT" && timezoneId != "UTC") return null

    val dayInt = day.toIntOrNull()
    val monthInt = month.toIntOrNull()
    val yearInt = year.toIntOrNull()
    val hourInt = hour.toIntOrNull() ?: 0
    val minuteInt = minute.toIntOrNull() ?: 0

    val offsetMillis = if (dayInt != null && monthInt != null && yearInt != null) {
        val cal = Calendar.getInstance(tz).apply {
            set(Calendar.YEAR, yearInt)
            set(Calendar.MONTH, (monthInt - 1).coerceIn(0, 11))
            set(Calendar.DAY_OF_MONTH, dayInt.coerceIn(1, 31))
            set(Calendar.HOUR_OF_DAY, hourInt.coerceIn(0, 23))
            set(Calendar.MINUTE, minuteInt.coerceIn(0, 59))
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        tz.getOffset(cal.timeInMillis)
    } else {
        tz.rawOffset
    }

    return offsetMillis / 3600000.0
}

private fun formatUtcOffset(offsetHours: Double): String {
    val totalMinutes = (offsetHours * 60).roundToInt()
    val sign = if (totalMinutes >= 0) "+" else "-"
    val absMinutes = abs(totalMinutes)
    val hours = absMinutes / 60
    val minutes = absMinutes % 60
    return "UTC$sign${"%02d".format(hours)}:${"%02d".format(minutes)}"
}
