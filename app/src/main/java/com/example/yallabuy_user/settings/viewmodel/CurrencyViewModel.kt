package com.example.yallabuy_user.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yallabuy_user.data.local.CurrencyPreferenceManager
import com.example.yallabuy_user.repo.ICurrencyRepository
import com.example.yallabuy_user.utilities.ApiResponse
import com.example.yallabuy_user.utilities.Common
import com.example.yallabuy_user.utilities.Currency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CurrencyViewModel(
    private val repository: ICurrencyRepository
) : ViewModel() {

    private val _currencyState = MutableStateFlow<ApiResponse<Double>>(ApiResponse.Loading)
    val currencyState: StateFlow<ApiResponse<Double>> = _currencyState

    private val _selectedCurrency = MutableStateFlow(CurrencyPreferenceManager.DEFAULT_CURRENCY)
    val selectedCurrency: StateFlow<String> = _selectedCurrency

    init {
        viewModelScope.launch {
            val preferredCurrency = try {
                repository.getPreferredCurrency()
            } catch (e: Exception) {
                CurrencyPreferenceManager.DEFAULT_CURRENCY
            }
            _selectedCurrency.value = preferredCurrency
            selectCurrency(preferredCurrency)
        }
    }

    fun selectCurrency(currencyCode: String) {
        viewModelScope.launch {
            _currencyState.value = ApiResponse.Loading
            try {
                repository.setPreferredCurrency(currencyCode)
                _selectedCurrency.value = currencyCode
                Common.currencyCode = when(currencyCode){
                    "EGP" -> Currency.EGP
                    "EUR" -> Currency.EUR
                    "SAR" -> Currency.SAR
                    "USD" -> Currency.USD
                    else-> Currency.EGP
                }
                val rate = if (currencyCode == CurrencyPreferenceManager.DEFAULT_CURRENCY) {
                    1.0
                } else {
                    repository.getCurrencyRate(
                        baseCurrency = CurrencyPreferenceManager.DEFAULT_CURRENCY,
                        targetCurrency = currencyCode
                    )
                }
                _currencyState.value = ApiResponse.Success(rate)
            } catch (e: Exception) {
                _currencyState.value = ApiResponse.Failure(e)
            }
        }
    }
}
