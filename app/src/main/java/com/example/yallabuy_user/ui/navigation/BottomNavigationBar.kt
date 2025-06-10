package com.example.yallabuy_user.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.yallabuy_user.R


@Composable
fun BottomNavigationBar(navController: NavController) {
    val navigationItems = listOf(
        NavigationItem("Home", painterResource(R.drawable.ic_home), ScreenRoute.Home.route),
        NavigationItem(
            "Wish List", painterResource(R.drawable.ic_wish), ScreenRoute.WishList.route
        ),
        NavigationItem(
            "Category", painterResource(R.drawable.ic_collections), ScreenRoute.Collections.route
        ),
        NavigationItem("Cart", painterResource(R.drawable.ic_cart), ScreenRoute.Cart.route),
        NavigationItem("Profile", painterResource(R.drawable.ic_account), ScreenRoute.Profile.route)
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
                selected = selectedNavigationIndex == index, onClick = {
                navController.navigate(navigationItem.route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                    restoreState = true
                }
            }, icon = {
                Icon(
                    painter = navigationItem.icon, contentDescription = navigationItem.title
                )
            }, label = {
                Text(navigationItem.title)
            }, colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color(0xFF3B9A94)
            )
            )
        }
    }
}