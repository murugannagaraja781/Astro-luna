package com.astroluna.app.ui.home

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Chat
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.VideoCall
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.astroluna.app.R
import com.astroluna.app.data.api.ApiClient
import com.astroluna.app.data.model.Astrologer
import com.astroluna.app.data.model.Banner
import com.astroluna.app.utils.Localization
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// --- Visual Constants for Consistency ---
private val CornerRadiusLarge = 24.dp
private val CornerRadiusMedium = 16.dp
private val CornerRadiusSmall = 12.dp
private val PaddingScreen = 16.dp
private val SpacingSection = 24.dp
private val CardElevation = 2.dp

// Premium Colors (Meditation/Fintech Aesthetic)
private val ColorSurface = Color(0xFFFFFFFF)
private val ColorBackground = Color(0xFFF7F9FC) // Very soft blue-grey
private val ColorPrimary = Color(0xFF673AB7) // Deep Purple
private val ColorTextPrimary = Color(0xFF1A1C1E)
private val ColorTextSecondary = Color(0xFF757575)
private val ColorAccent = Color(0xFF2E7D32) // Soft Green for Money/Success
private val ColorChatYellow = Color(0xFFFFE500) // Vibrant Yellow for Chat tab
private val ColorCallGreen = Color(0xFF2E7D32) // Green for Call tab
private val ColorDivider = Color(0xFFEEEEEE)

data class ComposeRasiItem(val id: Int, val name: String, val iconRes: Int, val color: Color)

@OptIn(ExperimentalMaterial3Api::class)
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
    isGuest: Boolean = false
) {
    val listState = rememberLazyListState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedFilter by remember { mutableStateOf("All") }
    // Kept logic: State for fetching banners
    var banners by remember { mutableStateOf<List<Banner>>(emptyList()) }
    var showLowBalanceDialog by remember { mutableStateOf(false) }

    // Logic Retrieval (Unchanged)
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

    val filteredAstros = remember(selectedFilter, astrologers) {
        if (selectedFilter == "All") astrologers
        else astrologers.filter { astro ->
            astro.skills.any { it.contains(selectedFilter, ignoreCase = true) } ||
            astro.name.contains(selectedFilter, ignoreCase = true)
        }
    }

    // Low Balance Dialog (Logic Unchanged)
    if (showLowBalanceDialog) {
        AlertDialog(
            onDismissRequest = { showLowBalanceDialog = false },
            title = { Text("Low Balance", style = MaterialTheme.typography.titleMedium) },
            text = { Text("Insufficient funds for consultation. Please recharge.", style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                Button(onClick = { showLowBalanceDialog = false; onWalletClick() }) {
                    Text("Recharge")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLowBalanceDialog = false }) { Text("Cancel") }
            },
            containerColor = ColorSurface,
            shape = RoundedCornerShape(CornerRadiusMedium)
        )
    }

    fun checkBalanceAndProceed(action: () -> Unit) {
        if (!isGuest && walletBalance < 10) {
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
            containerColor = ColorBackground,
            topBar = {
                HomeTopBar(
                    balance = walletBalance,
                    onWalletClick = onWalletClick,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    isGuest = isGuest,
                    selectedTab = selectedTab
                )
            },
            bottomBar = {
                Surface(
                    tonalElevation = 8.dp,
                    shadowElevation = 16.dp,
                    color = ColorPrimary // Deep Purple Bubble Color
                ) {
                    Column(modifier = Modifier.navigationBarsPadding()) {
                        if (selectedTab == 0) {
                            StickyFooterAction(
                                isGuest = isGuest,
                                onAction = { if (isGuest) onWalletClick() else selectedTab = 1 }
                            )
                        }
                        HomeBottomBar(
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it }
                        )
                    }
                }
            }
        ) { padding ->
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(bottom = 32.dp), // Extra bottom padding
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Spacer for status bar breathing room if needed, utilizing standard padding
                item { Spacer(modifier = Modifier.height(12.dp)) }

                // 0. Services (Clean Row)
                if (selectedTab == 0) {
                    item {
                        ServicesRow(onServiceClick)
                        Spacer(modifier = Modifier.height(SpacingSection))
                    }
                }

                // 1. Horoscope Quote/Card (Minimalist)
                if (selectedTab == 0) {
                    item {
                        DailyHoroscopeMinimal(horoscope)
                        Spacer(modifier = Modifier.height(SpacingSection))
                    }
                }

                // 2. Banner (Modern Pager)
                if (selectedTab == 0 && banners.isNotEmpty()) {
                    item {
                        ModernBannerPager(banners)
                        Spacer(modifier = Modifier.height(SpacingSection))
                    }
                }

                // 3. Rasi Grid (Clean)
                if (selectedTab == 0) {
                    item {
                        SectionTitle("Horoscope")
                        Spacer(modifier = Modifier.height(12.dp))
                        RasiGridClean(onRasiClick)
                        Spacer(modifier = Modifier.height(SpacingSection))
                    }
                }

                // 4. Stories
                item {
                    SectionTitle("Happy Stories")
                    Spacer(modifier = Modifier.height(12.dp))
                    CustomerStoriesRail()
                    Spacer(modifier = Modifier.height(SpacingSection))
                }

                // 5. Astrologer List Header & Filters
                item {
                    val title = when (selectedTab) {
                        1 -> "Chat with Astrologers"
                        2 -> "Video Consultation"
                        3 -> "Audio Consultation"
                        else -> "Premium Astrologers"
                    }
                    SectionTitle(title)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (selectedTab != 0) {
                    item {
                        FilterChips()
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // 6. Astrologer List
                if (isLoading) {
                    items(3) {
                        AstrologerSkeleton()
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                } else {
                    items(filteredAstros) { astro ->
                        AstrologerRowItem(
                            astro = astro,
                            selectedTab = selectedTab, // Passed tab index
                            onChatClick = { checkBalanceAndProceed { onChatClick(it) } },
                            onCallClick = { type -> checkBalanceAndProceed { onCallClick(astro, type) } }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                // 7. Footer
                if (selectedTab == 0) {
                    item {
                        SupportLinksFooter()
                    }
                }
            }
        }
    }
}

// --- Components ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    balance: Double,
    onWalletClick: () -> Unit,
    onMenuClick: () -> Unit,
    isGuest: Boolean,
    selectedTab: Int = 0
) {
    val isChatTab = selectedTab == 1
    val isCallTab = selectedTab == 3
    val isVideoTab = selectedTab == 2

    val containerColor = ColorPrimary
    val contentColor = Color.White

    CenterAlignedTopAppBar(
        title = {
            Text(
                if (isChatTab) "Chat with Astrologer" else if (isCallTab || isVideoTab) "Call with Astrologer" else "Astro Luna",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    letterSpacing = (-0.5).sp
                ),
                color = contentColor
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                    Icon(
                        if (isChatTab) Icons.Rounded.Chat else Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = contentColor,
                        modifier = Modifier.size(24.dp)
                    )
            }
        },
        actions = {
            // Pill Shape Wallet
            Surface(
                onClick = onWalletClick,
                shape = RoundedCornerShape(8.dp),
                color = Color.White.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                modifier = Modifier.padding(end = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isGuest) "LOGIN" else "₹${balance.toInt()}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = contentColor
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = containerColor,
            scrolledContainerColor = containerColor
        )
    )
}

@Composable
fun ServicesRow(onServiceClick: (String) -> Unit) {
    val services = listOf(
        Triple("Match", R.drawable.ic_match, Color(0xFFFFEBEE)),
        Triple("Horoscope", R.drawable.ic_free_kundali, Color(0xFFFFF8E1)),
        Triple("Academy", R.drawable.ic_academy, Color(0xFFE3F2FD)),
        Triple("Earn", R.drawable.ic_free_services, Color(0xFFE8F5E9))
    )
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = PaddingScreen),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        services.forEach { (name, icon, bg) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(72.dp)
                    .clickable {
                        // Logic preserved
                        when(name) {
                            "Match" -> context.startActivity(Intent(context, com.astroluna.app.ui.intake.IntakeActivity::class.java).apply { putExtra("type", "match") })
                            "Horoscope" -> context.startActivity(Intent(context, com.astroluna.app.ui.intake.IntakeActivity::class.java).apply { putExtra("type", "free_horoscope") })
                            "Academy" -> context.startActivity(Intent(context, com.astroluna.app.ui.academy.AcademyActivity::class.java))
                            else -> onServiceClick(name)
                        }
                    }
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(20.dp)) // Squircle
                        .background(ColorSurface)
                        // Subtle border instead of shadow for cleanliness
                        .background(bg.copy(alpha = 0.5f))
                ) {
                    Image(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    color = ColorTextSecondary
                )
            }
        }
    }
}

@Composable
fun DailyHoroscopeMinimal(content: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PaddingScreen),
        colors = CardDefaults.cardColors(containerColor = ColorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(CornerRadiusMedium)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF3E5F5)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.Star,
                    contentDescription = null,
                    tint = ColorPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    "Daily Insight",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = ColorTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    content,
                    style = MaterialTheme.typography.bodyMedium.copy(color = ColorTextSecondary, lineHeight = 20.sp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ModernBannerPager(banners: List<Banner>) {
    val pagerState = rememberPagerState(pageCount = { banners.size })

    // Only needed if banners > 1
    if (banners.size > 1) {
        LaunchedEffect(pagerState) {
            while (true) {
                delay(4000)
                pagerState.animateScrollToPage((pagerState.currentPage + 1) % banners.size)
            }
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = PaddingScreen),
            pageSpacing = 16.dp
        ) { page ->
            val banner = banners[page]
            Card(
                shape = RoundedCornerShape(CornerRadiusLarge),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Box {
                    AsyncImage(
                        model = banner.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Gradient overlay for text readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                                )
                            )
                    )
                    // Text
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp)
                    ) {
                        Text(
                            banner.title ?: "",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        // Indicators
        Row {
            repeat(banners.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) ColorPrimary else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }
}

@Composable
fun RasiGridClean(onClick: (ComposeRasiItem) -> Unit) {
    val rasiItems = listOf(
        ComposeRasiItem(1, "Aries", R.drawable.ic_rasi_aries_premium, Color(0xFFFFEBEE)),
        ComposeRasiItem(2, "Taurus", R.drawable.ic_rasi_taurus_premium_copy, Color(0xFFE8F5E9)),
        ComposeRasiItem(3, "Gemini", R.drawable.ic_rasi_gemini_premium_copy, Color(0xFFE3F2FD)),
        ComposeRasiItem(4, "Cancer", R.drawable.ic_rasi_cancer_premium_copy, Color(0xFFF3E5F5)),
        ComposeRasiItem(5, "Leo", R.drawable.ic_rasi_leo_premium, Color(0xFFFFF3E0)),
        ComposeRasiItem(6, "Virgo", R.drawable.ic_rasi_virgo_premium, Color(0xFFF1F8E9)),
        ComposeRasiItem(7, "Libra", R.drawable.ic_rasi_libra_premium_copy, Color(0xFFE0F2F1)),
        ComposeRasiItem(8, "Scorpio", R.drawable.ic_rasi_scorpio_premium, Color(0xFFFFE0B2)),
        ComposeRasiItem(9, "Sagittarius", R.drawable.ic_rasi_sagittarius_premium, Color(0xFFEDE7F6)),
        ComposeRasiItem(10, "Capricorn", R.drawable.ic_rasi_capricorn_premium_copy, Color(0xFFEFEBE9)),
        ComposeRasiItem(11, "Aquarius", R.drawable.ic_rasi_aquarius_premium, Color(0xFFE0F7FA)),
        ComposeRasiItem(12, "Pisces", R.drawable.ic_rasi_pisces_premium_copy, Color(0xFFFCE4EC))
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PaddingScreen)
    ) {
        rasiItems.chunked(4).forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { item ->
                    Card(
                        onClick = { onClick(item) },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = item.color), // Darker/Solid color
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, item.color.copy(alpha = 0.5f))
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = item.iconRes),
                                contentDescription = item.name,
                                modifier = Modifier.size(56.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
                if (rowItems.size < 4) {
                    repeat(4 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}


data class StoryItem(val name: String, val location: String, val review: String, val videoUrl: String)

@Composable
fun CustomerStoriesRail() {
    val context = LocalContext.current
    val stories = listOf(
        StoryItem("Akshay S.", "Dubai", "Detailed prediction about my career...", "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"),
        StoryItem("Priya K.", "Mumbai", "Very helpful session, recommended...", "zJ_X00v-jSg"), // YouTube ID test
        StoryItem("Rahul V.", "Delhi", "Guided me well through tough times...", "https://storage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4")
    )
    LazyRow(
        contentPadding = PaddingValues(horizontal = PaddingScreen),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(stories) { story ->
            Card(
                modifier = Modifier
                    .width(260.dp)
                    .clickable {
                        val intent = Intent(context, VideoStoryActivity::class.java).apply {
                            putExtra("videoUrl", story.videoUrl)
                            putExtra("title", story.name)
                        }
                        context.startActivity(intent)
                    },
                colors = CardDefaults.cardColors(containerColor = ColorSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(CornerRadiusMedium)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_person_placeholder),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp).clip(CircleShape).background(ColorDivider)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(story.name, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                            Text(story.location, style = MaterialTheme.typography.labelSmall.copy(color = ColorTextSecondary))
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = "Play",
                            tint = ColorPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        story.review,
                        style = MaterialTheme.typography.bodySmall.copy(color = ColorTextSecondary),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun FilterChips() {
    val categories = listOf(
        "All" to Icons.Default.GridView,
        "Love" to Icons.Default.Favorite,
        "Career" to Icons.Default.Work,
        "Finance" to Icons.Default.AccountBalance,
        "Education" to Icons.Default.School
    )
    LazyRow(
        contentPadding = PaddingValues(horizontal = PaddingScreen, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(categories) { (label, icon) ->
            Surface(
                shape = RoundedCornerShape(50),
                color = if (label == "All") ColorBackground else Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, ColorDivider),
                modifier = Modifier.height(36.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(icon, null, tint = if (label == "All") ColorPrimary else Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                        color = ColorTextPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun AstrologerRowItem(
    astro: Astrologer,
    selectedTab: Int,
    onChatClick: (Astrologer) -> Unit,
    onCallClick: (String) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PaddingScreen),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ColorDivider)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // LEFT: Avatar
                AsyncImage(
                    model = astro.image ?: R.drawable.ic_person_placeholder,
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .border(2.dp, ColorChatYellow, CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                // CENTER: Details
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = astro.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp),
                            color = ColorTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Default.Verified,
                            null,
                            tint = Color(0xFF2196F3),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = astro.skills.take(1).joinToString(", ").ifEmpty { "Vedic" },
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = ColorTextSecondary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Exp: ${astro.experience}Yrs",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = ColorTextSecondary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "₹${astro.price}/min",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                            color = ColorTextPrimary
                        )
                    }
                }

                // RIGHT: Rating
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) {
                            Icon(Icons.Default.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(10.dp))
                        }
                    }
                    Text(
                        "${(10..1000).random()} orders",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = ColorTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = ColorDivider, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // BOTTOM: Action Buttons in One Row, Aligned LEFT
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Video Button
                AstroActionButton(
                    icon = Icons.Rounded.VideoCall,
                    label = "Video",
                    color = if (astro.isVideoOnline || astro.isOnline) Color(0xFFE91E63) else Color.Gray,
                    onClick = { if (astro.isVideoOnline || astro.isOnline) onCallClick("video") }
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Call Button
                AstroActionButton(
                    icon = Icons.Rounded.Call,
                    label = "Call",
                    color = if (astro.isAudioOnline || astro.isOnline) ColorCallGreen else Color.Gray,
                    onClick = { if (astro.isAudioOnline || astro.isOnline) onCallClick("call") }
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Chat Button
                AstroActionButton(
                    icon = Icons.AutoMirrored.Rounded.Chat,
                    label = "Chat",
                    color = if (astro.isChatOnline || astro.isOnline) Color(0xFF2196F3) else Color.Gray,
                    onClick = { if (astro.isChatOnline || astro.isOnline) onChatClick(astro) }
                )
            }
        }
    }
}

@Composable
fun DetailInfoItem(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Icon(icon, null, tint = ColorTextSecondary, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(color = ColorTextSecondary),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun AstroActionButton(icon: ImageVector, label: String, color: Color, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
        modifier = Modifier.height(30.dp),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = color))
    }
}

// --- Footers ---

@Composable
fun StickyFooterAction(isGuest: Boolean, onAction: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .shadow(8.dp, RoundedCornerShape(50))
            .background(Color.White, RoundedCornerShape(50)) // White button on Purple background
            .clickable { onAction() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isGuest) "Login to Consult" else "Start Consultation",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
            color = ColorPrimary
        )
    }
}

@Composable
fun HomeBottomBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(
        containerColor = Color.Transparent, // Surface handles background
        tonalElevation = 0.dp
    ) {
        val items = listOf(
            Triple("Home", Icons.Filled.Home, 0),
            Triple("Chat", Icons.Rounded.Chat, 1),
            Triple("Video", Icons.Rounded.VideoCall, 2),
            Triple("Call", Icons.Rounded.Call, 3),
            Triple("Profile", Icons.Filled.Person, 4)
        )
        items.forEach { (label, icon, index) ->
            val isSelected = selectedTab == index
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                selected = isSelected,
                onClick = { onTabSelected(index) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ColorPrimary,
                    selectedTextColor = Color.White,
                    indicatorColor = Color.White, // High contrast indicator
                    unselectedIconColor = Color.White.copy(alpha = 0.6f),
                    unselectedTextColor = Color.White.copy(alpha = 0.6f)
                )
            )
        }
    }
}

@Composable
fun SupportLinksFooter() {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Terms",
                style = MaterialTheme.typography.labelMedium.copy(color = ColorTextSecondary, fontSize = 12.sp),
                modifier = Modifier.clickable { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://astroluna.in/terms-condition.html"))) }
            )
            Text("  •  ", color = Color.LightGray)
            Text(
                "Privacy",
                style = MaterialTheme.typography.labelMedium.copy(color = ColorTextSecondary, fontSize = 12.sp),
                modifier = Modifier.clickable { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://astroluna.in/privacy-policy.html"))) }
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text("v5.5.1", style = MaterialTheme.typography.labelSmall.copy(color = Color.LightGray, fontSize = 10.sp))
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = ColorTextPrimary,
        modifier = Modifier.padding(horizontal = PaddingScreen)
    )
}

@Composable
fun AppDrawer(onItemClick: (String) -> Unit, onClose: () -> Unit) {
    ModalDrawerSheet(
        drawerContainerColor = ColorSurface,
        drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Professional Header with Background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(ColorPrimary, ColorPrimary.copy(alpha = 0.8f))
                        )
                    )
                    .padding(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_person_placeholder),
                        contentDescription = null,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .border(2.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                            .padding(2.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "User Account",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            "Welcome Back!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                    // Close (X) Button
                    IconButton(onClick = onClose) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Drawer Items
            val items = listOf(
                "Home" to Icons.Default.Home,
                "Profile" to Icons.Default.Person,
                "Wallet" to Icons.Default.AccountBalanceWallet,
                "Transaction History" to Icons.Default.History,
                "Customer Support" to Icons.Default.SupportAgent,
                "Settings" to Icons.Default.Settings,
                "Logout" to Icons.Default.Logout
            )

            items.forEach { (label, icon) ->
                NavigationDrawerItem(
                    icon = { Icon(icon, contentDescription = label, tint = ColorPrimary) },
                    label = {
                        Text(
                            label,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                        )
                    },
                    selected = false,
                    onClick = { onItemClick(label) },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent,
                        unselectedTextColor = ColorTextPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // App Version in Footer
            Text(
                "Version 5.5.1",
                style = MaterialTheme.typography.labelSmall,
                color = Color.LightGray,
                modifier = Modifier.padding(24.dp).align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun AstrologerSkeleton() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PaddingScreen)
            .height(80.dp),
        colors = CardDefaults.cardColors(containerColor = ColorSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, ColorDivider)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(ColorDivider))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Box(modifier = Modifier.width(100.dp).height(14.dp).background(ColorDivider))
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.width(60.dp).height(10.dp).background(ColorDivider))
            }
        }
    }
}
