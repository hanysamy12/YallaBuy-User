package com.example.yallabuy_user.settings.model.remote

import retrofit2.Response


interface CurrencyRemoteDataSource {
         // Fetches the latest exchange rates from the API.
        suspend fun getLatestRates(apiKey: String, baseCurrency: String): Response<ExchangeRateResponse>

    }
