package com.example.yallabuy_user.settings.model.repository

interface ICurrencyRepository {
    suspend fun getPreferredCurrency(): String

    suspend fun setPreferredCurrency(currencyCode: String)

    suspend fun getCurrencyRate(baseCurrency: String, targetCurrency: String): Double

    suspend fun getRatesForBase(baseCurrency: String): Map<String, Double>

}