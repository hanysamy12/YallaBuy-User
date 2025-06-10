package com.example.yallabuy_user.di

import com.example.yallabuy_user.authentication.login.LoginViewModel
import com.example.yallabuy_user.authentication.registration.RegistrationViewModel
import com.example.yallabuy_user.cart.CartViewModel
import com.example.yallabuy_user.home.HomeViewModel
import com.example.yallabuy_user.repo.RepositoryInterface
import com.example.yallabuy_user.data.remote.ApiService
import com.example.yallabuy_user.data.remote.AuthInterceptor
import com.example.yallabuy_user.data.remote.FireBaseService
import com.example.yallabuy_user.data.remote.RemoteDataSource
import com.example.yallabuy_user.data.remote.RemoteDataSourceInterface
import com.example.yallabuy_user.orders.OrdersViewModel
import com.example.yallabuy_user.productInfo.ProductInfoViewModel
import com.example.yallabuy_user.repo.Repository
import com.example.yallabuy_user.products.ProductsViewModel
import com.example.yallabuy_user.profile.ProfileViewModel
import com.example.yallabuy_user.wish.WishViewModel
import com.google.firebase.auth.FirebaseAuth

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    single<AuthInterceptor> { AuthInterceptor() }
    single<Interceptor> { get<AuthInterceptor>() }
    factory {
        OkHttpClient.Builder()
            .addInterceptor(get<Interceptor>())
            .build()
    }
    single {
        Retrofit.Builder()
            .baseUrl("https://mad45-sv-and-01.myshopify.com/admin/api/2025-04/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
    }
    factory<ApiService> {
        get<Retrofit>().create(ApiService::class.java)
    }

    single { FirebaseAuth.getInstance() }
    single { FireBaseService(get()) }

    single<RemoteDataSourceInterface> {
        RemoteDataSource(get() , get())
    }
    single<RepositoryInterface> {
        Repository(get())
    }
    viewModel {
        HomeViewModel(get())
    }
    viewModel {
        WishViewModel()
    }
    viewModel {
        ProductsViewModel(get())
    }
    viewModel {
        CartViewModel()
    }
    viewModel {
        ProfileViewModel()
    }
    viewModel {
        ProductInfoViewModel(get())
    }
    viewModel {
        OrdersViewModel(get())
    }
        RegistrationViewModel(get())
    }
    viewModel {
        LoginViewModel(get())
    }

}
