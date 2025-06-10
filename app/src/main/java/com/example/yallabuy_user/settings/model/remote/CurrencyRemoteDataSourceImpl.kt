package com.example.yallabuy_user.settings.model.remote

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class CurrencyRemoteDataSourceImpl constructor(
    private val apiService: ExchangeRateApiService
) : CurrencyRemoteDataSource {

    override suspend fun getLatestRates(apiKey: String, baseCurrency: String): Flow<ExchangeRateResponse> = flow {
        emit(apiService.getLatestRates(apiKey = apiKey))
    //, baseCurrency = baseCurrency))
    }

    }

