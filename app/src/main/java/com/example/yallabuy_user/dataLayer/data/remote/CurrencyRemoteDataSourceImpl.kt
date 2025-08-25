package com.example.yallabuy_user.dataLayer.data.remote

import com.example.yallabuy_user.dataLayer.data.models.settings.ExchangeRateResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CurrencyRemoteDataSourceImpl(
    private val apiService: ExchangeRateApiService
) : CurrencyRemoteDataSource {

    override suspend fun getLatestRates(apiKey: String, baseCurrency: String): Flow<ExchangeRateResponse> = flow {
        emit(apiService.getLatestRates(apiKey = apiKey))
    //, baseCurrency = baseCurrency))
    }

    }

