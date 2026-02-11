package com.astroluna.app.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Chat
import androidx.compose.material.icons.rounded.VideoCall
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.animation.core.*
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.saveable.rememberSaveable
import com.astroluna.app.utils.Localization
import com.astroluna.app.data.model.Astrologer
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import com.astroluna.app.R
import com.astroluna.app.ui.theme.*
import com.astroluna.app.ui.theme.CosmicAppTheme
import com.astroluna.app.ui.theme.CosmicGradients
import com.astroluna.app.ui.theme.CosmicColors
import com.astroluna.app.ui.theme.CosmicShapes
import coil.compose.AsyncImage
import com.astroluna.app.data.api.ApiClient
import com.astroluna.app.data.model.Banner

import androidx.compose.foundation.ExperimentalFoundationApi




@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BannerSection(banners: List<Banner>) {
    if (banners.isEmpty()) return

    val pagerState = androidx.compose.foundation.pager.rememberPagerState(pageCount = { banners.size })

    // Auto-scroll logic
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(5000) // 5 seconds
            if (banners.isNotEmpty()) {
                val nextPage = (pagerState.currentPage + 1) % banners.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(bottom = 24.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 0.dp),
            pageSpacing = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) { page ->
             val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
             val scale by animateFloatAsState(targetValue = if (pageOffset == 0f) 1f else 0.9f, label = "scale")
             val alpha by animateFloatAsState(targetValue = if (pageOffset == 0f) 1f else 0.6f, label = "alpha")

             val banner = banners[page]

            Card(
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentTeal.copy(alpha = 0.3f)),
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    }
                    .fillMaxSize()
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // 1. Dynamic Background Image
                    AsyncImage(
                        model = banner.imageUrl,
                        contentDescription = banner.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // 2. Gradient Overlay for Readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color.Black.copy(alpha = 0.8f), Color.Transparent)
                                )
                            )
                    )

                    // 3. Content Text
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(24.dp)
                            .fillMaxWidth(0.7f) // Limit width so text doesn't span full image
                    ) {
                        if (!banner.title.isNullOrEmpty()) {
                            Text(
                                text = banner.title,
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White,
                                lineHeight = 30.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        if (!banner.subtitle.isNullOrEmpty()) {
                            Text(
                                text = banner.subtitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha=0.9f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // CTA Pill
                        if (!banner.ctaText.isNullOrEmpty()) {
                             Box(
                                 modifier = Modifier
                                     .background(AccentTeal, RoundedCornerShape(50))
                                     .padding(horizontal = 16.dp, vertical = 8.dp)
                             ) {
                                 Text(
                                     text = banner.ctaText,
                                     style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                     color = TextPrimary
                                 )
                             }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Indicators
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            repeat(banners.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) AccentTeal else AccentTeal.copy(alpha = 0.2f)
                val width by animateDpAsState(targetValue = if (pagerState.currentPage == iteration) 24.dp else 8.dp, label = "dotWidth")

                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .height(6.dp)
                        .width(width)
                        .clip(RoundedCornerShape(50))
                        .background(color)
                )
            }
        }
    }
}



// Data class wrapper for Rasi to be used in Compose
data class ComposeRasiItem(val id: Int, val name: String, val iconRes: Int, val color: Color)

// Local color definitions removed to use Theme aliases (White)

// Helper for Premium Sacred Cards


@Composable
fun HomeScreen(
    walletBalance: Double,
    horoscope: String,
    astrologers: List<Astrologer>,
    isLoading: Boolean,
    onWalletClick: () -> Unit,
    onChatClick: (Astrologer) -> Unit,
    onCallClick: (Astrologer, String) -> Unit,
    onRasiClick: (ComposeRasiItem) -> Unit,
    onLogoutClick: () -> Unit,
    onDrawerItemClick: (String) -> Unit = {},
    onServiceClick: (String) -> Unit = {},
    isGuest: Boolean = false // New Param
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedFilter by remember { mutableStateOf("All") }
    // Language State (Default Tamil)
    var isTamil by rememberSaveable { mutableStateOf(true) }

    // Banners State
    var banners by remember { mutableStateOf<List<Banner>>(emptyList()) }

    // Fetch Banners
    LaunchedEffect(Unit) {
        try {
            val response = ApiClient.api.getBanners()
            if (response.isSuccessful && response.body()?.ok == true) {
                banners = response.body()!!.banners
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Logic to filter astrologers based on selection
    val filteredAstros = remember(selectedFilter, astrologers) {
        if (selectedFilter == "All") astrologers
        else astrologers.filter { astro ->
             // Match skill or name
             astro.skills.any { it.contains(selectedFilter, ignoreCase = true) } ||
             astro.name.contains(selectedFilter, ignoreCase = true)
        }
    }

    var showLowBalanceDialog by remember { mutableStateOf(false) }

    if (showLowBalanceDialog) {
        AlertDialog(
            onDismissRequest = { showLowBalanceDialog = false },
            title = { Text("Low Balance!", fontWeight = FontWeight.Bold, color = Color.Red) },
            text = {
                Column {
                    Text("Current session ended due to insufficient funds. Please recharge to continue.", color = CosmicAppTheme.colors.textPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Current Balance: ₹${walletBalance.toInt()}", fontWeight = FontWeight.Bold, color = CosmicAppTheme.colors.accent)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLowBalanceDialog = false
                        onWalletClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentTeal)
                ) {
                    Text("Add Funds Now", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLowBalanceDialog = false }) {
                    Text("I'll do it later", color = CosmicAppTheme.colors.textSecondary)
                }
            },
            containerColor = CosmicAppTheme.colors.cardBg,
            shape = RoundedCornerShape(16.dp)
        )
    }

    fun checkBalanceAndProceed(action: () -> Unit) {
        if (!isGuest && walletBalance < 10) { // Skip check for guest (login handles it)
            showLowBalanceDialog = true
        } else {
            action()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                onItemClick = { item ->
                    scope.launch { drawerState.close() }
                    onDrawerItemClick(item)
                    if (item == "Logout") onLogoutClick()
                },
                onClose = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            containerColor = BackgroundMain,
            topBar = {
                HomeTopBar(
                    balance = walletBalance,
                    onWalletClick = onWalletClick,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    isGuest = isGuest,
                    isTamil = isTamil,
                    onToggleLanguage = { isTamil = !isTamil }
                )
            },
            bottomBar = {
                Column {
                    // STICKY FOOTER: Single Emerald Button
                    val showFooter = selectedTab == 0 // Only show on Home tab
                    if (showFooter) {
                        StickyFooterButtons(
                            isGuest = isGuest,
                            onTabSelected = { selectedTab = it },
                            onLoginClick = onWalletClick
                        )
                    }
                    HomeBottomBar(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                // 🌌 Background Clean
                Box(modifier = Modifier.fillMaxSize().background(BackgroundAlt))

                // Content Layer
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(bottom = 16.dp),
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    // 0. Top Services Row (Reference UI)
                    if (selectedTab == 0) {
                        item { TopServicesSection() }
                    }

                    // 1. Daily Horoscope Card (Only on Home)
                    if (selectedTab == 0) {
                        item { DailyHoroscopeCard(horoscope) }
                    }

                    // 2. Banner (Only on Home)
                    if (selectedTab == 0) {
                        item { BannerSection(banners) }
                    }

                    // 3. Rasi Grid Section (Only on Home)
                    if (selectedTab == 0) {
                        item {
                            Text(
                                text = Localization.get("horoscope", isTamil),
                                style = MaterialTheme.typography.titleLarge,
                                color = CosmicAppTheme.colors.accent,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        item { RasiGridSection(onRasiClick) }
                    }

                    // 4. Customer Stories (Marketplace)
                    item { CustomerStoriesSection() }

                    // 5. Astrologers Title
                    item {
                        val title = when(selectedTab) {
                            1 -> Localization.get("chat_services", isTamil) // Chat
                            2 -> Localization.get("video_call", isTamil) // Video
                            3 -> Localization.get("audio_call", isTamil) // Call
                            else -> Localization.get("premium_consultation", isTamil) // Home
                        }
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            color = CosmicAppTheme.colors.accent,
                            modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
                        )
                    }

                    // 5. Filter Bar (Only for Listing Tabs)
                    if (selectedTab != 0) {
                        item {
                            FilterBar(
                                filters = listOf("All", "Love", "Career", "Finance", "Marriage", "Health", "Education"),
                                selectedFilter = selectedFilter,
                                onFilterSelected = { selectedFilter = it }
                            )
                        }
                    }

                    // 6. Loading Indicator / Skeleton or List
                    if (isLoading) {
                        items(5) {
                            SkeletonAstrologerCard()
                        }
                    } else {
                        items(filteredAstros.size) { index ->
                            val astro = filteredAstros[index]
                            // Entrance Animation
                            val animatable = remember { Animatable(0f) }
                            val slideY = remember { Animatable(50f) }
                            LaunchedEffect(astro.userId) {
                                delay(index * 100L) // Staggered entrance
                                launch { animatable.animateTo(1f, tween(500, easing = FastOutSlowInEasing)) }
                                launch { slideY.animateTo(0f, tween(500, easing = FastOutSlowInEasing)) }
                            }

                            Box(modifier = Modifier.graphicsLayer {
                                scaleX = 0.9f + (0.1f * animatable.value)
                                scaleY = 0.9f + (0.1f * animatable.value)
                                translationY = slideY.value
                                alpha = animatable.value
                            }) {
                                AstrologerCard(
                                    astro = astro,
                                    onChatClick = { selectedAstro -> checkBalanceAndProceed { onChatClick(selectedAstro) } },
                                    onCallClick = { selectedAstro, type -> checkBalanceAndProceed { onCallClick(selectedAstro, type) } },
                                    selectedTab = selectedTab
                                )
                            }
                        }
                    }

                    // 7. Policy & Support Footer (Stronger Play Store Support)
                    if (selectedTab == 0) {
                        item { SupportAndPoliciesSection() }
                    }
                }
            }
        }
    }
}

@Composable
fun SupportAndPoliciesSection() {
    val context = LocalContext.current
    val baseUrl = "https://astroluna.in" // Updated domain

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Policies & Support",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            PolicyLink("Return Policy", "$baseUrl/return-policy.html", context)
            PolicyLink("Shipping Policy", "$baseUrl/shipping-policy.html", context)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            PolicyLink("Refund Policy", "$baseUrl/refund-cancellation-policy.html", context)
            PolicyLink("Terms & Conditions", "$baseUrl/terms-condition.html", context)
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Need Help? info@astroluna.in",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
        Text(
            text = "© 2024 Astro Luna. All Rights Reserved.",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary.copy(alpha=0.6f)
        )
    }
}

@Composable
fun PolicyLink(label: String, url: String, context: android.content.Context) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelMedium.copy(
            textDecoration = TextDecoration.Underline,
            fontWeight = FontWeight.Medium
        ),
        color = AccentTeal,
        modifier = Modifier.clickable {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "Cannot open link", Toast.LENGTH_SHORT).show()
            }
        }
    )
}

// --- 1. DRAWER ---
@Composable
fun AppDrawer(onItemClick: (String) -> Unit, onClose: () -> Unit) {
    val context = LocalContext.current
    ModalDrawerSheet(
        drawerContainerColor = Color(0xFFF8F9FA), // Light Color (User Request)
        drawerContentColor = Color.DarkGray
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF8F9FA)) // Light BG
                .padding(24.dp)
        ) {
            // Close Button Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Close,
                        contentDescription = "Close Drawer",
                        tint = PriceRed // Red Color (User Request)
                    )
                }
            }

            // Profile Section
            Image(
                painter = painterResource(id = com.astroluna.app.R.drawable.ic_person_placeholder),
                contentDescription = "Profile",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(2.dp, BorderLight, CircleShape)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text("User Profile", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary) // Strong Gray
            Text("Edit Profile", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Drawer Items
        val items = listOf("Home", "Profile", "Terms & Conditions", "Privacy Policy", "Settings", "Logout")
        items.forEach { item ->
            NavigationDrawerItem(
                label = {
                    Text(
                        text = item,
                        color = if(item == "Logout") PriceRed else TextPrimary, // Strong Gray / Red for logout might be nice, but strict request says "fornt garay color stonrg"
                        fontWeight = FontWeight.Bold
                    )
                },
                selected = false,
                onClick = {
                    when (item) {
                        "Terms & Conditions" -> {
                            onClose()
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://astroluna.in/terms-condition.html")))
                        }
                        "Privacy Policy" -> {
                            onClose()
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://astroluna.in/privacy-policy.html")))
                        }
                        else -> onItemClick(item)
                    }
                },
                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(24.dp))
    }
}

// --- 2. HEADER ---
@Composable
fun HomeTopBar(
    balance: Double,
    onWalletClick: () -> Unit,
    onMenuClick: () -> Unit,
    isGuest: Boolean = false,
    isTamil: Boolean,
    onToggleLanguage: () -> Unit
) {
    Surface(
        color = PrimaryPurple,
        shadowElevation = 0.dp, // Flat design
        modifier = Modifier.fillMaxWidth().height(56.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Menu Icon + Logo Text
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Astro Luna",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = Color.White
                )
            }

            // Right: Wallet/Login
            Surface(
                onClick = onWalletClick,
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp), // Consistent 8px/12px radius, but small here
                modifier = Modifier.height(32.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Icon(
                        Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isGuest) "Login" else "₹${balance.toInt()}",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}

// --- 3. RASI ITEM (Fitted BG + Border) ---
// --- 3. RASI ITEM (Simple Outline) ---
@Composable
fun RasiItemView(item: ComposeRasiItem, onClick: (ComposeRasiItem) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick(item) }
            .padding(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(64.dp)
                .border(1.dp, BorderLight, RoundedCornerShape(12.dp)) // Uniform 12px radius
                .background(Color.White, RoundedCornerShape(12.dp))
        ) {
             Image(
                painter = painterResource(id = item.iconRes),
                contentDescription = item.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(32.dp),
                colorFilter = ColorFilter.tint(PrimaryPurple) // Flat Primary Color
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = Localization.get(item.name.lowercase(), true),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = TextPrimary,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

// --- 4. ASTROLOGER CARD (Green Border, Animation, Shadow) ---
@Composable
fun AstrologerCard(
    astro: Astrologer,
    onChatClick: (Astrologer) -> Unit,
    onCallClick: (Astrologer, String) -> Unit,
    selectedTab: Int
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 2.dp, // Subtle shadow
                shape = RoundedCornerShape(12.dp),
                spotColor = Color.Black.copy(alpha = 0.1f)
            )
            .clickable {
                // Keep the functionality: Open Profile
                val intent = Intent(context, com.astroluna.app.ui.profile.AstrologerProfileActivity::class.java).apply {
                    putExtra("astro_name", astro.name)
                    putExtra("astro_exp", astro.experience.toString())
                    putExtra("astro_skills", if(astro.skills.isNotEmpty()) astro.skills.joinToString(", ") else "Vedic, Tarot")
                    putExtra("astro_id", astro.userId)
                    putExtra("is_chat_online", astro.isChatOnline)
                    putExtra("is_audio_online", astro.isAudioOnline)
                    putExtra("is_video_online", astro.isVideoOnline)
                    putExtra("astro_image", astro.image)
                    putExtra("astro_price", astro.price)
                }
                context.startActivity(intent)
            },
        shape = RoundedCornerShape(12.dp), // Consistent 12px
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // Handled by shadow
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically // Avatar centered vertically with content
        ) {
            // Left: Avatar (48dp Circle)
            Image(
                painter = painterResource(id = com.astroluna.app.R.drawable.ic_person_placeholder),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(1.dp, BorderLight, CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Middle: Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = astro.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    ),
                    color = TextPrimary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Combined metadata for cleaner look
                Text(
                    text = if(astro.skills.isNotEmpty()) astro.skills.take(2).joinToString(", ") else "Vedic",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1
                )
                Text(
                    text = "Exp: ${if(astro.experience>0) astro.experience else 5} yrs \u2022 Tam, Eng",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Right: Price & CTA
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${astro.price}/min",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = AccentTeal // Emerald Green
                    )
                )

            }
        }
    }
}

@Composable
fun HomeBottomBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(
        containerColor = Color.White,
        contentColor = TextSecondary
    ) {
        val items = listOf(
            Triple("Home", androidx.compose.material.icons.Icons.Default.Home, 0),
            Triple("Chat", androidx.compose.material.icons.Icons.Rounded.Chat, 1),
            Triple("Video", androidx.compose.material.icons.Icons.Rounded.VideoCall, 2),
            Triple("Call", androidx.compose.material.icons.Icons.Rounded.Call, 3),
            Triple("Profile", androidx.compose.material.icons.Icons.Default.Person, 4)
        )

        items.forEach { (label, icon, index) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryPurple,
                    selectedTextColor = PrimaryPurple,
                    indicatorColor = Color.Transparent, // Clean, no pill
                    unselectedIconColor = Color(0xFF9CA3AF),
                    unselectedTextColor = Color(0xFF9CA3AF)
                )
            )
        }
    }
}

@Composable
fun DailyHoroscopeCard(content: String) {
    // Breathing Animation
    val infiniteTransition = rememberInfiniteTransition(label = "CardBreath")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.01f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CardScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AccentTeal.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(PrimaryPurple.copy(alpha = 0.1f), CircleShape), // Soft Purple / Lavender
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = null,
                        tint = PrimaryPurple, // Deeper Purple
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Daily Horoscope",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        text = "இன்றைய ராசிபலன்",
                        style = MaterialTheme.typography.labelSmall,
                        color = AccentTeal
                    )
                }
            }

            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 22.sp,
                    color = Color.DarkGray
                )
            )
        }
    }
}

@Composable
fun RasiGridSection(onClick: (ComposeRasiItem) -> Unit) {
    val rasiItems = listOf(
        ComposeRasiItem(1, "Aries", com.astroluna.app.R.drawable.ic_rasi_aries_premium, Color.Gray),
        ComposeRasiItem(2, "Taurus", com.astroluna.app.R.drawable.ic_rasi_taurus_premium_copy, Color.Gray),
        ComposeRasiItem(3, "Gemini", com.astroluna.app.R.drawable.ic_rasi_gemini_premium_copy, Color.Gray),
        ComposeRasiItem(4, "Cancer", com.astroluna.app.R.drawable.ic_rasi_cancer_premium_copy, Color.Gray),
        ComposeRasiItem(5, "Leo", com.astroluna.app.R.drawable.ic_rasi_leo_premium, Color.Gray),
        ComposeRasiItem(6, "Virgo", com.astroluna.app.R.drawable.ic_rasi_virgo_premium, Color.Gray),
        ComposeRasiItem(7, "Libra", com.astroluna.app.R.drawable.ic_rasi_libra_premium_copy, Color.Gray),
        ComposeRasiItem(8, "Scorpio", com.astroluna.app.R.drawable.ic_rasi_scorpio_premium, Color.Gray),
        ComposeRasiItem(9, "Sagittarius", com.astroluna.app.R.drawable.ic_rasi_sagittarius_premium, Color.Gray),
        ComposeRasiItem(10, "Capricorn", com.astroluna.app.R.drawable.ic_rasi_capricorn_premium_copy, Color.Gray),
        ComposeRasiItem(11, "Aquarius", com.astroluna.app.R.drawable.ic_rasi_aquarius_premium, Color.Gray),
        ComposeRasiItem(12, "Pisces", com.astroluna.app.R.drawable.ic_rasi_pisces_premium_copy, Color.Gray)
    )

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        val rows = rasiItems.chunked(3) // 3 Column Grid
        for (rowItems in rows) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween // Equal spacing
            ) {
                for (item in rowItems) {
                    RasiItemView(item, onClick)
                }
                // Fill empty slots if last row has fewer items
                repeat(3 - rowItems.size) {
                    Spacer(modifier = Modifier.width(64.dp))
                }
            }
        }
    }
}

// Duplicate definitions removed






@Composable
fun FilterBar(filters: List<String>, selectedFilter: String, onFilterSelected: (String) -> Unit) {
    androidx.compose.foundation.lazy.LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        items(filters) { filter ->
            val isSelected = filter == selectedFilter
            val containerColor = if (isSelected) AccentTeal else Color.White
            val contentColor = if (isSelected) Color.White else TextPrimary
            val borderColor = if (isSelected) Color.Transparent else BorderLight

            Surface(
                onClick = { onFilterSelected(filter) },
                shape = RoundedCornerShape(50),
                color = containerColor,
                contentColor = contentColor,
                border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
                modifier = Modifier.height(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = filter,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}



@Composable
fun TopServicesSection() {
    val context = LocalContext.current
    val services: List<Triple<String, Int, Color>> = listOf(
        Triple("Love Match", com.astroluna.app.R.drawable.ic_match, BackgroundMain), // Unified BG
        Triple("Horoscope", com.astroluna.app.R.drawable.ic_free_kundali, BackgroundMain), // Unified BG
        Triple("Academy", com.astroluna.app.R.drawable.ic_academy, BackgroundMain), // Unified BG
        Triple("Earnings", com.astroluna.app.R.drawable.ic_free_services, BackgroundMain) // Unified BG
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        services.forEach { (name, icon, bgColor) ->
            ServiceItem(name, icon, bgColor) {
                when(name) {
                    "Love Match" -> {
                        val intent = Intent(context, com.astroluna.app.ui.intake.IntakeActivity::class.java).apply {
                             putExtra("type", "match")
                        }
                        context.startActivity(intent)
                    }
                    "Horoscope" -> {
                        val intent = Intent(context, com.astroluna.app.ui.intake.IntakeActivity::class.java).apply {
                            putExtra("type", "free_horoscope")
                        }
                        context.startActivity(intent)
                    }
                    "Academy" -> {
                        val intent = Intent(context, com.astroluna.app.ui.academy.AcademyActivity::class.java)
                        context.startActivity(intent)
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceItem(name: String, iconRes: Int, bgColor: Color, onClick: () -> Unit) {
    Column(
        modifier = Modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = bgColor,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
            modifier = Modifier.size(64.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            color = Color.DarkGray
        )
    }
}

@Composable
fun CustomerStoriesSection() {
    val stories = listOf(
        Triple("Akshay Sharma", "Sharjah, Dubai", "I talked to Asha ma'am on Anytime..."),
        Triple("Priya Singh", "Mumbai, India", "Very accurate prediction about my..."),
        Triple("Rahul Verma", "Delhi, India", "Helped me resolve my marriage...")
    )

    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = "Customer Stories",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            stories.forEach { (name, loc, review) ->
                CustomerStoryCard(name, loc, review)
            }
        }
    }
}

@Composable
fun CustomerStoryCard(name: String, loc: String, review: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.width(260.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Image(
                painter = painterResource(id = com.astroluna.app.R.drawable.ic_person_placeholder),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(imageVector = Icons.Filled.Menu, contentDescription=null, modifier=Modifier.size(16.dp), tint=Color.Gray) // 3-dot placeholder
                }
                Text(text = loc, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = review, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(text = "more", style = MaterialTheme.typography.labelSmall, color = AccentTeal)
            }
        }
    }
}

@Composable
fun StickyFooterButtons(
    isGuest: Boolean,
    onTabSelected: (Int) -> Unit,
    onLoginClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                if (isGuest) {
                    onLoginClick()
                } else {
                    onTabSelected(1) // Default to Chat directly for "Start Consultation"
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentTeal, // Emerald
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(100), // Fully Rounded
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(56.dp)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(100), spotColor = AccentTeal.copy(alpha=0.4f))
        ) {
            Text(
                text = "Start Consultation",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun SkeletonAstrologerCard() {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .height(140.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
        Row(modifier = Modifier.padding(12.dp).fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(70.dp).background(Color.LightGray.copy(alpha = alpha), CircleShape))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.fillMaxWidth(0.6f).height(16.dp).background(Color.LightGray.copy(alpha = alpha), RoundedCornerShape(4.dp)))
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth(0.9f).height(12.dp).background(Color.LightGray.copy(alpha = alpha), RoundedCornerShape(4.dp)))
                Spacer(modifier = Modifier.height(6.dp))
                Box(modifier = Modifier.fillMaxWidth(0.4f).height(12.dp).background(Color.LightGray.copy(alpha = alpha), RoundedCornerShape(4.dp)))
            }
        }
    }
}
