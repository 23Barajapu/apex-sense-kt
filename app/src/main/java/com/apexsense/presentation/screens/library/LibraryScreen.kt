package com.apexsense.presentation.screens.library

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.apexsense.presentation.components.CopyrightFooter
import com.apexsense.presentation.components.PageHeader
import com.apexsense.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(navController: NavController, viewModel: LibraryViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    val sheetState = rememberModalBottomSheetState()

    if (state.showAddSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.toggleAddSheet(false) },
            sheetState = sheetState,
            containerColor = CardGray,
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    "TAMBAH GAME KE GUDANG",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.fillMaxHeight(0.7f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.allApps) { app ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfaceGray, RoundedCornerShape(16.dp))
                                .clickable { 
                                    viewModel.addManualGame(app.package_name ?: "")
                                    viewModel.toggleAddSheet(false)
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val context = androidx.compose.ui.platform.LocalContext.current
                            val icon = remember(app.package_name) {
                                try { app.package_name?.let { context.packageManager.getApplicationIcon(it) } } catch (e: Exception) { null }
                            }
                            AsyncImage(
                                model = icon,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(app.name, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Column {
                            Text("GUDANG GAME", fontSize = 16.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                            Text("PUSTAKA PRIBADI", fontSize = 10.sp, color = AccentOrange)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
                )
            },
            containerColor = DarkBackground
        ) { padding ->
            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AccentOrange)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                        PageHeader(title = "Gudang")
                    }

                    // Tambah Game Card
                    item {
                        Card(
                            onClick = { viewModel.toggleAddSheet(true) },
                            colors = CardDefaults.cardColors(containerColor = SurfaceGray.copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(AccentOrange.copy(alpha = 0.1f), CircleShape)
                                        .border(1.dp, AccentOrange.copy(alpha = 0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = AccentOrange)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("TAMBAH GAME", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }

                    items(state.games) { game ->
                        val context = androidx.compose.ui.platform.LocalContext.current
                        ModernGameCard(
                            game = game,
                            onDelete = {
                                game.package_name?.let { viewModel.removeManualGame(it) }
                            },
                            onLaunch = {
                                game.package_name?.let { pkg ->
                                    viewModel.launchGameWithBoost {
                                        val intent = context.packageManager.getLaunchIntentForPackage(pkg)
                                        intent?.let { context.startActivity(it) }
                                    }
                                }
                            }
                        )
                    }
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                        CopyrightFooter()
                    }
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }

        if (state.isBoosting) {
            GameBoostOverlay(state.boostingProgress, state.boostingText)
        }
    }
}

@Composable
fun GameBoostOverlay(progress: Float, text: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "boost")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    val scanY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scan"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.98f))
            .clickable(enabled = false) { },
        contentAlignment = Alignment.Center
    ) {
        // System Matrix Background (Subtle)
        Column(modifier = Modifier.fillMaxSize().padding(20.dp).alpha(0.05f)) {
            repeat(20) {
                Text("SYSTEM_OPTIMIZING_CORE_PROC_0x${(100..999).random()}... OK", color = AccentOrange, fontSize = 10.sp, maxLines = 1)
            }
        }

        // Scanning Line Effect
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .align(Alignment.TopCenter)
                .offset(y = (800f * scanY).dp)
                .background(Brush.horizontalGradient(listOf(Color.Transparent, AccentOrange, Color.Transparent)))
                .shadow(elevation = 10.dp, spotColor = AccentOrange)
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center) {
                // Outer Ring
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(160.dp).graphicsLayer { rotationZ = rotation },
                    color = AccentOrange.copy(alpha = 0.2f),
                    strokeWidth = 2.dp,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                
                // Middle Ring (Reverse)
                CircularProgressIndicator(
                    progress = { 0.7f },
                    modifier = Modifier.size(140.dp).graphicsLayer { rotationZ = -rotation * 1.5f },
                    color = AccentOrange,
                    strokeWidth = 4.dp,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                
                // Inner Ring (Actual Progress)
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(110.dp),
                    color = AccentOrange,
                    strokeWidth = 8.dp,
                    trackColor = Color.White.copy(alpha = 0.05f),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )

                Icon(
                    Icons.Default.FlashOn,
                    contentDescription = null,
                    tint = AccentOrange,
                    modifier = Modifier.size(40.dp).shadow(elevation = 15.dp, spotColor = AccentOrange)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "APEX BOOST AKTIF",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    letterSpacing = 4.sp,
                    modifier = Modifier.shadow(elevation = 10.dp, spotColor = Color.White.copy(alpha = 0.5f))
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Box(
                    modifier = Modifier
                        .background(AccentOrange.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                        .border(1.dp, AccentOrange.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = text.uppercase(),
                        color = AccentOrange,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
        
        // Progress Percentage
        Text(
            text = "${(progress * 100).toInt()}%",
            color = Color.White.copy(alpha = 0.2f),
            fontSize = 80.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 100.dp)
        )
    }
}

@Composable
fun ModernGameCard(game: com.apexsense.domain.model.Game, onDelete: () -> Unit, onLaunch: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val appIcon = remember(game.package_name) {
        try {
            game.package_name?.let { context.packageManager.getApplicationIcon(it) }
        } catch (e: Exception) {
            null
        }
    }

    Card(
        onClick = onLaunch,
        colors = CardDefaults.cardColors(containerColor = SurfaceGray),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
    ) {
        Column {
            Box {
                AsyncImage(
                    model = appIcon,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                    contentScale = ContentScale.Fit,
                    placeholder = painterResource(id = android.R.drawable.ic_menu_gallery)
                )
                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                            )
                        )
                )
                
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                        .size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                Text(
                    text = game.name.uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp),
                    maxLines = 1
                )
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("TERINSTAL", color = AccentOrange, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
            }
        }
    }
}
