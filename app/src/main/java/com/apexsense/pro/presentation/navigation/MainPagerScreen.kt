package com.apexsense.pro.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.navigation.NavController
import com.apexsense.pro.presentation.screens.home.HomeScreen
import com.apexsense.pro.presentation.screens.profile.ProfileScreen
import com.apexsense.pro.presentation.screens.tools.GameToolsScreen

@Composable
fun MainPagerScreen(
    navController: NavController,
    pagerState: PagerState
) {
    val items = listOf(
        BottomNavItem.Profile,
        BottomNavItem.Home,
        BottomNavItem.Tools
    )

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        userScrollEnabled = true,
        beyondViewportPageCount = 2
    ) { page ->
        val pageOffset = (
            (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
        ).let { if (it < 0) -it else it }

        val alpha = 0f + (1f - 0f) * (1f - pageOffset.coerceIn(0f, 1f))
        val translationY = (-100f) * pageOffset.coerceIn(0f, 1f)

        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    this.alpha = alpha
                    this.translationY = translationY
                }
        ) {
            when (items[page]) {
                BottomNavItem.Profile -> ProfileScreen()
                BottomNavItem.Home -> HomeScreen(navController = navController)
                BottomNavItem.Tools -> GameToolsScreen(navController = navController)
            }
        }
    }
}
