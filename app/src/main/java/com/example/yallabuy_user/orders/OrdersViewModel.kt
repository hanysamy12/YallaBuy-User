package com.example.yallabuy_user.orders

import androidx.lifecycle.ViewModel
import com.example.yallabuy_user.data.models.OrdersItem
import com.example.yallabuy_user.repo.RepositoryInterface
import com.example.yallabuy_user.utilities.ApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class OrdersViewModel(private val repository: RepositoryInterface) : OrdersViewModelInterface,
    ViewModel() {
        private val _orders = MutableStateFlow<ApiResponse<List<OrdersItem>>>(ApiResponse.Loading)
        val orders: MutableStateFlow<ApiResponse<List<OrdersItem>>> = _orders

    override suspend fun getPreviousOrders(userID: Long) {
        try {
            repository.getPreviousOrders(userID)
                .map { it.orders.orEmpty().filterNotNull() }
                .catch { e -> _orders.value = ApiResponse.Failure(e) }
                .collect{ orders -> _orders.value = ApiResponse.Success(orders) }
        } catch(e : Exception){
            _orders.value = ApiResponse.Failure(e)
        }
    }

}