package com.example.yallabuy_user.settings.model.local

data class CurrencyUiState(
    val amountInput: String = "",
    val fromCurrency: String = "USD",
    val toCurrency: String = "EGP",
    val availableCurrencies: List<String> = listOf("USD", "EUR", "EGP", "SAR"),
    val conversionResult: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    private val rates: Map<String, Double>? = null
) {
    val areRatesAvailable: Boolean = rates != null
}
