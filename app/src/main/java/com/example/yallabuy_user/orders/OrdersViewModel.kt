package com.example.yallabuy_user.orders

import androidx.lifecycle.ViewModel
import com.example.yallabuy_user.data.models.Order
import com.example.yallabuy_user.data.models.OrdersItem
import com.example.yallabuy_user.repo.RepositoryInterface
import com.example.yallabuy_user.utilities.ApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class OrdersViewModel(private val repository: RepositoryInterface) : OrdersViewModelInterface,
    ViewModel() {
    private val _orders = MutableStateFlow<ApiResponse<List<OrdersItem>>>(ApiResponse.Loading)
    val orders: MutableStateFlow<ApiResponse<List<OrdersItem>>> = _orders

    private val _orderProducts = MutableStateFlow<ApiResponse<Order>>(ApiResponse.Loading)
    val orderProducts: MutableStateFlow<ApiResponse<Order>> = _orderProducts

    override suspend fun getPreviousOrders(userID: Long) {
        try {
            repository.getPreviousOrders(userID).map { it.orders.orEmpty().filterNotNull() }
                .catch { e -> _orders.value = ApiResponse.Failure(e) }
                .collect { orders -> _orders.value = ApiResponse.Success(orders) }
        } catch (e: Exception) {
            _orders.value = ApiResponse.Failure(e)
        }
    }

    override suspend fun getOrderById(orderID: Long?) {
        try {
            if (orderID != null) {
                repository.getOrderById(orderID).map { it.order }
                    .catch { e -> _orderProducts.value = ApiResponse.Failure(e) }.collect { order ->
                        if (order != null) {
                            _orderProducts.value = ApiResponse.Success(order)
                            val orderWithImage = addImgURLToOrderProducts(order)
                            _orderProducts.value = ApiResponse.Success(orderWithImage)
                        } else {
                            _orderProducts.value =
                                ApiResponse.Failure(NullPointerException("Order not found"))
                        }
                    }
            }
        } catch (e: Exception) {
            _orderProducts.value = ApiResponse.Failure(e)
        }
    }

    private suspend fun getProductImageUrl(productId: Long?): String? {
        if (productId == null) return null
        return try {
            val product = repository.getProductById(productId).first()
            product.product.image.src
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun addImgURLToOrderProducts(order: Order): Order {
        val lineItemsWithImages = order.lineItems?.mapNotNull { lineItem ->
            lineItem?.let { item ->
                try {
                    val imageUrl = getProductImageUrl(item.productId)
                    item.copy(imgUrl = imageUrl)
                } catch (e: Exception) {
                    item
                }
            }
        }
        return order.copy(lineItems = lineItemsWithImages)
    }


}