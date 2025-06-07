package com.example.yallabuy_user.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.yallabuy_user.settings.model.remote.CurrencyPreferenceManager
import com.example.yallabuy_user.settings.model.repository.ICurrencyRepository
import com.example.yallabuy_user.utilities.ApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

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

class CurrencyViewModelFactory(
    private val repository: ICurrencyRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CurrencyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CurrencyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}