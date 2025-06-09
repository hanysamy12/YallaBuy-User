package com.example.yallabuy_user.ui.navigation

import androidx.compose.ui.graphics.painter.Painter
import kotlinx.serialization.Serializable

data class NavigationItem(
    val title: String, val icon: Painter, val route: String
)

@Serializable
sealed class ScreenRoute(val route: String) {
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
    data class ProductsScreen(val vendorName: String?, val categoryID: Long?) : ScreenRoute(
        route = "products/{vendorName}/{categoryID}"
    ) {
        companion object {
            const val BASE_ROUTE = "products"
            const val FULL_ROUTE = "products/{vendorName}/{categoryID}"
            fun createRoute(vendorName: String?, categoryID: Long?): String {
                return if (vendorName != null || categoryID != null) "products/$vendorName/$categoryID" else BASE_ROUTE
            }
        }
    }

    @Serializable
    data object PreviousOrders : ScreenRoute("previous_orders")

    @Serializable
    data class PreviousOrderDetails(val orderId: Long?) :
        ScreenRoute(route = "previous_orders/$orderId") {
        companion object {
            const val BASE_ROUTE = "previous_orders"
            const val FULL_ROUTE = "previous_orders/{orderId}"
            fun createRoute(orderId: Long?): String {
                return if (orderId != null) "$BASE_ROUTE/$orderId" else BASE_ROUTE
            }
        }
    }

    @Serializable
    data class ProductInfo(val productId: Long)
}