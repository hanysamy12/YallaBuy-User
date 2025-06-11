package com.example.yallabuy_user.productInfo

import DraftCustomer
import DraftOrder
import DraftOrderLineItem
import LineItemProperty
import WishListDraftOrderRequest
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yallabuy_user.data.models.productInfo.ProductInfoResponse
import com.example.yallabuy_user.data.models.wishListDraftOrder.CustomerNoteUpdate
import com.example.yallabuy_user.data.models.wishListDraftOrder.UpdateNoteInCustomer
import com.example.yallabuy_user.repo.RepositoryInterface
import com.example.yallabuy_user.utilities.ApiResponse
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ProductInfoViewModel(
    private val repo: RepositoryInterface
) : ViewModel() {

    private val _productInfo: MutableStateFlow<ApiResponse<ProductInfoResponse>> =
        MutableStateFlow(ApiResponse.Loading)

    val productInfo = _productInfo.asStateFlow()

    private val _productIsAlreadySaved = MutableSharedFlow<Boolean>()
    val productIsAlreadySaved = _productIsAlreadySaved

    private val _isFirstProductInWishList = MutableStateFlow(false)
    val isFirstProductInWishList = _isFirstProductInWishList.asStateFlow()

    private var wishListDraftOrderIdGlobal: Long = 0L
    private var productIdGlobal: Long = 0L
    fun getProductInfoById(productId: Long) {
        viewModelScope.launch {
            try {
                val response = repo.getProductById(productId)
                response.collect { productInfo ->
                    _productInfo.emit(ApiResponse.Success(productInfo))
                    productIdGlobal = productInfo.product.id
                }
            } catch (e: HttpException) {
                Log.i("error", "getProductInfoById in view model http error ${e.message} ")
            } catch (e: NullPointerException) {
                Log.i("error", "getProductInfoById in view model null point  error ${e.message} ")
            }
        }
    }

    fun getCustomerById(customerId: Long, data: ProductInfoResponse) {
        viewModelScope.launch {
            try {
                val customerResponse = repo.getUserById(customerId)
                customerResponse.collect { customer ->
                    val note = customer.customer.note
                    val noteString = note as? String
                    if (noteString.isNullOrBlank()) {
                        Log.i("customer", "Note is empty or null.")
                        createWishListDraftOrder(data, customerId)
                    } else {
                        Log.i("customer", "Note is not empty: $noteString")
                        addProductToWishList(noteString, data, customerId)
                    }
                }
            } catch (e: Exception) {
                Log.i("customer", "getCustomerById in view model error is ${e.message} ")
            }
        }
    }

    private fun addProductToWishList(
        noteString: String,
        data: ProductInfoResponse,
        customerId: Long,
    ) {
        viewModelScope.launch {

            try {
                val wishListDraftOrderId = noteString.toLong()
                repo.getWishListDraftById(wishListDraftOrderId)
                    .collect { response ->
                        val lineItems = response.draft_order.line_items

                        val alreadyExists = isAlreadySaved(lineItems, data.product.title)
                        if (alreadyExists) {
                            _productIsAlreadySaved.emit(true)
                        } else {
                            val updatedLineItems = lineItems.toMutableList().apply {
                                add(
                                    DraftOrderLineItem(
                                        title = data.product.title,
                                        price = data.product.variants[0].price.takeIf { it.isNotEmpty() }
                                            ?: "0.00",
                                        quantity = 1,
                                        properties = listOf(
                                            LineItemProperty(
                                                name = "image",
                                                value = data.product.image.src
                                            ),
                                            LineItemProperty(
                                                name = "productId",
                                                value = productIdGlobal.toString()
                                            )
                                        )
                                    )
                                )
                            }

                            val draftOrder = DraftOrder(
                                line_items = updatedLineItems,
                                customer = DraftCustomer(customerId)
                            )

                            repo.updateDraftOrder(
                                wishListDraftOrderId,
                                WishListDraftOrderRequest(draftOrder)
                            )
                        }
                    }
            } catch (e: Exception) {
                Log.i("wishList", "addProductToWishList error in viewmodel ${e.message} ")
            }
        }
    }

    private fun createWishListDraftOrder(data: ProductInfoResponse, customerId: Long) {
        viewModelScope.launch {
            try {
                val draftOrder = DraftOrder(
                    line_items = listOf(
                        DraftOrderLineItem(
                            title = data.product.title,
                            price = data.product.variants[0].price.takeIf { it.isNotEmpty() }
                                ?: "0.00",
                            quantity = 1,
                            properties = listOf(
                                LineItemProperty(
                                    name = "image",
                                    value = data.product.image.src
                                )
                            )
                        )
                    ),
                    customer = DraftCustomer(customerId),
                )
                val draftOrderRequest = WishListDraftOrderRequest(draftOrder)
                val wishListDraftOrderResponse = repo.creteWishListDraftOrder(draftOrderRequest)
                wishListDraftOrderResponse.collect { wishListResponse ->
                    val wishListDraftOrderId = wishListResponse.draft_order.id
                    updateNoteInCustomer(wishListDraftOrderId, customerId)
                    _isFirstProductInWishList.emit(true)
                    wishListDraftOrderIdGlobal = wishListDraftOrderId
                    getWishListDraftOrderId()
                }
            } catch (e: Exception) {
                Log.i("wishList", "createWishListDraftOrder in view model error is ${e.message} ")
            }
        }
    }

    private fun updateNoteInCustomer(wishListDraftOrderId: Long, customerId: Long) {
        viewModelScope.launch {
            try {
                val updateNoteInCustomer = UpdateNoteInCustomer(
                    CustomerNoteUpdate(
                        wishListDraftOrderId,
                        wishListDraftOrderId.toString()
                    )
                )
                repo.updateNoteInCustomer(customerId, updateNoteInCustomer)
                    .collect { updatedCustomer ->
                        Log.i(
                            "wishList",
                            "updateNoteInCustomer success with new note = ${updatedCustomer.customer.note} "
                        )
                    }
            } catch (e: Exception) {
                Log.i("wishList", "updateNoteInCustomer in viewmodel error is ${e.message} ")
            }
        }
    }

    private fun isAlreadySaved(lineItems: List<DraftOrderLineItem>, title: String): Boolean {
        for (product in lineItems) {
            if (product.title == title) {
                return true
            }
        }
        return false
    }

    fun getWishListDraftOrderId(): Long {
        return wishListDraftOrderIdGlobal
    }
}