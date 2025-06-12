package com.example.yallabuy_user.orders

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.yallabuy_user.data.models.cart.DraftOrder
import com.example.yallabuy_user.repo.RepositoryInterface
import com.example.yallabuy_user.utilities.ApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private const val TAG = "NewOrderViewModel"
class NewOrderViewModel(private val repository: RepositoryInterface) : ViewModel() {
    private var _cartOrder = MutableStateFlow<ApiResponse<DraftOrder>>(ApiResponse.Loading)
    val cartOrder: MutableStateFlow<ApiResponse<DraftOrder>> = _cartOrder

    suspend fun getDraftOrder(id: Long) {
        try {
            repository.getDraftOrder(id).map { draftOrderBody -> draftOrderBody.draftOrder }
                .catch { e -> _cartOrder.value = ApiResponse.Failure(e) }
                .collect {
                    _cartOrder.value = ApiResponse.Success(it)
                }
        } catch (e: Exception) {
            _cartOrder.value = ApiResponse.Failure(e)
        }
    }

    suspend fun verifyCoupon(code: String) {
        Log.i(TAG, "verifyCoupon:$code ")
    }
    suspend fun postNewOrder(){
        Log.i(TAG, "postNewOrder: ")

    }
    }