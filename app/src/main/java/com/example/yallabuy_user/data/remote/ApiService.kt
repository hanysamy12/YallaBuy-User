package com.example.yallabuy_user.data.remote

import com.example.yallabuy_user.BuildConfig
import com.example.yallabuy_user.data.models.BrandResponse
import com.example.yallabuy_user.data.models.CategoryResponse
import com.example.yallabuy_user.data.models.ProductResponse
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.http.GET
import retrofit2.http.Path


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
}