package com.example.yallabuy_user.settings.model.repository

import android.util.Log
import com.example.yallabuy_user.settings.model.local.CurrencyLocalDataSource
import com.example.yallabuy_user.settings.model.remote.CurrencyRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class CurrencyRepositoryImpl constructor(
    private val remoteDataSource: CurrencyRemoteDataSource,
    private val localDataSource: CurrencyLocalDataSource,
    ) : CurrencyRepository {
    lateinit var apiKey: String
    companion object {
        private val CACHE_DURATION_MILLIS = TimeUnit.HOURS.toMillis(24)
    }

    override suspend fun getLatestRates(forceRefresh: Boolean): Map<String, Double>? {
        return withContext(Dispatchers.IO) {
            val lastFetchTime = localDataSource.getLastFetchTimestampMillis()
            val currentTime = System.currentTimeMillis()
            val isCacheExpired = (currentTime - lastFetchTime) > CACHE_DURATION_MILLIS

            if (!forceRefresh && !isCacheExpired) {
                val cachedRates = localDataSource.getRates()
                if (cachedRates != null) {
                    return@withContext cachedRates
                }
            }
            try {
                val response = remoteDataSource.getLatestRates(apiKey = apiKey, baseCurrency = "USD")

                if (response.isSuccessful && response.body() != null && response.body()!!.result == "success") {
                    val rates = response.body()!!.conversionRates
                    val fetchTimestamp = response.body()!!.timeLastUpdateUnix * 1000L
                    localDataSource.saveRates(rates, fetchTimestamp)
                    return@withContext rates
                } else {
                    val staleRates = localDataSource.getRates()
                    Log.i("Repo", "API Error: ${response.code()} - ${response.message()}")
                    return@withContext staleRates
                }
            } catch (e: Exception) {
                Log.e("Repo", "Network Exception", e)
                val staleRates = localDataSource.getRates()
                return@withContext staleRates
            }
        }
    }
}
