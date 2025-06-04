package com.example.yallabuy_user.settings.model.remote

import retrofit2.Response

class CurrencyRemoteDataSourceImpl (
        private val apiService: ExchangeRateApiService
    ) : CurrencyRemoteDataSource {

        override suspend fun getLatestRates(apiKey: String, baseCurrency: String): Response<ExchangeRateResponse> {
            return apiService.getLatestRates(apiKey = apiKey, baseCurrency = baseCurrency)
        }

    }
