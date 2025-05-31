package com.example.testshopify.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.yallabuy_user.R
import com.example.yallabuy_user.ui.navigation.NavigationItem
import com.example.yallabuy_user.ui.navigation.ScreenRoute


@Composable
fun BottomNavigationBar(navController: NavController) {
    val navigationItems = listOf(
        NavigationItem("Home", painterResource(R.drawable.ic_home), ScreenRoute.Home.route),
        NavigationItem(
            "Categories",
            painterResource(R.drawable.ic_collections),
            ScreenRoute.Collections.route
        )
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentRoute = navBackStackEntry?.destination?.route

    val selectedNavigationIndex = remember(currentRoute) {
        navigationItems.indexOfFirst { item ->
            when {
                item.route == ScreenRoute.Home.route -> currentRoute?.startsWith(ScreenRoute.Home.route) == true
                else -> currentRoute == item.route
            }
        }.takeIf { it >= 0 } ?: 0

    }
    NavigationBar(containerColor = Color.White) {
        navigationItems.forEachIndexed { index, navigationItem ->
            NavigationBarItem(
                selected = selectedNavigationIndex == index,
                onClick = {
                    navController.navigate(navigationItem.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painter = navigationItem.icon,
                        contentDescription = navigationItem.title
                    )
                },
                label = {
                    navigationItem.title
                })
        }
    }
}