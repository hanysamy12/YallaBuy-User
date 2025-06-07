package com.example.yallabuy_user.settings.model.remote

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
class CurrencyPreferenceManagerImpl(
    private val context: Context
) : CurrencyPreferenceManager {

    private val prefs = context.getSharedPreferences("currency_prefs", Context.MODE_PRIVATE)
    private val currencyKey = "preferred_currency"
    private val rateKey = "currency_rate"
    private val timeKey = "last_update_time"

    private val currencyFlow = MutableStateFlow(prefs.getString(currencyKey, CurrencyPreferenceManager.DEFAULT_CURRENCY) ?: CurrencyPreferenceManager.DEFAULT_CURRENCY)
    override val preferredCurrencyFlow: Flow<String> = currencyFlow

    override suspend fun setPreferredCurrency(currencyCode: String) {
        prefs.edit().putString(currencyKey, currencyCode).apply()
        currencyFlow.emit(currencyCode)
    }

    override suspend fun getPreferredCurrency(): String {
        return prefs.getString(currencyKey, CurrencyPreferenceManager.DEFAULT_CURRENCY) ?: CurrencyPreferenceManager.DEFAULT_CURRENCY
    }

    override suspend fun setCurrencyRate(rate: Double) {
        prefs.edit().putFloat(rateKey, rate.toFloat()).apply()
    }

    override suspend fun getCurrencyRate(): Double? {
        return if (prefs.contains(rateKey)) prefs.getFloat(rateKey, 1.0f).toDouble() else null
    }

    override suspend fun setLastUpdateTime(timestamp: Long) {
        prefs.edit().putLong(timeKey, timestamp).apply()
    }

    override suspend fun getLastUpdateTime(): Long? {
        return if (prefs.contains(timeKey)) prefs.getLong(timeKey, 0L) else null
    }
}