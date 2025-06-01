package com.example.yallabuy_user.ui.navigation

import androidx.compose.ui.graphics.painter.Painter
import kotlinx.serialization.Serializable

data class NavigationItem(
    val title: String, val icon: Painter, val route: String
)

@Serializable
sealed class ScreenRoute(val route: String){
    @Serializable
    data object Home : ScreenRoute("home")
    @Serializable
    data object WishList : ScreenRoute("wish")
    @Serializable
    data object Collections : ScreenRoute("collections")
    @Serializable
    data object Cart : ScreenRoute("cart")
    @Serializable
    data object Profile : ScreenRoute("profile")
    @Serializable
    data class ProductsScreen (val collectionId:Long?): ScreenRoute("products")
}