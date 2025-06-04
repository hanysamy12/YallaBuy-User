package com.example.yallabuy_user.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yallabuy_user.settings.model.local.CurrencyUiState
import com.example.yallabuy_user.settings.model.repository.CurrencyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class CurrencyViewModel(
    private val currencyRepository: CurrencyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CurrencyUiState())
    val uiState: StateFlow<CurrencyUiState> = _uiState.asStateFlow()

    private var currentRates: Map<String, Double>? = null

    init {
        fetchRates()
    }

    /*
 ^ – Start of the string
 \d* – Zero or more digits
 \.? – Optional decimal point (.), escaped as \\. in Kotlin strings
 \d* – Zero or more digits after the decimal point
 $ – End of the string
     */
    fun onAmountChange(newAmount: String) {
        if (newAmount.matches(Regex("^\\d*\\.?\\d*$"))) {
            _uiState.update { it.copy(amountInput = newAmount, conversionResult = "", errorMessage = null) }
        }
    }

    fun onFromCurrencySelected(currency: String) {
        _uiState.update { it.copy(fromCurrency = currency, conversionResult = "", errorMessage = null) }
         performConversion()
    }

    fun onToCurrencySelected(currency: String) {
        _uiState.update { it.copy(toCurrency = currency, conversionResult = "", errorMessage = null) }
         performConversion()
    }

    fun onConvertClicked() {
        performConversion()
    }

    fun onRetryFetchClicked() {
        fetchRates(forceRefresh = true)
    }

    private fun fetchRates(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                currentRates = currencyRepository.getLatestRates(forceRefresh)
                if (currentRates == null) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Failed to load exchange rates. Check connection.") }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = null) }
                    if (_uiState.value.amountInput.isNotEmpty()) {
                        performConversion()
                    }
                }
            } catch (e: Exception) {
                currentRates = null
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error: ${e.localizedMessage ?: "Unknown error"}") }
            }
        }
    }

    private fun performConversion() {
        val state = _uiState.value
        val rates = currentRates
        val amount = state.amountInput.toDoubleOrNull()

        if (rates == null) {
            _uiState.update { it.copy(conversionResult = "", errorMessage = "Rates not available. Try fetching again.") }
            fetchRates()
            return
        }

        if (amount == null || amount <= 0) {
            _uiState.update { it.copy(conversionResult = "", errorMessage = if (state.amountInput.isNotEmpty()) "Invalid amount" else null) }
            return
        }

        val fromRate = rates[state.fromCurrency]
        val toRate = rates[state.toCurrency]

        if (fromRate == null || toRate == null || fromRate <= 0) {
            _uiState.update { it.copy(conversionResult = "", errorMessage = "Conversion rate error for selected currencies.") }
            return
        }

        val resultValue = amount * (toRate / fromRate)

        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 2
        }
        val formattedResult = "${numberFormat.format(resultValue)} ${state.toCurrency}"

        _uiState.update { it.copy(conversionResult = formattedResult, errorMessage = null) }
    }
}
