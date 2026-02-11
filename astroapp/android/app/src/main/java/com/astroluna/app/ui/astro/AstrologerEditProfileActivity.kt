package com.astroluna.app.ui.astro

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.astroluna.app.data.local.TokenManager
import com.astroluna.app.data.remote.SocketManager
import com.astroluna.app.ui.theme.CosmicAppTheme
import com.astroluna.app.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

// --- Visual Constants ---
private val CornerRadiusMedium = 16.dp
private val ColorSurface = Color(0xFFFFFFFF)
private val ColorBackground = Color(0xFFF7F9FC)
private val ColorPrimary = Color(0xFF673AB7)
private val ColorTextPrimary = Color(0xFF1A1C1E)
private val ColorTextSecondary = Color(0xFF757575)
private val ColorDivider = Color(0xFFEEEEEE)

class AstrologerEditProfileActivity : ComponentActivity() {
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenManager = TokenManager(this)

        setContent {
            CosmicAppTheme {
                AstrologerEditProfileScreen(
                    tokenManager = tokenManager,
                    onBack = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AstrologerEditProfileScreen(
    tokenManager: TokenManager,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val session = tokenManager.getUserSession()

    // Form States
    var name by remember { mutableStateOf(session?.name ?: "") }
    var imageUrl by remember { mutableStateOf(session?.image ?: "") }
    var skills by remember { mutableStateOf(session?.skills?.joinToString(", ") ?: "") }
    var languages by remember { mutableStateOf(session?.languages?.joinToString(", ") ?: "") }
    var price by remember { mutableStateOf(session?.price?.toString() ?: "15") }
    var experience by remember { mutableStateOf(session?.experience?.toString() ?: "0") }

    var isUploading by remember { mutableStateOf(false) }

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            isUploading = true
            uploadImage(context, it) { success, url ->
                isUploading = false
                if (success && url != null) {
                    imageUrl = url
                    Toast.makeText(context, "Photo Uploaded!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Upload Failed", Toast.LENGTH_SHORT).show()
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
                        "Edit Astrologer Profile",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = ColorTextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = ColorTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = ColorBackground)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Profile Photo
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(ColorSurface)
                        .border(3.dp, ColorPrimary.copy(alpha = 0.2f), CircleShape)
                        .clickable { pickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null, modifier = Modifier.size(50.dp), tint = ColorTextSecondary)
                    if (isUploading) {
                        Box(Modifier.fillMaxSize().background(Color.Black.copy(0.3f)), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
                        }
                    }
                }
                Surface(
                    shape = CircleShape,
                    color = ColorPrimary,
                    modifier = Modifier.size(30.dp).border(2.dp, ColorSurface, CircleShape)
                ) {
                    Icon(Icons.Default.CameraAlt, null, tint = Color.White, modifier = Modifier.padding(6.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Form Fields
            Card(
                shape = RoundedCornerShape(CornerRadiusMedium),
                colors = CardDefaults.cardColors(containerColor = ColorSurface),
                border = BorderStroke(1.dp, ColorDivider),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    EditField(label = "Display Name", value = name, onValueChange = { name = it }, icon = Icons.Default.Person)
                    EditField(label = "Skills (e.g. Vedic, Tarot)", value = skills, onValueChange = { skills = it }, icon = Icons.Default.FlashOn)
                    EditField(label = "Languages (e.g. Hindi, Tamil)", value = languages, onValueChange = { languages = it }, icon = Icons.Default.Language)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(Modifier.weight(1f)) {
                            EditField(label = "Price (₹/min)", value = price, onValueChange = { price = it }, icon = Icons.Default.CurrencyRupee, keyboardType = KeyboardType.Number)
                        }
                        Box(Modifier.weight(1f)) {
                            EditField(label = "Experience (Yrs)", value = experience, onValueChange = { experience = it }, icon = Icons.Default.School, keyboardType = KeyboardType.Number)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Save Button
            Button(
                onClick = {
                    val updates = JSONObject().apply {
                        put("name", name)
                        put("image", imageUrl)
                        put("skills", JSONArray(skills.split(",").map { it.trim() }.filter { it.isNotEmpty() }))
                        put("languages", JSONArray(languages.split(",").map { it.trim() }.filter { it.isNotEmpty() }))
                        put("price", price.toIntOrNull() ?: 15)
                        put("experience", experience.toIntOrNull() ?: 0)
                    }

                    SocketManager.updateProfile(updates) { res ->
                        if (res?.optBoolean("ok") == true) {
                            // Update local session
                            session?.let {
                                val updated = it.copy(
                                    name = name,
                                    image = imageUrl,
                                    skills = skills.split(",").map { s -> s.trim() }.filter { s -> s.isNotEmpty() },
                                    languages = languages.split(",").map { s -> s.trim() }.filter { s -> s.isNotEmpty() },
                                    price = price.toIntOrNull() ?: 15,
                                    experience = experience.toIntOrNull() ?: 0
                                )
                                tokenManager.saveUserSession(updated)
                            }
                            scope.launch(Dispatchers.Main) {
                                Toast.makeText(context, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show()
                                onBack()
                            }
                        } else {
                            scope.launch(Dispatchers.Main) {
                                Toast.makeText(context, "Failed to update profile", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp).shadow(8.dp, RoundedCornerShape(50), spotColor = ColorPrimary.copy(alpha = 0.4f)),
                colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary),
                shape = RoundedCornerShape(50)
            ) {
                Text("Update Profile", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun EditField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        leadingIcon = { Icon(icon, null, tint = ColorTextSecondary, modifier = Modifier.size(20.dp)) },
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ColorPrimary,
            unfocusedBorderColor = ColorDivider,
            focusedContainerColor = ColorBackground,
            unfocusedContainerColor = ColorBackground
        )
    )
}

private fun uploadImage(context: android.content.Context, uri: Uri, callback: (Boolean, String?) -> Unit) {
    val client = OkHttpClient()
    val file = getFileFromUri(context, uri) ?: return callback(false, null)

    val requestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("file", file.name, RequestBody.create("image/*".toMediaTypeOrNull(), file))
        .build()

    val request = Request.Builder()
        .url("${Constants.SERVER_URL}/upload")
        .post(requestBody)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: java.io.IOException) = callback(false, null)
        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                val json = JSONObject(response.body?.string() ?: "{}")
                if (json.optBoolean("ok")) callback(true, json.optString("url")) else callback(false, null)
            } else callback(false, null)
        }
    })
}

private fun getFileFromUri(context: android.content.Context, uri: Uri): File? {
    val inputStream = context.contentResolver.openInputStream(uri) ?: return null
    val file = File(context.cacheDir, "temp_astro_pic.jpg")
    val outputStream = FileOutputStream(file)
    inputStream.copyTo(outputStream)
    outputStream.close()
    inputStream.close()
    return file
}
