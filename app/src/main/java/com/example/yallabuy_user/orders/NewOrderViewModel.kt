package com.example.yallabuy_user.orders

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.yallabuy_user.data.models.CreateCustomer
import com.example.yallabuy_user.data.models.CreateLineItem
import com.example.yallabuy_user.data.models.CreateOrder
import com.example.yallabuy_user.data.models.CreateOrderRequest
import com.example.yallabuy_user.data.models.CreateShippingAddress
import com.example.yallabuy_user.data.models.CreateTransaction
import com.example.yallabuy_user.data.models.DiscountCode
import com.example.yallabuy_user.data.models.cart.DraftOrderCart
import com.example.yallabuy_user.repo.RepositoryInterface
import com.example.yallabuy_user.utilities.ApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private const val TAG = "NewOrderViewModel"

class NewOrderViewModel(private val repository: RepositoryInterface) : ViewModel() {
    private var _cartOrder = MutableStateFlow<ApiResponse<DraftOrderCart>>(ApiResponse.Loading)
    val cartOrder: MutableStateFlow<ApiResponse<DraftOrderCart>> = _cartOrder

    suspend fun getDraftOrder(id: Long) {
        try {
            repository.getDraftOrderById(id).map { draftOrderBody -> draftOrderBody.draftOrderCart }
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

    suspend fun postNewOrder() {
        // Create Retrofit request object
        val orderRequest = CreateOrderRequest(
            order = CreateOrder(
                lineItems = listOf(
                    CreateLineItem(variantId = 51762978160958, quantity = 2),
                    CreateLineItem(variantId = 51762995659070, quantity = 1)
                ),
                discountCodes = listOf(DiscountCode(code = "ZIAD40")),
                shippingAddress = CreateShippingAddress(
                    firstName = "Hany",
                    lastName = "Samy",
                    address1 = "Sinoris",
                    city = "Fayoum",
                    country = "Egypt",
                    phone = "+1557500033"
                ),
                customer = CreateCustomer(id = 8792449548606, email = "honi76034@gmail.com"),
                transactions = listOf(
                    CreateTransaction(
                        kind = "sale",
                        status = "success",
                        amount = "464.00",
                        gateway = "credit_card"
                    )
                ),
                financialStatus = "paid",
                fulfillmentStatus = "fulfilled",
                sendReceipt = true,
                sendFulfillmentReceipt = true,
                currency = "EGP"
            )
        )
        try {
            repository.createOrder(orderRequest)
                .catch { Log.i(TAG, "postNewOrder: Failed $it") }
                .collect { Log.i(TAG, "postNewOrder Succeeded: ORDER ID: ${it.order?.id}") }
        } catch (e: Exception) {
            Log.i(TAG, "postNewOrder: Failed $e")

        }
    }
}