package com.example.yallabuy_user.data.remote

import com.example.yallabuy_user.data.models.settings.ExchangeRateResponse
import kotlinx.coroutines.flow.Flow


interface CurrencyRemoteDataSource {
        suspend fun getLatestRates(apiKey: String, baseCurrency: String): Flow<ExchangeRateResponse>

    }
