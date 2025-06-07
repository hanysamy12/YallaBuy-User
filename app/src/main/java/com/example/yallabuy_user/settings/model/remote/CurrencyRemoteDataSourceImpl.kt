package com.example.yallabuy_user.settings.model.remote

import retrofit2.Response

class CurrencyRemoteDataSourceImpl constructor(
    private val apiService: ExchangeRateApiService
) : CurrencyRemoteDataSource {

    override suspend fun getLatestRates(apiKey: String, baseCurrency: String): Response<ExchangeRateResponse> {
        // Simply delegate the call to the provided Retrofit service
        return apiService.getLatestRates(apiKey = apiKey, baseCurrency = baseCurrency)
    }

}