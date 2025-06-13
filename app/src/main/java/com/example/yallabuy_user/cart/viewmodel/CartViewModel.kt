package com.example.yallabuy_user.cart.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yallabuy_user.data.models.cart.DraftOrderBody
import com.example.yallabuy_user.data.models.cart.DraftOrderResponse
import com.example.yallabuy_user.data.models.cart.LineItem
import com.example.yallabuy_user.repo.RepositoryInterface
import com.example.yallabuy_user.utilities.ApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CartViewModel(private val cartRepository: RepositoryInterface) : ViewModel() {

    private val _cartState = MutableStateFlow<ApiResponse<DraftOrderBody>>(ApiResponse.Loading)
    val cartState: StateFlow<ApiResponse<DraftOrderBody>> = _cartState.asStateFlow()

    private val _draftOrders =
        MutableStateFlow<ApiResponse<DraftOrderResponse>>(ApiResponse.Loading)
    val draftOrders: StateFlow<ApiResponse<DraftOrderResponse>> = _draftOrders.asStateFlow()

    private val _showSignUpDialog = MutableStateFlow(false)
    val showSignUpDialog: StateFlow<Boolean> = _showSignUpDialog.asStateFlow()

    fun addToCart(draftOrder: DraftOrderBody, customerId: Long) {
        if (customerId == 0L) {
            _showSignUpDialog.value = true
            return
        }
        viewModelScope.launch {
            _cartState.value = ApiResponse.Loading
            cartRepository.createDraftOrderCart(draftOrder)
                .catch { e -> _cartState.value = ApiResponse.Failure(e) }
                .collect { createdOrder ->
                    _cartState.value = ApiResponse.Success(createdOrder)
                }
        }
    }

    fun fetchCart(customerId: Long) {
        viewModelScope.launch {
            _draftOrders.value = ApiResponse.Loading

            cartRepository.getDraftOrderCart()
                .catch { e ->
                    _draftOrders.value = ApiResponse.Failure(e)
                }
                .collect { response ->
                    Log.i("TAG", "fetchCart: our draft orders${response.draftOrders.size} ")
                    val filteredOrders =
                        response.draftOrders.filter { it.customer?.id == customerId }
                    Log.i("TAG", "fetchCart: filtered draft order count = ${filteredOrders.size}")

                    _draftOrders.value =
                        ApiResponse.Success(DraftOrderResponse(filteredOrders.toMutableList()))
                    Log.i("TAG", "fetchCart: our draft orders second ${draftOrders.value} ")

                }
        }
    }

    fun increaseItemQuantity(draftOrderId: Long, variantId: Long) {
        viewModelScope.launch {

            val currentDraft =
                (_draftOrders.value as? ApiResponse.Success)?.data?.draftOrders?.firstOrNull { it.id == draftOrderId }
            currentDraft?.let { draft ->
                val updatedLineItems = draft.lineItems.map { item ->
                    if (item.variantID == variantId) item.copy(quantity = item.quantity + 1)
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

    fun decreaseItemQuantity(draftOrderId: Long, variantId: Long) {
        viewModelScope.launch {
            val currentDraft =
                (_draftOrders.value as? ApiResponse.Success)?.data?.draftOrders?.firstOrNull { it.id == draftOrderId }
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
                (_draftOrders.value as? ApiResponse.Success)?.data?.draftOrders?.firstOrNull { it.id == draftOrderId }
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
            (_draftOrders.value as? ApiResponse.Success)?.data?.draftOrders?.toMutableList()
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
            (_draftOrders.value as? ApiResponse.Success)?.data?.draftOrders?.toMutableList()
                ?: return
        val updatedList = currentList.filterNot { it.id == draftOrderId }
        _draftOrders.value = ApiResponse.Success(DraftOrderResponse(updatedList.toMutableList()))
    }
}