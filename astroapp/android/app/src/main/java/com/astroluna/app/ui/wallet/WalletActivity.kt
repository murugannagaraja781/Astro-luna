package com.astroluna.app.ui.wallet

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.astroluna.app.data.api.ApiClient
import com.astroluna.app.data.local.TokenManager
import com.astroluna.app.ui.theme.CosmicAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.ArrayList

// --- Visual Constants ---
private val CornerRadiusLarge = 24.dp
private val CornerRadiusMedium = 16.dp
private val CornerRadiusSmall = 12.dp
private val PaddingScreen = 16.dp

// Premium Colors
private val ColorSurface = Color(0xFFFFFFFF)
private val ColorBackground = Color(0xFFF7F9FC)
private val ColorPrimary = Color(0xFF673AB7) // Deep Purple
private val ColorSecondary = Color(0xFF9575CD) // Lighter Purple
private val ColorTextPrimary = Color(0xFF1A1C1E)
private val ColorTextSecondary = Color(0xFF757575)
private val ColorSuccess = Color(0xFF43A047)
private val ColorError = Color(0xFFE53935)
private val ColorDivider = Color(0xFFEEEEEE)

class WalletActivity : ComponentActivity() {

    private lateinit var tokenManager: TokenManager
    private val transactionsState = mutableStateListOf<JSONObject>()
    private var balanceState by mutableDoubleStateOf(0.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenManager = TokenManager(this)

        updateBalanceFromSession()

        setContent {
            CosmicAppTheme {
                WalletScreen(
                    balance = balanceState,
                    transactions = transactionsState,
                    onBackClick = { finish() },
                    onAddMoney = { amount ->
                         if (amount < 1) {
                            Toast.makeText(this, "Enter valid amount", Toast.LENGTH_SHORT).show()
                        } else {
                            val intent = Intent(this, com.astroluna.app.ui.payment.PaymentActivity::class.java)
                            intent.putExtra("amount", amount.toDouble())
                            startActivity(intent)
                        }
                    },
                    onRefreshHistory = { loadPaymentHistory() }
                )
            }
        }

        loadPaymentHistory()
    }

    override fun onResume() {
        super.onResume()
        refreshWalletBalance()
        loadPaymentHistory()

        com.astroluna.app.data.remote.SocketManager.onWalletUpdate { newBalance ->
             runOnUiThread {
                tokenManager.updateWalletBalance(newBalance)
                balanceState = newBalance
            }
        }
    }

    override fun onPause() {
        super.onPause()
        com.astroluna.app.data.remote.SocketManager.off("wallet-update")
    }

    private fun updateBalanceFromSession() {
        val user = tokenManager.getUserSession()
        balanceState = user?.walletBalance ?: 0.0
    }

    private fun refreshWalletBalance() {
        val userId = tokenManager.getUserSession()?.userId ?: return
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = ApiClient.api.getUserProfile(userId)
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    runOnUiThread {
                        tokenManager.saveUserSession(user)
                        balanceState = user.walletBalance ?: 0.0
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadPaymentHistory() {
        val userId = tokenManager.getUserSession()?.userId ?: return

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                     // Used existing URL structure
                    .url("${com.astroluna.app.utils.Constants.SERVER_URL}/api/payment/history/$userId")
                    .get()
                    .build()

                val client = OkHttpClient()
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val body = response.body?.string()
                        val json = JSONObject(body ?: "{}")
                        val data = json.optJSONArray("data")

                        val newTransactions = ArrayList<JSONObject>()
                        if (data != null) {
                            for (i in 0 until data.length()) {
                                newTransactions.add(data.getJSONObject(i))
                            }
                        }

                        runOnUiThread {
                            transactionsState.clear()
                            transactionsState.addAll(newTransactions)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    balance: Double,
    transactions: List<JSONObject>,
    onBackClick: () -> Unit,
    onAddMoney: (Int) -> Unit,
    onRefreshHistory: () -> Unit
) {
    var amountInput by remember { mutableStateOf("") }

    Scaffold(
        containerColor = ColorBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "My Wallet",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = ColorTextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = ColorTextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = onRefreshHistory) {
                        Icon(Icons.Rounded.History, contentDescription = "Refresh", tint = ColorTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = ColorBackground
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = PaddingScreen),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // 1. Premium Wallet Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .shadow(16.dp, RoundedCornerShape(CornerRadiusLarge), spotColor = ColorPrimary.copy(alpha = 0.5f))
                        .clip(RoundedCornerShape(CornerRadiusLarge))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF512DA8), Color(0xFF7E57C2)),
                                start = Offset(0f, 0f),
                                end = Offset(1000f, 1000f)
                            )
                        )
                ) {
                    // Decorative Circles
                    Box(modifier = Modifier.offset(x = (-20).dp, y = (-20).dp).size(150.dp).alpha(0.1f).clip(CircleShape).background(Color.White))
                    Box(modifier = Modifier.align(Alignment.BottomEnd).offset(x = 30.dp, y = 30.dp).size(150.dp).alpha(0.1f).clip(CircleShape).background(Color.White))

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.AccountBalanceWallet, null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Current Balance", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                        }

                        Text(
                            text = "₹ ${"%.2f".format(balance)}",
                            style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Astro Luna Wallet", style = MaterialTheme.typography.titleSmall, color = Color.White.copy(alpha = 0.9f))
                            Surface(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("ACTIVE", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = Color.White)
                            }
                        }
                    }
                }
            }

            // 2. Add Money Section
            item {
                Text("Top Up Wallet", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = ColorTextPrimary)
                Spacer(Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = ColorSurface),
                    shape = RoundedCornerShape(CornerRadiusMedium),
                    border = BorderStroke(1.dp, ColorDivider),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = amountInput,
                            onValueChange = { amountInput = it.filter { c -> c.isDigit() } },
                            label = { Text("Enter Amount") },
                            leadingIcon = { Text("₹", style = MaterialTheme.typography.titleMedium, color = ColorTextSecondary) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(CornerRadiusSmall),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ColorPrimary,
                                unfocusedBorderColor = ColorDivider,
                                focusedContainerColor = ColorSurface,
                                unfocusedContainerColor = ColorSurface
                            )
                        )

                        // Quick Amounts
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val quickAmounts = listOf(100, 500, 1000, 2000)
                            quickAmounts.forEach { amt ->
                                OutlinedButton(
                                    onClick = {
                                        amountInput = amt.toString()
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(CornerRadiusSmall),
                                    contentPadding = PaddingValues(0.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorTextPrimary),
                                    border = BorderStroke(1.dp, ColorDivider)
                                ) {
                                    Text("₹$amt", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }

                        Button(
                            onClick = {
                                val amt = amountInput.toIntOrNull() ?: 0
                                if (amt >= 1) {
                                    onAddMoney(amt)
                                    amountInput = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary)
                        ) {
                            Icon(Icons.Rounded.Add, null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Recharge Now", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 3. Transactions
            item {
                Text("Transaction History", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = ColorTextPrimary)
            }

            items(transactions) { transaction ->
                TransactionItem(transaction)
            }

            if (transactions.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No transactions yet", style = MaterialTheme.typography.bodyMedium, color = ColorTextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: JSONObject) {
    val amount = transaction.optDouble("amount", 0.0)
    val status = transaction.optString("status", "pending")
    val dateStr = transaction.optString("createdAt", "")
    val displayDate = if (dateStr.length > 10) dateStr.substring(0, 10) else dateStr
    val isSuccess = status == "success"

    Card(
        colors = CardDefaults.cardColors(containerColor = ColorSurface),
        shape = RoundedCornerShape(CornerRadiusSmall),
        border = BorderStroke(1.dp, ColorDivider),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isSuccess) ColorSuccess.copy(alpha = 0.1f) else ColorError.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
               Text(
                   if(isSuccess) "✓" else "!",
                   color = if(isSuccess) ColorSuccess else ColorError,
                   fontWeight = FontWeight.Bold
               )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if(isSuccess) "Recharge Successful" else "Transaction Failed",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = ColorTextPrimary
                )
                Text(
                    text = displayDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = ColorTextSecondary
                )
            }

            Text(
                text = "+ ₹${amount.toInt()}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = if(isSuccess) ColorSuccess else ColorError
            )
        }
    }
}
