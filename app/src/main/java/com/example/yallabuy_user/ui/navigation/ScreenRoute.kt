package com.example.yallabuy_user.ui.navigation

import android.net.Uri
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

    @Serializable
    data class ProductsScreen(val vendorName: String?, val categoryID: Long?, val title: String?) : ScreenRoute(
        "products/{vendorName}/{categoryID}/{title}"
    )

    @Serializable
    data object PreviousOrders : ScreenRoute("previous_orders")

    @Serializable
    data class PreviousOrderDetails(val orderId: Long?) : ScreenRoute("previous_orders/$orderId") {
    }

    @Serializable
    data class ProductInfo(val productId: Long) : ScreenRoute("productInfo")

    @Serializable
    data object Registration : ScreenRoute("Registration")

    @Serializable
    data object Login : ScreenRoute("Login")

    @Serializable
    data object Settings : ScreenRoute("settings")

    @Serializable
    data object AboutUs : ScreenRoute("about_us")

    @Serializable
    data object ContactUs : ScreenRoute("contact_us")

    @Serializable
    data object Currency : ScreenRoute("currency")

    @Serializable
    data object Map : ScreenRoute("map")

    @Serializable
    data object Address : ScreenRoute("address")

    @Serializable
    data class AddressForm(
        val addressId: Long,
        val fullAddress: String,
        val city: String,
        val country: String
    ) : ScreenRoute("address_form? \"addressId={addressId}&fullAddress={fullAddress}&city={city}&country={country}") {
        fun createRoute(): String =
            "address_form?addressId=${addressId}&fullAddress=${Uri.encode(fullAddress)}&city=${Uri.encode(city)}&country=${Uri.encode(country)}"
    }

    @Serializable
    data class Payment(val total: Double) : ScreenRoute("payment")


//@Serializable
//data class Address(val customerId: Long) : ScreenRoute("address/{customerId}") {
//    companion object {
//        fun createRoute(customerId: Long) = "address/$customerId"
//    }
//}

    @Serializable
    data class OrderCheckOut(val orderId: Long, val totalAmount: Double) : ScreenRoute("new_order")

}