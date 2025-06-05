package com.example.yallabuy_user.settings.model.local

interface CurrencyLocalDataSource {
        suspend fun saveRates(rates: Map<String, Double>, timestampMillis: Long)

        suspend fun getRates(): Map<String, Double>?

        suspend fun getLastFetchTimestampMillis(): Long
    }


