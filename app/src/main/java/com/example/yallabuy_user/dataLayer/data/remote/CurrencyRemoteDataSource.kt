package com.example.yallabuy_user.dataLayer.data.remote

import com.example.yallabuy_user.dataLayer.data.models.settings.ExchangeRateResponse
import kotlinx.coroutines.flow.Flow


interface CurrencyRemoteDataSource {
        suspend fun getLatestRates(apiKey: String, baseCurrency: String): Flow<ExchangeRateResponse>

    }
