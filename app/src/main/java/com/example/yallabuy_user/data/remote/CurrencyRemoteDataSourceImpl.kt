package com.example.yallabuy_user.data.remote

import com.example.yallabuy_user.data.models.settings.ExchangeRateResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CurrencyRemoteDataSourceImpl constructor(
    private val apiService: ExchangeRateApiService
) : CurrencyRemoteDataSource {

    override suspend fun getLatestRates(apiKey: String, baseCurrency: String): Flow<ExchangeRateResponse> = flow {
        emit(apiService.getLatestRates(apiKey = apiKey))
    //, baseCurrency = baseCurrency))
    }

    }

