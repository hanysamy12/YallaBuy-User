package com.example.yallabuy_user.cart.viewmodel

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yallabuy_user.data.models.cart.CustomerTagUpdate
import com.example.yallabuy_user.data.models.cart.DraftOrderBody
import com.example.yallabuy_user.data.models.cart.DraftOrderCart
import com.example.yallabuy_user.data.models.cart.DraftOrderResponse
import com.example.yallabuy_user.data.models.cart.LineItem
import com.example.yallabuy_user.data.models.cart.UpdateCustomerBody
import com.example.yallabuy_user.repo.RepositoryInterface
import com.example.yallabuy_user.utilities.ApiResponse
import com.example.yallabuy_user.utilities.Currency
import com.example.yallabuy_user.utilities.CurrencyConversionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CartViewModel(private val cartRepository: RepositoryInterface,
                    private val currencyConversionManager: CurrencyConversionManager
) : ViewModel() {

    private val _cartState = MutableStateFlow<ApiResponse<DraftOrderBody>>(ApiResponse.Loading)
    val cartState: StateFlow<ApiResponse<DraftOrderBody>> = _cartState.asStateFlow()

    private val _draftOrders =
        MutableStateFlow<ApiResponse<DraftOrderResponse>>(ApiResponse.Loading)
    val draftOrders: StateFlow<ApiResponse<DraftOrderResponse>> = _draftOrders.asStateFlow()

    private val _showSignUpDialog = MutableStateFlow(false)
    val showSignUpDialog: StateFlow<Boolean> = _showSignUpDialog.asStateFlow()

    private val _showOutOfStockDialog = MutableStateFlow(false)
    val showOutOfStockDialog: StateFlow<Boolean> = _showOutOfStockDialog.asStateFlow()

    val cartTotalInPreferredCurrency = MutableStateFlow<Double?>(null)
    val convertedPrices = mutableStateMapOf<Long, String>()

    private val _cartTotalRaw = MutableStateFlow<Double?>(null)
    val cartTotalRaw: StateFlow<Double?> = _cartTotalRaw.asStateFlow()

    private val _preferredCurrency = MutableStateFlow(Currency.EGP)
    val preferredCurrency: StateFlow<Currency> = _preferredCurrency


    private fun fetchCartByDraftOrderId(draftOrderId: Long) {
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
                      // _cartState.emit(ApiResponse.Success(data = ))
                        _draftOrders.emit(ApiResponse.Success(DraftOrderResponse(emptyList())))
                    }
                }
            } catch (e: Exception) {
                _cartState.value = ApiResponse.Failure(e)
            }
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

    fun removeItemFromCart(draftOrderId: Long, variantId: Long , customerId : Long) {
        viewModelScope.launch {
            val currentDraft =
                (_draftOrders.value as? ApiResponse.Success)?.data?.draftOrderCarts?.firstOrNull { it.id == draftOrderId }
            currentDraft?.let { draft ->
                val updatedLineItems =
                    draft.lineItems.filter { it.variantID != variantId }.toMutableList()
                if (updatedLineItems.isEmpty()) {
                    cartRepository.deleteDraftOrderCart(draftOrderId)
                        .catch { e -> _cartState.value = ApiResponse.Failure(e) }
                        .collect {
                            _cartState.value = ApiResponse.Success(DraftOrderBody(draft))
                            removeLocalDraftOrder(draftOrderId)
                            cartRepository.updateCustomerTags(customerId , UpdateCustomerBody(
                                CustomerTagUpdate(customerId , "")
                            ))
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

    fun convertItemPrices(draftOrders: List<DraftOrderCart>) {
        viewModelScope.launch {
            draftOrders.flatMap { it.lineItems }.forEach { item ->
                val price = item.price.toDoubleOrNull()?.times(item.quantity) ?: 0.0
                val converted = currencyConversionManager.convertAmount(price)
                convertedPrices[item.variantID] = "%.2f".format(converted)
            }
        }
    }


}