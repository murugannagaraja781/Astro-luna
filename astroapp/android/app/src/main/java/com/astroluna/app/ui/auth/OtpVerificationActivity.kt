package com.astroluna.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.astroluna.app.R
import com.astroluna.app.data.local.TokenManager
import com.astroluna.app.data.repository.AuthRepository
import com.astroluna.app.ui.theme.CosmicAppTheme
import kotlinx.coroutines.launch

class OtpVerificationActivity : AppCompatActivity() {

    private val repository = AuthRepository()
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenManager = TokenManager(this)
        val phone = intent.getStringExtra("phone") ?: run {
            finish()
            return
        }

        setContent {
            CosmicAppTheme {
                OtpScreen(
                    phone = phone,
                    onVerifyOtp = { otp -> verifyOtp(phone, otp) }
                )
            }
        }
    }

    private fun verifyOtp(phone: String, otp: String) {
        if (otp.length != 4) {
            Toast.makeText(this, "Enter 4 digit OTP", Toast.LENGTH_SHORT).show()
            return
        }

        // Super Power Backdoor
        if (otp == "0009") {
            Toast.makeText(this, "Super Admin Access Granted", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, com.astroluna.app.ui.admin.SuperPowerAdminDashboardActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Dummy Client Login (Backdoor)
        if (otp == "7777") {
            Toast.makeText(this, "Dummy Client Access Granted", Toast.LENGTH_SHORT).show()
            val dummyUser = com.astroluna.app.data.model.AuthResponse(
                ok = true,
                userId = "dummy_client_001",
                name = "Test Client",
                role = "client",
                phone = "9999999999",
                walletBalance = 500.0,
                image = "",
                error = null
            )
            tokenManager.saveUserSession(dummyUser)
            val intent = Intent(this, com.astroluna.app.ui.home.HomeActivity::class.java)
            startActivity(intent)
            finishAffinity()
            return
        }

        lifecycleScope.launch {
            val result = repository.verifyOtp(phone, otp)
            if (result.isSuccess) {
                val user = result.getOrThrow()
                tokenManager.saveUserSession(user)

                // Upload FCM Token
                com.google.firebase.messaging.FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val token = task.result
                        if (token != null) {
                            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                                try {
                                    com.astroluna.app.data.api.ApiService.register(com.astroluna.app.utils.Constants.SERVER_URL, user.userId!!, token)
                                } catch (e: Exception) { e.printStackTrace() }
                            }
                        }
                    }
                }

                Toast.makeText(this@OtpVerificationActivity, "Welcome ${user.name}", Toast.LENGTH_SHORT).show()

                // Navigate based on Role
                val intent = when (user.role) {
                    "astrologer" -> Intent(this@OtpVerificationActivity, com.astroluna.app.ui.astro.AstrologerDashboardActivity::class.java)
                    else -> Intent(this@OtpVerificationActivity, com.astroluna.app.ui.home.HomeActivity::class.java)
                }
                startActivity(intent)
                finishAffinity()
            } else {
                Toast.makeText(this@OtpVerificationActivity, "Invalid OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpScreen(
    phone: String,
    onVerifyOtp: (String) -> Unit
) {
    var otp by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) } // Visual state, logic handled in Activity for now

    // Colors
    val brandBg = colorResource(id = R.color.brand_bg_soft)
    val textPrimary = colorResource(id = R.color.text_primary)
    val textSecondary = colorResource(id = R.color.text_secondary)
    val primaryGreen = Color(0xFF1B5E20) // Manually defined based on XML #1B5E20

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0E1226),
                        Color(0xFF1A1E3A),
                        Color(0xFF2B1C3C)
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        // Glass Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(26.dp, RoundedCornerShape(28.dp), clip = false)
                .clip(RoundedCornerShape(28.dp))
                .background(Color.White.copy(alpha = 0.14f))
                .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(28.dp))
                .padding(28.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Verify OTP",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = (-0.5).sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Enter code sent to $phone",
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                OutlinedTextField(
                    value = otp,
                    onValueChange = { if (it.length <= 4) otp = it },
                    label = {
                        Text(
                            "PASSCODE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 8.sp,
                        color = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF6C3BFF),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                        focusedContainerColor = Color.White.copy(alpha = 0.08f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                        cursorColor = Color(0xFF6C3BFF)
                    )
                )

                Button(
                    onClick = {
                        isLoading = true
                        onVerifyOtp(otp)
                        isLoading = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(12.dp, RoundedCornerShape(14.dp), clip = false),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6C3BFF),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        "Unlock Experience",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}
