package com.example.yallabuy_user.settings.model.local

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomCurrencyLocalDataSource(
    private val currencyDao: CurrencyDao
) : CurrencyLocalDataSource {

    private val gson = Gson()

    override suspend fun saveRates(rates: Map<String, Double>, timestampMillis: Long) {
        withContext(Dispatchers.IO) {
            val ratesJson = gson.toJson(rates)
            val cacheEntity = CurrencyRateCacheEntity(
                id = 1,
                ratesJson = ratesJson,
                lastFetchTimestampMillis = timestampMillis
            )
            currencyDao.insertOrUpdateRates(cacheEntity)
        }
    }

    override suspend fun getRates(): Map<String, Double>? {
        return withContext(Dispatchers.IO) {
            val cacheEntity = currencyDao.getCachedRates()
            if (cacheEntity != null) {
                val type = object : TypeToken<Map<String, Double>>() {}.type
                gson.fromJson<Map<String, Double>>(cacheEntity.ratesJson, type)
            } else {
                null
            }
        }
    }

    override suspend fun getLastFetchTimestampMillis(): Long {
        return withContext(Dispatchers.IO) {
            currencyDao.getCachedRates()?.lastFetchTimestampMillis ?: 0L
        }
    }
}
