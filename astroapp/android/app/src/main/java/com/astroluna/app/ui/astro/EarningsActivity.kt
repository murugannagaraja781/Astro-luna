package com.astroluna.app.ui.astro

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.astroluna.app.data.local.TokenManager
import com.astroluna.app.ui.theme.CosmicAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class EarningsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tokenManager = TokenManager(this)
        val session = tokenManager.getUserSession()
        val userId = session?.userId ?: ""
        val initialBalance = session?.walletBalance ?: 0.0

        setContent {
            CosmicAppTheme {
                EarningsScreen(
                    userId = userId,
                    initialBalance = initialBalance,
                    onBack = { finish() }
                )
            }
        }
    }
}

@Composable
fun EarningsScreen(userId: String, initialBalance: Double, onBack: () -> Unit) {
    val context = LocalContext.current
    var walletBalance by remember { mutableDoubleStateOf(initialBalance) }
    var transactions by remember { mutableStateOf<List<TransactionItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        scope.launch {
            // Fetch Latest Balance
            withContext(Dispatchers.IO) {
                try {
                    val client = okhttp3.OkHttpClient()
                    val request = okhttp3.Request.Builder()
                        .url("https://astroluna.in/api/user/$userId")
                        .build()
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        val json = JSONObject(response.body?.string() ?: "{}")
                        walletBalance = json.optDouble("walletBalance", initialBalance)
                    }
                } catch (e: Exception) { e.printStackTrace() }
            }

            // Fetch History for Transactions
            withContext(Dispatchers.IO) {
                try {
                    val client = okhttp3.OkHttpClient()
                    val request = okhttp3.Request.Builder()
                        .url("https://astroluna.in/api/astrology/history/$userId")
                        .build()
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        val body = response.body?.string()
                        val json = JSONObject(body ?: "{}")
                        if (json.optBoolean("ok")) {
                            val array = json.optJSONArray("sessions") ?: JSONArray()
                            val list = mutableListOf<TransactionItem>()
                            for (i in 0 until array.length()) {
                                val obj = array.getJSONObject(i)
                                list.add(
                                    TransactionItem(
                                        title = "Consultation with ${obj.optString("clientName", "Client")}",
                                        subtitle = obj.optString("type", "call").replaceFirstChar { it.uppercase() },
                                        amount = obj.optDouble("totalEarned", 0.0),
                                        time = obj.optLong("actualBillingStart", 0),
                                        status = "COMPLETED",
                                        type = obj.optString("type", "call")
                                    )
                                )
                            }
                            transactions = list.sortedByDescending { it.time }
                        }
                    }
                } catch (e: Exception) { e.printStackTrace() }
                finally {
                    isLoading = false
                }
            }
        }
    }

    var showWithdrawDialog by remember { mutableStateOf(false) }
    var withdrawalAmount by remember { mutableStateOf("") }

    if (showWithdrawDialog) {
        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            title = { Text("Withdraw Funds", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Available Balance: ₹${String.format("%,.2f", walletBalance)}", color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = withdrawalAmount,
                        onValueChange = { withdrawalAmount = it },
                        label = { Text("Enter Amount") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = withdrawalAmount.toDoubleOrNull() ?: 0.0
                        if (amt > 0 && amt <= walletBalance) {
                            // Logic placeholder
                            Toast.makeText(context, "Withdrawal request of ₹$amt sent!", Toast.LENGTH_LONG).show()
                            showWithdrawDialog = false
                        } else {
                            Toast.makeText(context, "Invalid Amount", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B42F6))
                ) {
                    Text("Withdraw")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }

    Scaffold(
        topBar = {
            EarningsHeader(onBack = onBack)
        },
        bottomBar = {
            EarningsBottomNav()
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF8F9FE)),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                BalanceCard(balance = walletBalance, onWithdraw = { showWithdrawDialog = true })
            }

            // 2. Monthly Earnings Chart Placeholder
            item {
                MonthlyEarningsSection()
            }

            // 3. Stats Rows
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "THIS MONTH",
                        amount = "₹${(walletBalance * 0.3).toInt()}", // Dummy calc
                        change = "+12%",
                        isPositive = true
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "LAST MONTH",
                        amount = "₹${(walletBalance * 0.25).toInt()}",
                        change = "",
                        isPositive = false
                    )
                }
            }

            // 4. Transactions List
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Recent Transactions", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF111827))
                    Text("View History", color = Color(0xFF7B42F6), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            if (isLoading) {
                item {
                    Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF7B42F6))
                    }
                }
            } else {
                items(transactions.take(10)) { transaction ->
                    TransactionListItem(transaction)
                }
            }
        }
    }
}

@Composable
fun EarningsHeader(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF7B42F6), Color(0xFF6C3BFF))
                )
            )
            .padding(top = 12.dp, bottom = 40.dp, start = 16.dp, end = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
            }

            Text(
                text = "Earnings",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color.White)
            )

            IconButton(
                onClick = { },
                modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(Icons.Rounded.Help, null, tint = Color.White)
            }
        }
    }
}

@Composable
fun BalanceCard(balance: Double, onWithdraw: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-30).dp)
            .shadow(16.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Total Balance", color = Color.Gray, fontSize = 16.sp)
            Text(
                text = "₹${String.format("%,.0f", balance)}",
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF111827),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Button(
                onClick = onWithdraw,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCC00))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.AccountBalanceWallet, null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Withdraw Funds", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun MonthlyEarningsSection() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Monthly Earnings", fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(16.dp))

            // Simple Line Chart Drawing
            Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val path = Path().apply {
                        moveTo(0f, size.height * 0.7f)
                        cubicTo(
                            size.width * 0.2f, size.height * 0.8f,
                            size.width * 0.3f, size.height * 0.3f,
                            size.width * 0.5f, size.height * 0.4f
                        )
                        cubicTo(
                            size.width * 0.7f, size.height * 0.6f,
                            size.width * 0.8f, size.height * 0.1f,
                            size.width, size.height * 0.4f
                        )
                    }
                    drawPath(
                        path = path,
                        color = Color(0xFF7B42F6),
                        style = Stroke(width = 4.dp.toPx())
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("JAN", "FEB", "MAR", "APR", "MAY", "JUN").forEach { month ->
                    Text(
                        text = month,
                        fontSize = 11.sp,
                        color = if (month == "APR") Color(0xFF7B42F6) else Color.LightGray,
                        fontWeight = if (month == "APR") FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(modifier: Modifier, label: String, amount: String, change: String, isPositive: Boolean) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if(label.contains("THIS")) Color(0xFF21D0B2) else Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(amount, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                if (change.isNotEmpty()) {
                    Text(
                        text = " $change",
                        fontSize = 11.sp,
                        color = if (isPositive) Color(0xFF21D0B2) else Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionListItem(item: TransactionItem) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).background(Color(0xFF7B42F6).copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when(item.type.lowercase()){
                        "chat" -> Icons.Default.Chat
                        "video" -> Icons.Default.VideoCall
                        else -> Icons.Default.Call
                    },
                    contentDescription = null,
                    tint = Color(0xFF7B42F6),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 15.sp)
                Text(dateFormat.format(Date(item.time)), color = Color.Gray, fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("₹${item.amount.toInt()}", fontWeight = FontWeight.ExtraBold, color = Color.Black, fontSize = 16.sp)
                Surface(
                    color = if(item.status == "COMPLETED") Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = item.status,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if(item.status == "COMPLETED") Color(0xFF2E7D32) else Color(0xFFEF6C00)
                    )
                }
            }
        }
    }
}

@Composable
fun EarningsBottomNav() {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("Home") },
            selected = false,
            onClick = { }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Chat, null) },
            label = { Text("Chat") },
            selected = false,
            onClick = { }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.VideoCall, null) },
            label = { Text("Video") },
            selected = false,
            onClick = { }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.MonetizationOn, null) },
            label = { Text("Earnings") },
            selected = true,
            onClick = { }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, null) },
            label = { Text("Profile") },
            selected = false,
            onClick = { }
        )
    }
}

data class TransactionItem(
    val title: String,
    val subtitle: String,
    val amount: Double,
    val time: Long,
    val status: String,
    val type: String
)
