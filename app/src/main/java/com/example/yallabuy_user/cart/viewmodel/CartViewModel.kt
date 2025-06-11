package com.example.yallabuy_user.cart.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yallabuy_user.data.models.cart.DraftOrderBody
import com.example.yallabuy_user.data.models.productInfo.Variant
import com.example.yallabuy_user.repo.Repository
import com.example.yallabuy_user.repo.RepositoryInterface
import com.example.yallabuy_user.utilities.ApiResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch


class CartViewModel(private val cartRepository: RepositoryInterface) : ViewModel() {

    private val _cartState = MutableStateFlow<ApiResponse<DraftOrderBody>>(ApiResponse.Loading)
    val cartState: StateFlow<ApiResponse<DraftOrderBody>> = _cartState.asStateFlow()

    fun addToCart(draftOrder: DraftOrderBody) {
        Log.d("CartViewModel", "Adding to cart: ${Gson().toJson(draftOrder)}")

        viewModelScope.launch {
            _cartState.value = ApiResponse.Loading
            cartRepository.createDraftOrder(draftOrder)
                .catch { e ->
                    _cartState.value = ApiResponse.Failure(e)
                }
                .collect { createdOrder ->
                    Log.d("CartViewModel", "Draft order created successfully")
                    _cartState.value = ApiResponse.Success(createdOrder)
                }
        }
    }

    fun fetchCart(draftOrderId: Long) {
        viewModelScope.launch {
            _cartState.value = ApiResponse.Loading
            cartRepository.getDraftOrder(draftOrderId)
                .catch { e ->
                    _cartState.value = ApiResponse.Failure(e)
                }
                .collect { draftOrder ->
                    _cartState.value = ApiResponse.Success(draftOrder)
                }
        }
    }
}