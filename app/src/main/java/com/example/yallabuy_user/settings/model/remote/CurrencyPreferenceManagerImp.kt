package com.example.yallabuy_user.settings.model.remote

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class CurrencyPreferenceManagerImpl(context: Context) : CurrencyPreferenceManager {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )

    private val _preferredCurrencyFlow = MutableStateFlow(getInitialPreference())
    override val preferredCurrencyFlow: Flow<String> = _preferredCurrencyFlow.asStateFlow()

    companion object {
        private const val PREFS_NAME = "currency_settings_prefs"
        private const val KEY_PREFERRED_CURRENCY = "preferred_currency"
    }

    private fun getInitialPreference(): String {
        return sharedPreferences.getString(KEY_PREFERRED_CURRENCY, CurrencyPreferenceManager.DEFAULT_CURRENCY)
            ?: CurrencyPreferenceManager.DEFAULT_CURRENCY
    }

    override suspend fun getPreferredCurrency(): String {

        return _preferredCurrencyFlow.value

        /*
        return withContext(Dispatchers.IO) {
            sharedPreferences.getString(KEY_PREFERRED_CURRENCY, CurrencyPreferenceManager.DEFAULT_CURRENCY)
                ?: CurrencyPreferenceManager.DEFAULT_CURRENCY
        }
        */
    }

    override suspend fun setPreferredCurrency(currencyCode: String) {

        if (currencyCode in CurrencyPreferenceManager.SUPPORTED_CURRENCIES) {
            withContext(Dispatchers.IO) {
                sharedPreferences.edit {
                    putString(KEY_PREFERRED_CURRENCY, currencyCode)
                }
            }
            _preferredCurrencyFlow.value = currencyCode
        }
    }
}
