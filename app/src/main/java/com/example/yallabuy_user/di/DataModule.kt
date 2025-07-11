package com.example.yallabuy_user.di

import android.util.Log
import com.example.yallabuy_user.authentication.login.LoginViewModel
import com.example.yallabuy_user.authentication.registration.RegistrationViewModel
import com.example.yallabuy_user.cart.viewmodel.CartViewModel
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
import com.example.yallabuy_user.utilities.CurrencyConversionManager
import com.example.yallabuy_user.data.local.CurrencyPreferenceManager
import com.example.yallabuy_user.data.local.CurrencyPreferenceManagerImpl
import com.example.yallabuy_user.data.remote.CurrencyRemoteDataSource
import com.example.yallabuy_user.data.remote.CurrencyRemoteDataSourceImpl
import com.example.yallabuy_user.data.remote.ExchangeRateApiService
import com.example.yallabuy_user.orders.NewOrderViewModel
import com.example.yallabuy_user.repo.CurrencyRepository
import com.example.yallabuy_user.repo.ICurrencyRepository
import com.example.yallabuy_user.settings.viewmodel.AddressViewModel
import com.example.yallabuy_user.settings.viewmodel.CurrencyViewModel
import com.example.yallabuy_user.wish.WishViewModel
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    single<AuthInterceptor> { AuthInterceptor() }
    single<Interceptor> { get<AuthInterceptor>() }
    factory {
        val httpClientLoggingInterceptor = HttpLoggingInterceptor { msg ->
            Log.i("NetworkInterceptor", "Result : $msg")
        }
        httpClientLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        OkHttpClient.Builder()
            .addInterceptor(get<Interceptor>())
            .addInterceptor(httpClientLoggingInterceptor)
            .build()
    }
    single (named("shopifyRetrofit")) {
        Retrofit.Builder()
            .baseUrl("https://mad45-sv-and-01.myshopify.com/admin/api/2025-04/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
    }

    factory<ApiService> {
        get<Retrofit>(named("shopifyRetrofit")).create(ApiService::class.java)
    }

    single { FirebaseAuth.getInstance() }
    single { FireBaseService(get()) }


    single<RemoteDataSourceInterface> {
        RemoteDataSource(get() , get())
    }
    single<RepositoryInterface> {
        Repository(get())
    }

    //currency
    single(named("currencyRetrofit")) {
        Retrofit.Builder()
            .baseUrl("https://v6.exchangerate-api.com/v6/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    factory<ExchangeRateApiService> {
        get<Retrofit>(named("currencyRetrofit")).create(ExchangeRateApiService::class.java)
    }

    single<CurrencyRemoteDataSource> {
        CurrencyRemoteDataSourceImpl(get())
    }

    single<CurrencyPreferenceManager>{
        CurrencyPreferenceManagerImpl(androidContext())
    }
    single<ICurrencyRepository>{
        CurrencyRepository(get(), get())
    }
    single<CurrencyConversionManager>{
        CurrencyConversionManager(get())
    }



    viewModel {
        HomeViewModel(get())
    }
    viewModel {
        WishViewModel(get())
    }
    viewModel {
        ProductsViewModel(get(),get())
    }
    viewModel {
        CartViewModel(get(), get())
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
    viewModel {
        RegistrationViewModel(get())
    }
//    viewModel {
//        LoginViewModel(get())
//        CurrencyViewModel(get())
//    }
    viewModel {
        LoginViewModel(get())
    }

    viewModel {
        CurrencyViewModel(get())
    }



    viewModel {
        AddressViewModel(get())
    }
    viewModel {
        NewOrderViewModel(get(),get())
    }

}
