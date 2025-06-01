package com.example.yallabuy_user.di

import com.example.yallabuy_user.cart.CartViewModel
import com.example.yallabuy_user.home.HomeViewModel
import com.example.yallabuy_user.repo.RepositoryInterface
import com.example.yallabuy_user.data.remote.ApiService
import com.example.yallabuy_user.data.remote.AuthInterceptor
import com.example.yallabuy_user.data.remote.RemoteDataSource
import com.example.yallabuy_user.data.remote.RemoteDataSourceInterface
import com.example.yallabuy_user.repo.Repository
import com.example.yallabuy_user.collections.CollectionsViewModel
import com.example.yallabuy_user.profile.ProfileViewModel
import com.example.yallabuy_user.wish.WishViewModel

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
            .addInterceptor(get())
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
    single<RemoteDataSourceInterface> {
        RemoteDataSource(get())
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
        CollectionsViewModel(get())
    }
    viewModel {
        CartViewModel()
    }
    viewModel {
        ProfileViewModel()
    }


}
