package com.example.yallabuy_user.di


import com.example.yallabuy_user.cart.model.repo.CartRepositoryImpl
import com.example.yallabuy_user.cart.model.repo.ICartRepository
import com.example.yallabuy_user.cart.viewmodel.CartViewModel
import com.example.yallabuy_user.home.HomeViewModel
import com.example.yallabuy_user.repo.RepositoryInterface
import com.example.yallabuy_user.data.remote.ApiService
import com.example.yallabuy_user.data.remote.AuthInterceptor
import com.example.yallabuy_user.data.remote.RemoteDataSource
import com.example.yallabuy_user.data.remote.RemoteDataSourceInterface
import com.example.yallabuy_user.productInfo.ProductInfoViewModel
import com.example.yallabuy_user.repo.Repository
import com.example.yallabuy_user.products.ProductsViewModel
import com.example.yallabuy_user.profile.ProfileViewModel
import com.example.yallabuy_user.settings.model.remote.CurrencyConversionManager
import com.example.yallabuy_user.settings.model.local.CurrencyPreferenceManager
import com.example.yallabuy_user.settings.model.local.CurrencyPreferenceManagerImpl
import com.example.yallabuy_user.settings.model.remote.CurrencyRemoteDataSource
import com.example.yallabuy_user.settings.model.remote.CurrencyRemoteDataSourceImpl
import com.example.yallabuy_user.settings.model.remote.ExchangeRateApiService
import com.example.yallabuy_user.settings.model.remote.address.AddressManager
import com.example.yallabuy_user.settings.model.remote.address.IAddressManager
import com.example.yallabuy_user.settings.model.repository.CurrencyRepository
import com.example.yallabuy_user.settings.model.repository.ICurrencyRepository
import com.example.yallabuy_user.settings.model.repository.address.AddressRepository
import com.example.yallabuy_user.settings.model.repository.address.IAddressRepository
import com.example.yallabuy_user.settings.viewmodel.AddressViewModel
import com.example.yallabuy_user.settings.viewmodel.CurrencyViewModel
import com.example.yallabuy_user.wish.WishViewModel

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.core.scope.get
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



    single<RemoteDataSourceInterface> {
        RemoteDataSource(get())
    }
    single<RepositoryInterface> {
        Repository(get())
    }

//    single<CartRemoteDataSource> {
//        CartRemoteDataSource(get())
//    }

    single<ICartRepository> {
        CartRepositoryImpl(get())
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

//address

    single<IAddressManager>{
        AddressManager(get())
    }

    single<IAddressRepository>{
        AddressRepository(get ())
    }

    viewModel {
        HomeViewModel(get())
    }
    viewModel {
        WishViewModel()
    }
    viewModel {
        ProductsViewModel(get(),get())
    }
    viewModel {
        CartViewModel(get())
    }
    viewModel {
        ProfileViewModel()
    }
    viewModel {
        ProductInfoViewModel(get())
    }
    viewModel {
        CurrencyViewModel(get())
    }
    viewModel {
        AddressViewModel(get())
    }

}
