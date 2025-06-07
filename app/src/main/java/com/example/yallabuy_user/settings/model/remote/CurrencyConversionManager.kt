package com.example.yallabuy_user.settings.model.remote

import com.example.yallabuy_user.BuildConfig


class CurrencyConversionManager(
    private val currencyPreferenceManager: CurrencyPreferenceManager,
    private val currencyRemoteDataSource: CurrencyRemoteDataSource
) {

    private var cachedRates: Map<String, Double>? = null
    private var baseCurrency: String = CurrencyPreferenceManager.DEFAULT_CURRENCY

    suspend fun convertAmount(amountInBase: Double): Double {
        val preferred = currencyPreferenceManager.getPreferredCurrency()

        if (preferred == baseCurrency) return amountInBase

        if (cachedRates == null || preferred !in cachedRates!!) {
            fetchLatestRates()
        }

        val rate = cachedRates?.get(preferred) ?: return amountInBase
        return amountInBase * rate
    }

    suspend fun fetchLatestRates() {
        baseCurrency = currencyPreferenceManager.getPreferredCurrency()

        val response = currencyRemoteDataSource.getLatestRates(
            apiKey = BuildConfig.CURRENCY_API_KEY,
            baseCurrency = baseCurrency
        )

        if (response.isSuccessful) {
            cachedRates = response.body()?.conversionRates
        }
    }
}
