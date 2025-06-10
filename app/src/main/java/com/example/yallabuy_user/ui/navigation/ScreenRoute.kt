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
/*    @Serializable
    data class ProductsScreen (val collectionId:Long?): ScreenRoute("products/$collectionId?")*/

    @Serializable
    data class ProductsScreen(val collectionId: String?) : ScreenRoute(
        route = "products/{collectionId}"
    ) {
        companion object {
            const val BASE_ROUTE = "products"
            const val FULL_ROUTE = "products/{collectionId}"
            fun createRoute(collectionId: Long?): String {
                return if (collectionId != null) "products/$collectionId" else BASE_ROUTE
            }
        }
    }

    @Serializable
    data class ProductInfo(val productId : Long) : ScreenRoute("productInfo")
    @Serializable
    data object Registration : ScreenRoute("Registration")
    @Serializable
    data object Login : ScreenRoute("Login")
}