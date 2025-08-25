package com.example.yallabuy_user.presentationLayer.settings.viewmodel

import com.example.yallabuy_user.presentationLayer.ApiResponse
import kotlinx.coroutines.flow.StateFlow

interface ICurrencyViewModel {
    val currencyState: StateFlow<ApiResponse<Double>>
    val selectedCurrency: StateFlow<String>
    fun selectCurrency(currencyCode: String)
}