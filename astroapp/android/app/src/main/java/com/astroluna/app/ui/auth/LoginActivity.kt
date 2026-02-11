package com.astroluna.app.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import com.astroluna.app.ui.auth.OtpVerificationActivity
import com.astroluna.app.data.repository.AuthRepository
import com.astroluna.app.ui.theme.CosmicAppTheme
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.Brightness4
import com.astroluna.app.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CosmicAppTheme {
                LoginScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen() {
    val context = LocalContext.current
    val repository = remember { AuthRepository() }
    val scope = rememberCoroutineScope()

    var phoneNumber by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    val canSubmit = phoneNumber.length == 10 && !isLoading

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F0C29),
                        Color(0xFF302B63),
                        Color(0xFF24243E)
                    )
                )
            )
    ) {
        StarField()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Top Logo/Icon (Circular Star)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .background(Color(0xFFDED2FF), CircleShape)
            ) {
                // Using a Column/Box to draw stars if no specific drawable
                Icon(
                    painter = painterResource(id = R.drawable.ic_star_filled),
                    contentDescription = null,
                    tint = Color(0xFF7B42F6),
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Astro Luna",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1A1C2E),
                letterSpacing = 0.5.sp
            )

            Text(
                text = "Your celestial guide awaits",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 2. Main Login Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp, RoundedCornerShape(32.dp), clip = false),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Welcome Back",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )

                    Text(
                        text = "Connect with your cosmic destiny",
                        fontSize = 15.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
                    )

                    // Mobile Number Input
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "MOBILE NUMBER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Gray.copy(alpha = 0.7f),
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = {
                                val digits = it.filter { ch -> ch.isDigit() }
                                phoneNumber = digits.take(10)
                                if (showError && phoneNumber.length == 10) showError = false
                            },
                            prefix = { Text("+91  ", fontWeight = FontWeight.Medium, color = Color.Gray) },
                            placeholder = { Text("98765 43210", color = Color.LightGray) },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            isError = showError && phoneNumber.length != 10,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color(0xFFF3F4F9),
                                unfocusedContainerColor = Color(0xFFF3F4F9),
                                errorContainerColor = Color(0xFFFFEBEE)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            if (phoneNumber.length != 10) {
                                showError = true
                                return@Button
                            }
                            isLoading = true
                            scope.launch {
                                try {
                                    val result = repository.sendOtp(phoneNumber.trim())
                                    if (result.isSuccess) {
                                        val intent = Intent(context, OtpVerificationActivity::class.java)
                                        intent.putExtra("phone", phoneNumber.trim())
                                        context.startActivity(intent)
                                        (context as? AppCompatActivity)?.finish()
                                    } else {
                                        showError = true
                                    }
                                } catch (e: Exception) {
                                    showError = true
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(8.dp, RoundedCornerShape(16.dp), clip = false),
                        enabled = canSubmit,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent, // Managed by Box with Brush
                            disabledContainerColor = Color.LightGray.copy(alpha = 0.3f),
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(Color(0xFF7446F6), Color(0xFF4B80F9))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Get Secret Key", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(20.dp), tint = Color.White)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Divider(modifier = Modifier.weight(1f), color = Color.LightGray.copy(alpha = 0.5f))
                        Text(
                            "OR CONTINUE WITH",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            fontSize = 12.sp,
                            color = Color.Gray,
                            letterSpacing = 0.5.sp
                        )
                        Divider(modifier = Modifier.weight(1f), color = Color.LightGray.copy(alpha = 0.5f))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Social Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SocialLoginButton(modifier = Modifier.weight(1f)) // Google placeholder
                        SocialLoginButton(modifier = Modifier.weight(1f), isApple = true) // Apple placeholder
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Footer
            Row {
                Text("New to the cosmic path? ", color = Color.Gray, fontSize = 14.sp)
                Text("Join us", color = Color(0xFF7B42F6), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.padding(bottom = 40.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text("TERMS", fontSize = 12.sp, color = Color.Gray, letterSpacing = 1.sp)
                Text("PRIVACY", fontSize = 12.sp, color = Color.Gray, letterSpacing = 1.sp)
                Text("CONTACT", fontSize = 12.sp, color = Color.Gray, letterSpacing = 1.sp)
            }
        }

        // 3. Floating Dark Mode Toggle
        FloatingActionButton(
            onClick = { /* Toggle theme */ },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = Color.White,
            contentColor = Color(0xFF1A1C2E),
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
        ) {
            Icon(Icons.Default.Brightness4, contentDescription = "Toggle Dark Mode")
        }
    }
}

@Composable
fun SocialLoginButton(modifier: Modifier = Modifier, isApple: Boolean = false) {
    Surface(
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF3F4F9),
        onClick = { }
    ) {
        Box(contentAlignment = Alignment.Center) {
             // Placeholders for icons
             if (isApple) {
                 Text("", fontSize = 24.sp, color = Color.Black)
             } else {
                 Text("G", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
             }
        }
    }
}
@Composable
fun StarField() {
    val stars = remember { List(40) { Triple(Math.random().toFloat(), Math.random().toFloat(), Math.random().toFloat()) } }
    val infiniteTransition = rememberInfiniteTransition(label = "StarAnim")
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Reverse),
        label = "StarAlpha"
    )

    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
        stars.forEachIndexed { index, (x, y, starSize) ->
            val phase = (index % 10) / 10f
            val baseAlpha = (animProgress + phase) % 1f
            drawCircle(
                color = Color.White,
                radius = 1.5.dp.toPx() * (starSize + 0.2f),
                center = androidx.compose.ui.geometry.Offset(x * size.width, y * size.height),
                alpha = baseAlpha * 0.4f
            )
        }
    }
}
