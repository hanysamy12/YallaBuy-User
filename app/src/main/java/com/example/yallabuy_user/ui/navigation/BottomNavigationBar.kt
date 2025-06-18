package com.example.yallabuy_user.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.yallabuy_user.R

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navigationItems = listOf(
        NavigationItem("Home", painterResource(R.drawable.ic_home), ScreenRoute.Home.route),
        NavigationItem("Wish List", painterResource(R.drawable.ic_wish), ScreenRoute.WishList.route),
        NavigationItem("Category", painterResource(R.drawable.ic_collections), ScreenRoute.Collections.route),
        NavigationItem("Cart", painterResource(R.drawable.ic_cart), ScreenRoute.Cart.route),
        NavigationItem("Profile", painterResource(R.drawable.ic_account), ScreenRoute.Profile.route)
    )

    val currentDestination = navController.currentBackStackEntryAsState().value
    val currentRoute = currentDestination?.destination?.route

    val selectedNavigationIndex = remember(currentRoute) {
        navigationItems.indexOfFirst { item ->
            when {
                item.route == ScreenRoute.Home.route -> currentRoute?.startsWith(ScreenRoute.Home.route) == true
                else -> currentRoute == item.route
            }
        }.takeIf { it >= 0 } ?: 0
    }

    val middleIndex = 2

//    val shouldShowBottomNav = currentRoute !in listOf(
//        ScreenRoute.ProductDetails.route,
//        ScreenRoute.CreateProduct.route,
//        ScreenRoute.AboutUs.route,
//        ScreenRoute.ContactUs.route
//    )

//    if (shouldShowBottomNav)

        Box {
            NavigationBar(containerColor = Color.White) {
                navigationItems.forEachIndexed { index, item ->
                    if (index == middleIndex) {
                        Spacer(Modifier.weight(1f)) // reserve space for floating button
                    } else {
                        NavigationBarItem(
                            selected = selectedNavigationIndex == index,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Column(
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        painter = item.icon,
                                        contentDescription = item.title,
                                        tint = if (selectedNavigationIndex == index)
                                            Color.Black
                                        else
                                            Color(0xFF4F585D)
                                    )
                                }
                            },
                            label = {
                                Text(
                                    item.title,
                                    color = if (index == selectedNavigationIndex)
                                        Color(0xFF1B1B1E)
                                    else
                                        Color(0xFF4F585D)
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color(0xFF009688)
                            )
                        )
                    }
                }
            }

            // Floating Center Button (e.g., for "Category")
            val isSelected = currentRoute == ScreenRoute.Collections.route

            FloatingActionButton(
                onClick = {
                    navController.navigate(ScreenRoute.Collections.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                containerColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(6.dp),
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-18).dp)
            ) {
                Icon(
                    painter = navigationItems[middleIndex].icon,
                    contentDescription = "Category",
                    tint = if (isSelected) Color(0xFF009688) else Color(0xFF424554)
                )
            }
        }

}

//
//@Composable
//fun BottomNavigationBar(navController: NavController) {
//    val navigationItems = listOf(
//        NavigationItem("Home", painterResource(R.drawable.ic_home), ScreenRoute.Home.route),
//        NavigationItem(
//            "Wish List", painterResource(R.drawable.ic_wish), ScreenRoute.WishList.route
//        ),
//        NavigationItem(
//            "Category", painterResource(R.drawable.ic_collections), ScreenRoute.Collections.route
//        ),
//        NavigationItem("Cart", painterResource(R.drawable.ic_cart), ScreenRoute.Cart.route),
//        NavigationItem("Profile", painterResource(R.drawable.ic_account), ScreenRoute.Profile.route)
//    )
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//
//    val currentRoute = navBackStackEntry?.destination?.route
//
//    val selectedNavigationIndex = remember(currentRoute) {
//        navigationItems.indexOfFirst { item ->
//            when {
//                item.route == ScreenRoute.Home.route -> currentRoute?.startsWith(ScreenRoute.Home.route) == true
//                else -> currentRoute == item.route
//            }
//        }.takeIf { it >= 0 } ?: 0
//
//    }
//
//    NavigationBar(containerColor = Color.White) {
//        navigationItems.forEachIndexed { index, navigationItem ->
//            NavigationBarItem(
//                selected = selectedNavigationIndex == index, onClick = {
//                navController.navigate(navigationItem.route) {
//                    popUpTo(navController.graph.startDestinationId)
//                    launchSingleTop = true
//                    restoreState = true
//                }
//            }, icon = {
//                Icon(
//                    painter = navigationItem.icon, contentDescription = navigationItem.title
//                )
//            }, label = {
//                Text(navigationItem.title)
//            }, colors = NavigationBarItemDefaults.colors(
//                indicatorColor = Color(0xFF3B9A94)
//            )
//            )
//        }
//    }
//}