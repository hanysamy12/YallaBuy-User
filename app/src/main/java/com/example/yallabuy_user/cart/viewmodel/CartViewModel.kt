package com.example.yallabuy_user.cart.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yallabuy_user.data.models.cart.Customer
import com.example.yallabuy_user.data.models.cart.DraftOrder
import com.example.yallabuy_user.data.models.cart.DraftOrderBody
import com.example.yallabuy_user.data.models.cart.LineItem
import com.example.yallabuy_user.cart.model.repo.ICartRepository
import com.example.yallabuy_user.data.models.productInfo.Variant
import com.example.yallabuy_user.utilities.ApiResponse
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class CartViewModel(
    private val cartRepository: ICartRepository
) : ViewModel() {

    private val _cartState = MutableStateFlow<ApiResponse<DraftOrder>>(ApiResponse.Loading)
    val cartState: StateFlow<ApiResponse<DraftOrder>> = _cartState

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage

    private var currentDraftOrderId: Long? = null

    fun initializeCart(draftOrderId: Long?) {
        if (draftOrderId != null && draftOrderId != 0L) {
            currentDraftOrderId = draftOrderId
            getCart(draftOrderId)
        } else {
            _cartState.value = ApiResponse.Success(
                DraftOrder(
                    Id = 0L,
                    note = "New Cart",
                    lineItems = mutableListOf(),
                    totalPrice = "0.0",
                    customer = Customer(id = 0L)
                )
            )
        }
    }


    fun addProductToCart(customerId: Long, variant: Variant, quantity: Int) {
        viewModelScope.launch {
            _cartState.value = ApiResponse.Loading

            val lineItem = LineItem(
                variantID = variant.id,
                productID = variant.product_id,
                title = variant.title,
                quantity = quantity.toLong(),
                name = variant.title,
                price = variant.price,
                properties = emptyList()
            )

            val draftOrder = DraftOrderBody(
                draftOrder = DraftOrder(
                    Id = 0L,
                    note = "Customer cart for customer $customerId",
                    lineItems = mutableListOf(lineItem),
                    totalPrice = "0.0", // Shopify calculates this
                    customer = Customer(id = customerId)
                )
            )

            val result = cartRepository.createDraftOrder(draftOrder)
            if (result.isSuccess) {
                val order = result.getOrNull()?.draftOrder
                if (order != null) {
                    currentDraftOrderId = order.Id
                    _cartState.value = ApiResponse.Success(order)
                    _snackbarMessage.emit("Cart created successfully!")
                } else {
                    _cartState.value = ApiResponse.Failure(Exception("Failed to get draft order from response."))
                    _snackbarMessage.emit("Error creating cart: Response body is null.")
                }
            } else {
                val error = result.exceptionOrNull()
                _cartState.value = ApiResponse.Failure(error ?: Exception("Unknown error creating cart."))
                _snackbarMessage.emit("Error creating cart: ${error?.message ?: "Unknown error"}")
            }
        }
    }

    fun getCart(draftOrderId: Long) {
        viewModelScope.launch {
            _cartState.value = ApiResponse.Loading
            val result = cartRepository.getDraftOrder(draftOrderId)

            if (result.isSuccess) {
                val order = result.getOrNull()?.draftOrder
                if (order != null) {
                    currentDraftOrderId = order.Id
                    _cartState.value = ApiResponse.Success(order)
                    _snackbarMessage.emit("Cart loaded successfully!")
                } else {
                    _cartState.value = ApiResponse.Failure(Exception("Failed to get draft order from response."))
                    _snackbarMessage.emit("Error loading cart: Response body is null.")
                }
            } else {
                val error = result.exceptionOrNull()
                _cartState.value = ApiResponse.Failure(error ?: Exception("Unknown error getting cart."))
                _snackbarMessage.emit("Error loading cart: ${error?.message ?: "Unknown error"}")
            }
        }
    }

    suspend fun updateCartItem(variant: Variant, newQuantity: Long) {
        val draftOrderId = currentDraftOrderId
        if (draftOrderId == null || draftOrderId == 0L) {
            _snackbarMessage.emit("No active cart to update. Please create or load a cart first.")
            return
        }

        val currentOrder = (cartState.value as? ApiResponse.Success)?.data
        if (currentOrder == null) {
            _snackbarMessage.emit("Current cart state is not available for update.")
            return
        }

        val updatedItems = currentOrder.lineItems.toMutableList()
        val existingItem = updatedItems.find { it.variantID == variant.id }

        if (newQuantity <= 0) {
            existingItem?.let {
                updatedItems.remove(it)
                _snackbarMessage.emit("${variant.title} removed from cart.")
            }
        } else {
            if (existingItem != null) {
                // Update quantity of existing item
                val index = updatedItems.indexOf(existingItem)
                updatedItems[index] = existingItem.copy(quantity = newQuantity)
                _snackbarMessage.emit("Quantity for ${variant.title} updated to $newQuantity.")
            } else {
                val newLineItem = LineItem(
                    variantID = variant.id,
                    productID = variant.product_id,
                    title = variant.title,
                    quantity = newQuantity,
                    name = variant.title,
                    price = variant.price,
                    properties = emptyList()
                )
                updatedItems.add(newLineItem)
                _snackbarMessage.emit("${variant.title} added to cart.")
            }
        }

        val updatedDraftOrder = currentOrder.copy(lineItems = updatedItems, totalPrice = "0.0")

        viewModelScope.launch {
            _cartState.value = ApiResponse.Loading // Indicate loading state
            val result = cartRepository.updateDraftOrder(draftOrderId, DraftOrderBody(updatedDraftOrder))
            if (result.isSuccess) {
                val order = result.getOrNull()?.draftOrder
                if (order != null) {
                    _cartState.value = ApiResponse.Success(order)
                    _snackbarMessage.emit("Cart updated successfully!")
                } else {
                    _cartState.value = ApiResponse.Failure(Exception("Failed to get draft order from response."))
                    _snackbarMessage.emit("Error updating cart: Response body is null.")
                }
            } else {
                val error = result.exceptionOrNull()
                _cartState.value = ApiResponse.Failure(error ?: Exception("Update failed."))
                _snackbarMessage.emit("Error updating cart: ${error?.message ?: "Unknown error"}")
            }
        }
    }


    suspend fun deleteCart() {
        val draftOrderId = currentDraftOrderId
        if (draftOrderId == null || draftOrderId == 0L) {
            _snackbarMessage.emit("No active cart to delete.")
            return
        }

        viewModelScope.launch {
            _cartState.value = ApiResponse.Loading
            val result = cartRepository.deleteDraftOrder(draftOrderId)
            if (result.isSuccess) {
                currentDraftOrderId = null
                val emptyDraftOrder = DraftOrder(
                    Id = 0L,
                    note = "",
                    lineItems = mutableListOf(),
                    totalPrice = "0.0",
                    customer = Customer(id = 0L)
                )
                _cartState.value = ApiResponse.Success(emptyDraftOrder)
                _snackbarMessage.emit("Cart deleted successfully!")
            } else {
                val error = result.exceptionOrNull()
                _cartState.value = ApiResponse.Failure(error ?: Exception("Error deleting cart."))
                _snackbarMessage.emit("Error deleting cart: ${error?.message ?: "Unknown error"}")
            }
        }
    }
}