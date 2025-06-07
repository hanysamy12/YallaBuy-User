package com.example.yallabuy_user.settings.model.remote

import kotlinx.coroutines.flow.Flow
import retrofit2.Response


interface CurrencyRemoteDataSource {
         // Fetches the latest exchange rates from the API.
        suspend fun getLatestRates(apiKey: String, baseCurrency: String): Flow<ExchangeRateResponse>

    }
