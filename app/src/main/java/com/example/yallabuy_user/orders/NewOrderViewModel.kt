package com.example.yallabuy_user.orders

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.yallabuy_user.authentication.login.CustomerIdPreferences
import com.example.yallabuy_user.data.models.CreateCustomer
import com.example.yallabuy_user.data.models.CreateLineItem
import com.example.yallabuy_user.data.models.CreateOrder
import com.example.yallabuy_user.data.models.CreateOrderRequest
import com.example.yallabuy_user.data.models.CreateShippingAddress
import com.example.yallabuy_user.data.models.CreateTransaction
import com.example.yallabuy_user.data.models.DiscountCode
import com.example.yallabuy_user.data.models.cart.DraftOrderCart
import com.example.yallabuy_user.data.models.settings.Address
import com.example.yallabuy_user.repo.ICurrencyRepository
import com.example.yallabuy_user.repo.RepositoryInterface
import com.example.yallabuy_user.utilities.ApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private const val TAG = "NewOrderViewModel"

class NewOrderViewModel(
    private val repository: RepositoryInterface,
    private val currencyRepository: ICurrencyRepository
) : ViewModel() {
    private var _cartOrder = MutableStateFlow<ApiResponse<DraftOrderCart>>(ApiResponse.Loading)
    val cartOrder: MutableStateFlow<ApiResponse<DraftOrderCart>> = _cartOrder
    private var _address = MutableStateFlow<ApiResponse<List<Address>>>(ApiResponse.Loading)
    val address: MutableStateFlow<ApiResponse<List<Address>>> = _address
    private var _currency = MutableStateFlow<ApiResponse<String>>(ApiResponse.Loading)


    private var customerId = -1L
    private var customerEmail = ""
    suspend fun getDraftOrder(id: Long) {
        try {
            repository.getDraftOrderCart(id).map { draftOrderBody -> draftOrderBody.draftOrderCart }
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

    suspend fun postNewOrder(
        items: List<CreateLineItem>,
        discountCode: String,
        shippingAddress: CreateShippingAddress,
        amount: String,
        getWay: String,
        financialStatus: String,
        context: Context
    ) {
        getCustomerData(context)
        val orderRequest = CreateOrderRequest(
            order = CreateOrder(
                lineItems = items,
                discountCodes = listOf(DiscountCode(code = discountCode)),
                shippingAddress = shippingAddress,
                customer = CreateCustomer(
                    id = customerId,
                    email = customerEmail
                ),//from Prefs
                transactions = listOf(
                    CreateTransaction(
                        kind = "sale",
                        status = "success",
                        amount = amount,
                        gateway = getWay
                    )
                ),
                financialStatus = financialStatus,
                fulfillmentStatus = "fulfilled",
                sendReceipt = true,
                sendFulfillmentReceipt = true,
                //currency = "EGP" //from Prefs or OrderCheckoutScreen
            )
        )
        Log.i(TAG, "postNewOrder: $orderRequest")
        try {
            repository.createOrder(orderRequest)
                .catch { Log.i(TAG, "postNewOrder: Failed $it") }
                .collect { Log.i(TAG, "postNewOrder Succeeded: ORDER ID: ${it.order?.id}") }
        } catch (e: Exception) {
            Log.i(TAG, "postNewOrder: Failed $e")

        }
    }

     private fun getCustomerData(context: Context) {
         customerId = CustomerIdPreferences.getData(context)
         customerEmail = "honi76034@gmail.com"
        Log.i(TAG, "getCustomerData: $customerId")
    }

    private suspend fun getCurrency(context: Context) {
        //currencyRepository.getCurrencyRate()
    }

    suspend fun getCustomerAddress(context: Context) {

        try {
            getCustomerData(context)
            repository.getAddresses(customerId).map { it.addresses }
                .catch { e -> Log.i(TAG, "getCustomerAddress: Failed $e") }
                .collect { addresses -> _address.value = ApiResponse.Success(addresses) }
        } catch (e: Exception) {
            address.value = ApiResponse.Failure(e)
        }
    }
}

//
//val orderRequest = CreateOrderRequest(
//    order = CreateOrder(
//        lineItems = listOf( //from OrderCheckoutScreen
//            CreateLineItem(variantId = 51762978160958, quantity = 2),
//            CreateLineItem(variantId = 51762995659070, quantity = 1)
//        ),
//        discountCodes = listOf(DiscountCode(code = "ZIAD40")),///from OrderCheckoutScreen
//        shippingAddress = CreateShippingAddress( //from OrderCheckoutScreen
//            firstName = "Hany",
//            lastName = "Samy",
//            address1 = "Sinoris",
//            city = "Fayoum",
//            country = "Egypt",
//            phone = "+1557500033"
//        ),
//        customer = CreateCustomer(
//            id = 8792449548606,
//            email = "honi76034@gmail.com"
//        ),//from Prefs
//        transactions = listOf(
//            CreateTransaction(
//                kind = "sale",
//                status = "success",
//                amount = "464.00",//from OrderCheckoutScreen
//                gateway = "credit_card"//from OrderCheckoutScreen
//            )
//        ),
//        financialStatus = "paid",
//        fulfillmentStatus = "fulfilled",
//        sendReceipt = true,
//        sendFulfillmentReceipt = true,
//        currency = "EGP" //from Prefs or OrderCheckoutScreen
//    )
//)