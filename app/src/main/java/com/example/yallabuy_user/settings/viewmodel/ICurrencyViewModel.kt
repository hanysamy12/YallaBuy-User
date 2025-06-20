package com.example.yallabuy_user.settings.viewmodel

import com.example.yallabuy_user.utilities.ApiResponse
import kotlinx.coroutines.flow.StateFlow

interface ICurrencyViewModel {
    val currencyState: StateFlow<ApiResponse<Double>>
    val selectedCurrency: StateFlow<String>
    fun selectCurrency(currencyCode: String)
}