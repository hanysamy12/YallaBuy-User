package com.example.yallabuy_user.data.remote

import WishListDraftOrderRequest
import com.example.yallabuy_user.BuildConfig
import android.util.Log
import com.example.yallabuy_user.data.models.cart.DraftOrderBody
import com.example.yallabuy_user.data.models.BrandResponse
import com.example.yallabuy_user.data.models.CategoryResponse
import com.example.yallabuy_user.data.models.OrderDetailsResponse
import com.example.yallabuy_user.data.models.OrdersResponse
import com.example.yallabuy_user.data.models.ProductResponse
import com.example.yallabuy_user.data.models.cart.DraftOrderResponse
import com.example.yallabuy_user.data.models.createUser.CreateUserOnShopifyResponse
import com.example.yallabuy_user.data.models.createUser.request.CreateUSerOnShopifyRequest
import com.example.yallabuy_user.data.models.customer.CustomerDataResponse
import com.example.yallabuy_user.data.models.productInfo.ProductInfoResponse
import com.example.yallabuy_user.data.models.wishListDraftOrder.UpdateNoteInCustomer
import com.example.yallabuy_user.data.models.wishListDraftOrder.response.WishListDraftOrderResponse
import com.example.yallabuy_user.data.models.settings.AddressBody
import com.example.yallabuy_user.data.models.settings.AddressesResponse
import com.example.yallabuy_user.data.models.settings.DeleteResponse
import com.example.yallabuy_user.data.models.settings.NewAddressResponse
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


class AuthInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = BuildConfig.API_TOKEN
        val request = chain.request().newBuilder()
            .addHeader("X-Shopify-Access-Token", token)
            .addHeader("Content-Type", "application/json")
            .build()
        Log.d("TAG", "interceptor : ${request.url} ====== ${request.headers.get("X-Shopify-Access-Token")} ")
        return chain.proceed(request)
    }
}

interface ApiService  {
    @GET("custom_collections.json")
    suspend fun getAllCategories(): CategoryResponse

    @GET("smart_collections.json")
    suspend fun getAllBrands(): BrandResponse

    @GET("collections/{categoryID}/products.json")
    suspend fun getCategoryProducts(@Path("categoryID") categoryID: Long): ProductResponse

    @GET("products.json")
    suspend fun getAllProducts(): ProductResponse

    @GET("products/{product_id}.json")
    suspend fun getProductById(

      @Path("product_id") productId: Long
    ): ProductInfoResponse

    @GET("customers/{userID}/orders.json")
    suspend fun getPreviousOrders(@Path("userID") userID: Long): OrdersResponse

    @GET("orders/{orderID}.json")
    suspend fun getOrderById(@Path("orderID") orderID: Long): OrderDetailsResponse

    @Headers("Accept: application/json")
    @POST("customers.json?send_email_invite=true")
    suspend fun createUserOnShopify(
        @Body request : CreateUSerOnShopifyRequest
    ) : CreateUserOnShopifyResponse

    @GET("/admin/api/2025-04/customers/search.json")
    suspend fun getUserDataByEmail(
        @Query("email") email : String
    ) : CustomerDataResponse

    @GET("/admin/api/2025-04/customers/{id}.json")
    suspend fun getCustomerById(
        @Path("id") customerId : Long
    ) : CreateUserOnShopifyResponse

    @POST("/admin/api/2025-04/draft_orders.json")
    suspend fun createWishListDraftOrder(
        @Body wishListDraftOrderRequest: WishListDraftOrderRequest
    ) : WishListDraftOrderResponse

    @PUT("/admin/api/2025-04/customers/{id}.json")
    suspend fun updateNoteInCustomer(
        @Path("id") customerId : Long ,
        @Body updateNoteInCustomer: UpdateNoteInCustomer
    ) : CreateUserOnShopifyResponse

    @GET("/admin/api/2025-04/draft_orders/{id}.json")
    suspend fun getWishListDraftById(
        @Path("id") wishListDraftOrderId : Long
    ) : WishListDraftOrderResponse

    @PUT("/admin/api/2025-04/draft_orders/{draftOrderId}.json")
    suspend fun updateDraftOrder(
        @Path("draftOrderId") draftOrderId : Long ,
        @Body wishListDraftOrderRequest: WishListDraftOrderRequest
    ): WishListDraftOrderResponse
//address
    @GET("customers/{customer_id}/addresses/{address_id}.json")
    suspend fun getCustomerAddressById(
        @Path("customer_id") customerId: Long,
        @Path("address_id") addressId: Long,
    ): NewAddressResponse

    @GET("customers/{customer_id}/addresses.json")
    suspend fun getAddresses(
        @Path("customer_id") customerId: Long
    ): AddressesResponse

    @POST("customers/{customer_id}/addresses.json")
    suspend fun createCustomerAddress(
        @Path("customer_id") customerId: Long,
        @Body newAddressBody: AddressBody,
    ): NewAddressResponse

    @PUT("customers/{customer_id}/addresses/{address_id}.json")
    suspend fun updateCustomerAddress(
        @Path("customer_id") customerId: Long,
        @Path("address_id") addressId: Long,
        @Body updatedAddressBody: AddressBody,
    ): NewAddressResponse

    @DELETE("customers/{customer_id}/addresses/{address_id}.json")
    suspend fun deleteCustomerAddress(
        @Path("customer_id") customerId: Long,
        @Path("address_id") addressId: Long,
    ): DeleteResponse

//cart

//    @GET("draft_orders/{draft_order_id}.json")
//    suspend fun getDraftOrders(
//        @Path("draft_order_id") draftOrderID: Long
//    ): DraftOrderBody

    @POST("draft_orders.json")
    suspend fun createDraftOrder(
        @Body draftOrderBody: DraftOrderBody
    ): DraftOrderBody

    @GET("draft_orders.json")
    suspend fun getDraftOrders(
    ): DraftOrderResponse


    @PUT("draft_orders/{draft_order_id}.json")
    suspend fun updateDraftOrder(
        @Body draftOrderBody: DraftOrderBody,
        @Path("draft_order_id") draftOrderID: Long
    ): DraftOrderBody


    @DELETE("draft_orders/{draft_order_id}.json")
    suspend fun deleteDraftOrder(
        @Path("draft_order_id") draftOrderID: Long
    )

    @GET("products/{product_id}.json")
    suspend fun getProductByID(
        @Path("product_id") productID: Long
    ): ProductResponse


}