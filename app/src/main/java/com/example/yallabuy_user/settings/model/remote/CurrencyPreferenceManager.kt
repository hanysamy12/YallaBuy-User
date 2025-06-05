package com.example.yallabuy_user.settings.model.remote

import kotlinx.coroutines.flow.Flow

interface CurrencyPreferenceManager {


    suspend fun getPreferredCurrency(): String

    //save the preferred currency code
    suspend fun setPreferredCurrency(currencyCode: String)


     // this allows other parts of the app to reactively update when the preference changes.
    val preferredCurrencyFlow: Flow<String>

    companion object {
        const val DEFAULT_CURRENCY = "EGP"
        val SUPPORTED_CURRENCIES = listOf("USD", "EUR", "EGP", "SAR")
    }
}
