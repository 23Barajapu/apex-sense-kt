package com.apexsense.presentation.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.apexsense.presentation.navigation.BottomNavItem
import com.apexsense.presentation.theme.AccentOrange
import com.apexsense.presentation.theme.SurfaceGray
import kotlinx.coroutines.launch

@Composable
fun ApexBottomBar(
    navController: NavController,
    pagerState: androidx.compose.foundation.pager.PagerState
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()

    val items = listOf(
        BottomNavItem.Profile,
        BottomNavItem.Home,
        BottomNavItem.Tools
    )

    // Only show on main screens
    if (currentRoute == null || currentRoute == "splash" || currentRoute.startsWith("result")) {
        return
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.height(64.dp),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1614).copy(alpha = 0.95f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    val isSelected = if (currentRoute == com.apexsense.presentation.navigation.Screen.MainContainer.route) {
                        pagerState.currentPage == index
                    } else {
                        currentRoute == item.route
                    }
                    
                    Surface(
                        onClick = {
                            if (currentRoute == com.apexsense.presentation.navigation.Screen.MainContainer.route) {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            } else if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp)),
                        color = if (isSelected) Color(0xFF2C1F1A) else Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = if (isSelected) AccentOrange else Color.Gray,
                                modifier = Modifier.size(24.dp)
                            )
                            
                            AnimatedVisibility(
                                visible = isSelected,
                                enter = expandHorizontally() + fadeIn(),
                                exit = shrinkHorizontally() + fadeOut()
                            ) {
                                Text(
                                    text = item.title,
                                    color = AccentOrange,
                                    fontSize = 14.sp,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
