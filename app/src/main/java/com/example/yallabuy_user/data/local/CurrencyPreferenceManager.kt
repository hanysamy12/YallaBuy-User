package com.example.yallabuy_user.data.local

import kotlinx.coroutines.flow.Flow

interface CurrencyPreferenceManager {

    suspend fun setPreferredCurrency(currencyCode: String)
    suspend fun getPreferredCurrency(): String

    val preferredCurrencyFlow: Flow<String>

    suspend fun setCurrencyRate(rate: Double)
    suspend fun getCurrencyRate(): Double?

    suspend fun setLastUpdateTime(timestamp: Long)
    suspend fun getLastUpdateTime(): Long?

    companion object {
        const val DEFAULT_CURRENCY = "EGP"
        val SUPPORTED_CURRENCIES = listOf("USD", "EUR", "EGP", "SAR")
        const val RATE_EXPIRATION_MS = 24 * 60 * 60 * 1000 // 24 hours
    }
}