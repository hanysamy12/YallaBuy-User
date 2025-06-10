package com.example.yallabuy_user.settings.model.repository

import com.example.yallabuy_user.BuildConfig
import com.example.yallabuy_user.settings.model.local.CurrencyPreferenceManager
import com.example.yallabuy_user.settings.model.remote.CurrencyRemoteDataSource
import com.example.yallabuy_user.utilities.Common
import com.example.yallabuy_user.utilities.Currency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class CurrencyRepository(
    private val remoteDataSource: CurrencyRemoteDataSource,
    private val preferenceManager: CurrencyPreferenceManager,
) : ICurrencyRepository {

    override suspend fun getPreferredCurrency(): String  {
        val currencyCode = preferenceManager.getPreferredCurrency()
        Common.currencyCode = when(currencyCode){
            "EGP" -> Currency.EGP
            "EUR" -> Currency.EUR
            "SAR" -> Currency.SAR
            "USD" -> Currency.USD
            else-> Currency.EGP
        }
        return currencyCode
    }

    override suspend fun setPreferredCurrency(currencyCode: String) {
        preferenceManager.setPreferredCurrency(currencyCode)
    }

  //  override val preferredCurrencyFlow: Flow<String> = preferenceManager.preferredCurrencyFlow

    override suspend fun getCurrencyRate(baseCurrency: String, targetCurrency: String): Double {
        val now = System.currentTimeMillis()
        val lastUpdate = preferenceManager.getLastUpdateTime() ?: 0L

        return if (now - lastUpdate < CurrencyPreferenceManager.RATE_EXPIRATION_MS) {
            preferenceManager.getCurrencyRate() ?: 1.0
        } else {

            val response = remoteDataSource.getLatestRates(BuildConfig.CURRENCY_API_KEY, baseCurrency)
                .first()

            if (response.result == "success") {
                val newRate = response.conversionRates[targetCurrency] ?: 1.0
                preferenceManager.setCurrencyRate(newRate)
                preferenceManager.setLastUpdateTime(now)
                newRate
            } else {
                preferenceManager.getCurrencyRate() ?: 1.0
            }
        }
    }

    override suspend fun getRatesForBase(baseCurrency: String): Map<String, Double> {
        return remoteDataSource
            .getLatestRates(
                apiKey = BuildConfig.CURRENCY_API_KEY,
                baseCurrency = baseCurrency
            )
            .first()
            .conversionRates    }
}
