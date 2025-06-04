package com.example.yallabuy_user.settings.model.repository

interface CurrencyRepository {

    suspend fun getLatestRates(forceRefresh: Boolean = false): Map<String, Double>?
}