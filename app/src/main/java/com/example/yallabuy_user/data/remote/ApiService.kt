package com.example.yallabuy_user.data.remote

import com.example.yallabuy_user.BuildConfig
import com.example.yallabuy_user.data.models.BrandResponse
import com.example.yallabuy_user.data.models.CategoryResponse
import com.example.yallabuy_user.data.models.OrderDetailsResponse
import com.example.yallabuy_user.data.models.OrdersResponse
import com.example.yallabuy_user.data.models.ProductResponse
import com.example.yallabuy_user.data.models.createUser.CreateUserOnShopifyResponse
import com.example.yallabuy_user.data.models.createUser.request.CreateUSerOnShopifyRequest
import com.example.yallabuy_user.data.models.customer.CustomerDataResponse
import com.example.yallabuy_user.data.models.productInfo.ProductInfoResponse
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


class AuthInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = BuildConfig.API_TOKEN
        val request = chain.request().newBuilder()
            .addHeader("X-Shopify-Access-Token", token)
            .addHeader("Content-Type", "application/json")
            .build()
        return chain.proceed(request)
    }
}

interface ApiService {
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

        @Path("product_id") productId : Long
    ) : ProductInfoResponse

    @Headers("Accept: application/json")
    @POST("customers.json?send_email_invite=true")
    suspend fun createUserOnShopify(
        @Body request : CreateUSerOnShopifyRequest
    ) : CreateUserOnShopifyResponse

    @GET("/admin/api/2025-04/customers/search.json")
    suspend fun getUserDataByEmail(
        @Query("email") email : String
    ) : CustomerDataResponse
}