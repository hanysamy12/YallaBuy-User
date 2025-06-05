package com.example.yallabuy_user.settings.model.repository

import kotlinx.coroutines.delay

//don't forget to remove it

class FakeCurrencyRepository : CurrencyRepository {
        override suspend fun getLatestRates(forceRefresh: Boolean): Map<String, Double>? {
            delay(1000) // Simulate a network delay
            return mapOf(
                "USD" to 1.0,
                "EUR" to 0.92,
                "EGP" to 47.5,
                "SAR" to 3.75,
                "JPY" to 155.0
            )
        }
    }
