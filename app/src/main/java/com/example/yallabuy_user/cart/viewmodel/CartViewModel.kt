package com.example.yallabuy_user.cart.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yallabuy_user.data.models.Coupon.CouponValidationResult
import com.example.yallabuy_user.data.models.Coupon.DiscountCodeCoupon
import com.example.yallabuy_user.data.models.cart.DraftOrderBody
import com.example.yallabuy_user.data.models.cart.DraftOrderCart
import com.example.yallabuy_user.data.models.cart.DraftOrderResponse
import com.example.yallabuy_user.data.models.cart.LineItem
import com.example.yallabuy_user.repo.RepositoryInterface
import com.example.yallabuy_user.utilities.ApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CartViewModel(private val cartRepository: RepositoryInterface) : ViewModel() {

    private val _cartState = MutableStateFlow<ApiResponse<DraftOrderBody>>(ApiResponse.Loading)
    val cartState: StateFlow<ApiResponse<DraftOrderBody>> = _cartState.asStateFlow()

    private val _draftOrders =
        MutableStateFlow<ApiResponse<DraftOrderResponse>>(ApiResponse.Loading)
    val draftOrders: StateFlow<ApiResponse<DraftOrderResponse>> = _draftOrders.asStateFlow()

    private val _showSignUpDialog = MutableStateFlow(false)
    val showSignUpDialog: StateFlow<Boolean> = _showSignUpDialog.asStateFlow()

    private val _showOutOfStockDialog = MutableStateFlow(false)
    val showOutOfStockDialog: StateFlow<Boolean> = _showOutOfStockDialog.asStateFlow()

    private val _couponValidationResult = MutableStateFlow<CouponValidationResult?>(null)
    val couponValidationResult: StateFlow<CouponValidationResult?> = _couponValidationResult.asStateFlow()

//    fun fetchCart(customerId: Long) {
//        viewModelScope.launch {
//            _draftOrders.value = ApiResponse.Loading
//
//            cartRepository.getDraftOrderCart()
//                .catch { e ->
//                    _draftOrders.value = ApiResponse.Failure(e)
//                }
//                .collect { response ->
//                    Log.i("TAG", "fetchCart: our draft orders${response.draftOrderCarts.size} ")
//                    val filteredOrders =
//                        response.draftOrderCarts.filter { it.customer?.id == customerId }
//                    Log.i("TAG", "fetchCart: filtered draft order count = ${filteredOrders.size}")
//
//                    _draftOrders.value =
//                        ApiResponse.Success(DraftOrderResponse(filteredOrders.toMutableList()))
//                    Log.i("TAG", "fetchCart: our draft orders second ${draftOrders.value} ")
//
//                }
//        }
//    }

    fun fetchCartByDraftOrderId(draftOrderId: Long) {
        viewModelScope.launch {
            _cartState.value = ApiResponse.Loading
            try {
                cartRepository.getDraftOrderCart(draftOrderId)
                    .catch { e -> _cartState.value = ApiResponse.Failure(e) }
                    .collect { draftOrderBody ->
                        _cartState.value = ApiResponse.Success(draftOrderBody)

                        _draftOrders.value = ApiResponse.Success(
                            DraftOrderResponse(mutableListOf(draftOrderBody.draftOrderCart))
                        )
                    }
            } catch (e: Exception) {
                _cartState.value = ApiResponse.Failure(e)
            }
        }
    }

    fun getCustomerByIdAndFetchCart(customerId: Long) {
        viewModelScope.launch {
            try {
                cartRepository.getUserById(customerId).collect { customerResponse ->
                    val tag = customerResponse.customer.tags
                    val draftOrderId = tag.toLongOrNull()

                    if (draftOrderId != null) {
                        fetchCartByDraftOrderId(draftOrderId)
                    } else {
                        _cartState.value = ApiResponse.Failure(Throwable("No cart associated with customer"))
                    }
                }
            } catch (e: Exception) {
                _cartState.value = ApiResponse.Failure(e)
            }
        }
    }

    private suspend fun getDraftOrderById(id: Long): DraftOrderCart? {
        return (_draftOrders.value as? ApiResponse.Success)
            ?.data?.draftOrderCarts?.firstOrNull { it.id == id }
            ?: run {
                var result: DraftOrderCart? = null
                cartRepository.getDraftOrderCart(id)
                    .catch { e -> _cartState.value = ApiResponse.Failure(e) }
                    .collect { response ->
                        result = response.draftOrderCart
                    }
                result
            }
    }

    fun increaseItemQuantity(draftOrderId: Long, variantId: Long) {
        viewModelScope.launch {
            val currentDraft = (_draftOrders.value as? ApiResponse.Success)
                ?.data?.draftOrderCarts?.firstOrNull { it.id == draftOrderId }

            currentDraft?.let { draft ->
                val item = draft.lineItems.firstOrNull { it.variantID == variantId }

                if (item != null) {
                    cartRepository.getProductVariantById(variantId)
                        .catch { e -> _cartState.value = ApiResponse.Failure(e) }
                        .collect { productVariant ->
                            val availableQty = productVariant.variant.inventoryQuantity
                            val currentQty = item.quantity

                            if (currentQty < availableQty) {
                                val updatedLineItems = draft.lineItems.map {
                                    if (it.variantID == variantId) it.copy(quantity = it.quantity + 1)
                                    else it
                                }.toMutableList()

                                val updatedDraftOrder = DraftOrderBody(draft.copy(lineItems = updatedLineItems))

                                _cartState.value = ApiResponse.Loading
                                cartRepository.updateDraftOrder(draftOrderId, updatedDraftOrder)
                                    .catch { e -> _cartState.value = ApiResponse.Failure(e) }
                                    .collect { response ->
                                        _cartState.value = ApiResponse.Success(response)
                                        updateLocalDraftOrder(draftOrderId, updatedLineItems)
                                    }
                            } else {
                                _showOutOfStockDialog.value = true
                            }
                        }
                }
            }
        }
    }

    fun dismissOutOfStockDialog() {
        _showOutOfStockDialog.value = false
    }

    fun decreaseItemQuantity(draftOrderId: Long, variantId: Long) {
        viewModelScope.launch {
            val currentDraft =
                (_draftOrders.value as? ApiResponse.Success)?.data?.draftOrderCarts?.firstOrNull { it.id == draftOrderId }
            currentDraft?.let { draft ->
                val updatedLineItems = draft.lineItems.map { item ->
                    if (item.variantID == variantId && item.quantity > 1) item.copy(quantity = item.quantity - 1)
                    else item
                }.toMutableList()

                val updatedDraftOrder = DraftOrderBody(draft.copy(lineItems = updatedLineItems))

                _cartState.value = ApiResponse.Loading
                cartRepository.updateDraftOrder(draftOrderId, updatedDraftOrder)
                    .catch { e -> _cartState.value = ApiResponse.Failure(e) }
                    .collect { response ->
                        _cartState.value = ApiResponse.Success(response)
                        updateLocalDraftOrder(draftOrderId, updatedLineItems)
                    }
            }
        }
    }

    fun removeItemFromCart(draftOrderId: Long, variantId: Long) {
        viewModelScope.launch {
            val currentDraft =
                (_draftOrders.value as? ApiResponse.Success)?.data?.draftOrderCarts?.firstOrNull { it.id == draftOrderId }
            currentDraft?.let { draft ->
                val updatedLineItems =
                    draft.lineItems.filter { it.variantID != variantId }.toMutableList()

                _cartState.value = ApiResponse.Loading

                if (updatedLineItems.isEmpty()) {
                    cartRepository.deleteDraftOrderCart(draftOrderId)
                        .catch { e -> _cartState.value = ApiResponse.Failure(e) }
                        .collect {
                            _cartState.value = ApiResponse.Success(DraftOrderBody(draft))
                            removeLocalDraftOrder(draftOrderId)
                        }
                } else {
                    val updatedDraftOrder = DraftOrderBody(draft.copy(lineItems = updatedLineItems))
                    cartRepository.updateDraftOrder(draftOrderId, updatedDraftOrder)
                        .catch { e -> _cartState.value = ApiResponse.Failure(e) }
                        .collect { response ->
                            _cartState.value = ApiResponse.Success(response)
                            updateLocalDraftOrder(draftOrderId, updatedLineItems)
                        }
                }
            }
        }
    }

    fun dismissSignUpDialog() {
        _showSignUpDialog.value = false
    }

    private fun updateLocalDraftOrder(draftOrderId: Long, updatedLineItems: List<LineItem>) {
        val currentList =
            (_draftOrders.value as? ApiResponse.Success)?.data?.draftOrderCarts?.toMutableList()
                ?: return
        val index = currentList.indexOfFirst { it.id == draftOrderId }
        if (index != -1) {
            val updatedDraft = currentList[index].copy(lineItems = updatedLineItems.toMutableList())
            currentList[index] = updatedDraft
            _draftOrders.value = ApiResponse.Success(DraftOrderResponse(currentList))
        }
    }

    private fun removeLocalDraftOrder(draftOrderId: Long) {
        val currentList =
            (_draftOrders.value as? ApiResponse.Success)?.data?.draftOrderCarts?.toMutableList()
                ?: return
        val updatedList = currentList.filterNot { it.id == draftOrderId }
        _draftOrders.value = ApiResponse.Success(DraftOrderResponse(updatedList.toMutableList()))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun validateCoupon(
        code: String,
        cartTotal: Double,
        homeCoupons: List<DiscountCodeCoupon>? = null
    ) {
        viewModelScope.launch {
            try {
                // Step 1: Match from home coupons
                val matchedCoupon = homeCoupons?.firstOrNull { it.code.equals(code, ignoreCase = true) }

                // Step 2: If not found in homeCoupons, search backend rules
                val finalCoupon = matchedCoupon ?: run {
                    val priceRules = cartRepository.fetchPriceRules().first()
                    var foundCoupon: DiscountCodeCoupon? = null

                    for (rule in priceRules) {
                        val discountCodes = cartRepository.getAllCouponsForRule(rule.id).first()
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

                // Step 3: Fetch associated PriceRule
                val priceRules = cartRepository.fetchPriceRules().first()
                val rule = priceRules.firstOrNull { it.id == finalCoupon.priceRuleId }

                if (rule == null) {
                    _couponValidationResult.value = CouponValidationResult(
                        isValid = false,
                        message = "Coupon rule not found."
                    )
                    return@launch
                }

                // Step 4: Validate start and end dates
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

                // Step 5: Compute discount amount based on valueType
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

}