package com.example.yallabuy_user.orders

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yallabuy_user.authentication.login.CustomerIdPreferences
import com.example.yallabuy_user.data.models.Coupon.CouponValidationResult
import com.example.yallabuy_user.data.models.Coupon.DiscountCodeCoupon
import com.example.yallabuy_user.data.models.CreateCustomer
import com.example.yallabuy_user.data.models.CreateLineItem
import com.example.yallabuy_user.data.models.CreateOrder
import com.example.yallabuy_user.data.models.CreateOrderRequest
import com.example.yallabuy_user.data.models.CreateShippingAddress
import com.example.yallabuy_user.data.models.CreateTransaction
import com.example.yallabuy_user.data.models.DiscountCode
import com.example.yallabuy_user.data.models.cart.DraftOrderCart
import com.example.yallabuy_user.data.models.settings.Address
import com.example.yallabuy_user.repo.RepositoryInterface
import com.example.yallabuy_user.utilities.ApiResponse
import com.example.yallabuy_user.utilities.CurrencyConversionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val TAG = "NewOrderViewModel"

class NewOrderViewModel(
    private val repository: RepositoryInterface,
    private val currencyConversionManager: CurrencyConversionManager
) : ViewModel() {

    private var _cartOrder = MutableStateFlow<ApiResponse<DraftOrderCart>>(ApiResponse.Loading)
    val cartOrder: MutableStateFlow<ApiResponse<DraftOrderCart>> = _cartOrder
    private var _address = MutableStateFlow<ApiResponse<List<Address>>>(ApiResponse.Loading)
    val address: MutableStateFlow<ApiResponse<List<Address>>> = _address

    val convertedPrices = mutableStateMapOf<Long, String>()
    val cartTotalInPreferredCurrency = MutableStateFlow<Double?>(null)


    private val _couponValidationResult = MutableStateFlow<CouponValidationResult?>(null)
    val couponValidationResult: StateFlow<CouponValidationResult?> = _couponValidationResult.asStateFlow()

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
                  //  email = customerEmail
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
                //fulfillmentStatus = "fulfilled",
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun validateCoupon(
        code: String,
        cartTotal: Double,
        homeCoupons: List<DiscountCodeCoupon>? = null
    ) {
        viewModelScope.launch {
            try {
                val matchedCoupon = homeCoupons?.firstOrNull { it.code.equals(code, ignoreCase = true) }

                val finalCoupon = matchedCoupon ?: run {
                    val priceRules = repository.fetchPriceRules().first()
                    var foundCoupon: DiscountCodeCoupon? = null

                    for (rule in priceRules) {
                        val discountCodes = repository.getAllCouponsForRule(rule.id).first()
                        val match = discountCodes.firstOrNull { it.code.equals(code, ignoreCase = true) }
                        if (match != null) {
                            foundCoupon = match
                            break
                        }
                    }
                    foundCoupon
                }

                if (finalCoupon == null) {
                    _couponValidationResult.value = CouponValidationResult(
                        isValid = false,
                        message = "Invalid coupon code."
                    )
                    return@launch
                }

                val priceRules = repository.fetchPriceRules().first()
                val rule = priceRules.firstOrNull { it.id == finalCoupon.priceRuleId }

                if (rule == null) {
                    _couponValidationResult.value = CouponValidationResult(
                        isValid = false,
                        message = "Coupon rule not found."
                    )
                    return@launch
                }

                val formatter = DateTimeFormatter.ISO_DATE_TIME
                val now = LocalDateTime.now()
                val startDate = LocalDateTime.parse(rule.startsAt, formatter)
                val endDate = rule.endsAt?.let { LocalDateTime.parse(it, formatter) }

                if (now.isBefore(startDate) || (endDate != null && now.isAfter(endDate))) {
                    _couponValidationResult.value = CouponValidationResult(
                        isValid = false,
                        message = "Coupon expired or not yet active."
                    )
                    return@launch
                }

                val discountAmount = when (rule.valueType.lowercase()) {
                    "fixed_amount" -> kotlin.math.abs(rule.value.toDoubleOrNull() ?: 0.0)
                    "percentage" -> {
                        val percent = kotlin.math.abs(rule.value.toDoubleOrNull() ?: 0.0)
                        (percent / 100.0) * cartTotal
                    }
                    else -> 0.0
                }

                _couponValidationResult.value = CouponValidationResult(
                    isValid = true,
                    message = "Coupon applied successfully!",
                    discountValue = discountAmount,
                    valueType = rule.valueType
                )

            } catch (e: Exception) {
                _couponValidationResult.value = CouponValidationResult(
                    isValid = false,
                    message = "Error validating coupon: ${e.message ?: "Unknown error"}"
                )
            }
        }
    }

    fun convertItemPrices(order: DraftOrderCart) {
        viewModelScope.launch {
            val convertedTotal = order.lineItems.sumOf { lineItem ->
                val price = lineItem.price.toDoubleOrNull()?.times(lineItem.quantity) ?: 0.0
                val converted = currencyConversionManager.convertAmount(price)
                convertedPrices[lineItem.variantID] = "%.2f".format(converted)
                converted
            }
            cartTotalInPreferredCurrency.value = convertedTotal
        }
    }

    suspend fun convertTotalAmount(amount: Double): Double {
        val converted = currencyConversionManager.convertAmount(amount)
        cartTotalInPreferredCurrency.value = converted
        return converted
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