package com.example.yallabuy_user.utilities

import android.util.Log
import com.example.yallabuy_user.data.local.CurrencyPreferenceManager
import com.example.yallabuy_user.repo.ICurrencyRepository

class CurrencyConversionManager(
    private val currencyRepository: ICurrencyRepository
) {

    private var cachedRates: Map<String, Double>? = null
    // This stores the base currency (the currency in which the user’s amounts are originally denominated).
    // It is initialized with a default value from CurrencyPreferenceManager.DEFAULT_CURRENCY.
    private var baseCurrency: String = CurrencyPreferenceManager.DEFAULT_CURRENCY
    private var lastUpdateTime: Long = 0L

    private val expirationMillis = CurrencyPreferenceManager.RATE_EXPIRATION_MS

    suspend fun convertAmount(amountInBase: Double): Double {
        val preferredCurrency = currencyRepository.getPreferredCurrency()
        Log.i("TAG", "convertAmount: preferredCurrency $preferredCurrency ")
        if (preferredCurrency == baseCurrency) return amountInBase
        Log.i("TAG", "convertAmount: preferredCurrency after IF $preferredCurrency ")

        val isExpired = System.currentTimeMillis() - lastUpdateTime > expirationMillis
        val isRateMissing = cachedRates == null || preferredCurrency !in cachedRates!!

        if (isRateMissing || isExpired) {
            Log.i("TAG", "convertAmount: rate is missing")
            fetchLatestRates()
        }

        val rate = cachedRates?.get(preferredCurrency)
        Log.i("TAG", "convertAmount: rate is $rate ")
        return if (rate != null) amountInBase * rate else amountInBase
    }

    suspend fun fetchLatestRates() {
        //baseCurrency = currencyRepository.getPreferredCurrency()

        Log.i("TAG", "fetchLatestRates: base curr $baseCurrency ")
        try {
            val allRates = currencyRepository.getRatesForBase(baseCurrency)
            Log.i("TAG", "fetchLatestRates: allRates $allRates ")

            cachedRates = allRates
            lastUpdateTime = System.currentTimeMillis()

        } catch (e: Exception) {
            Log.e("TAG", "fetchLatestRates: error $e ")
        }
    }
}