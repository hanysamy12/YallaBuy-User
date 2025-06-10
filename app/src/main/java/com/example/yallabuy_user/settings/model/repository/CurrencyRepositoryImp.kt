
//package com.example.yallabuy_user.settings.model.repository
//
//import android.util.Log
//import com.example.yallabuy_user.settings.model.local.CurrencyLocalDataSource
//import com.example.yallabuy_user.settings.model.remote.CurrencyRemoteDataSource
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import java.util.concurrent.TimeUnit
//
//class CurrencyRepositoryImpl constructor(
//    private val remoteDataSource: CurrencyRemoteDataSource,
//    private val localDataSource: CurrencyLocalDataSource,
//    ) : CurrencyRepository {
//    lateinit var apiKey: String
//    companion object {
//        private val CACHE_DURATION_MILLIS = TimeUnit.HOURS.toMillis(24)
//    }
//
//    override suspend fun getLatestRates(forceRefresh: Boolean): Map<String, Double>? {
//        return withContext(Dispatchers.IO) {
//            val lastFetchTime = localDataSource.getLastFetchTimestampMillis()
//            val currentTime = System.currentTimeMillis()
//            val isCacheExpired = (currentTime - lastFetchTime) > CACHE_DURATION_MILLIS
//
//            if (!forceRefresh && !isCacheExpired) {
//                val cachedRates = localDataSource.getRates()
//                if (cachedRates != null) {
//                    return@withContext cachedRates
//                }
//            }
//            try {
//                val response = remoteDataSource.getLatestRates(apiKey = apiKey, baseCurrency = "USD")
//
//                if (response.isSuccessful && response.body() != null && response.body()!!.result == "success") {
//                    val rates = response.body()!!.conversionRates
//                    val fetchTimestamp = response.body()!!.timeLastUpdateUnix * 1000L
//                    localDataSource.saveRates(rates, fetchTimestamp)
//                    return@withContext rates
//                } else {
//                    val staleRates = localDataSource.getRates()
//                    Log.i("Repo", "API Error: ${response.code()} - ${response.message()}")
//                    return@withContext staleRates
//                }
//            } catch (e: Exception) {
//                Log.e("Repo", "Network Exception", e)
//                val staleRates = localDataSource.getRates()
//                return@withContext staleRates
//            }
//        }
//    }
//}
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
