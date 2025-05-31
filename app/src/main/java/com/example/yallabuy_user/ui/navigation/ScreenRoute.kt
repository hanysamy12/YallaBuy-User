package com.example.testshopify.ui.navigation

import androidx.compose.ui.graphics.painter.Painter

data class NavigationItem(
    val title: String, val icon: Painter, val route: String
)


sealed class ScreenRoute(val route: String){
    data object Home : ScreenRoute("home")
    data object Collections : ScreenRoute("collections")
}