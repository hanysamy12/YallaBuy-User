package com.example.yallabuy_user.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.yallabuy_user.settings.model.local.CurrencyUiState
import com.example.yallabuy_user.settings.model.remote.CurrencyPreferenceManager
import com.example.yallabuy_user.settings.model.repository.CurrencyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale


open class CurrencyViewModel(
    private val preferenceManager: CurrencyPreferenceManager
) : ViewModel() {


    val preferredCurrency: StateFlow<String> = preferenceManager.preferredCurrencyFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // keep flow active for 5s after last subscriber
            initialValue = CurrencyPreferenceManager.DEFAULT_CURRENCY
        )

    val availableCurrencies: List<String> = CurrencyPreferenceManager.SUPPORTED_CURRENCIES


      //calle it  when the user selects a new currency from the dropdown.
    fun onCurrencySelected(newCurrencyCode: String) {
        viewModelScope.launch {
            preferenceManager.setPreferredCurrency(newCurrencyCode)
        }
    }
}

//I will delete it after DI using hilt
class CurrencyViewModelFactory(
    private val preferenceManager: CurrencyPreferenceManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CurrencyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CurrencyViewModel(preferenceManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}