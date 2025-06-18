package com.example.yallabuy_user.productInfo

import DraftCustomer
import DraftOrder
import DraftOrderLineItem
import LineItemProperty
import WishListDraftOrderRequest
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yallabuy_user.data.models.cart.Customer
import com.example.yallabuy_user.data.models.cart.CustomerTagUpdate
import com.example.yallabuy_user.data.models.cart.DraftOrderBody
import com.example.yallabuy_user.data.models.cart.DraftOrderCart
import com.example.yallabuy_user.data.models.cart.LineItem
import com.example.yallabuy_user.data.models.cart.Property
import com.example.yallabuy_user.data.models.cart.UpdateCustomerBody
import com.example.yallabuy_user.data.models.productInfo.ProductInfoResponse
import com.example.yallabuy_user.data.models.wishListDraftOrder.CustomerNoteUpdate
import com.example.yallabuy_user.data.models.wishListDraftOrder.UpdateNoteInCustomer
import com.example.yallabuy_user.repo.RepositoryInterface
import com.example.yallabuy_user.utilities.ApiResponse
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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

  /*  fun getCustomerById(customerId: Long, data: ProductInfoResponse) {
        viewModelScope.launch {
            try {
                val customerResponse = repo.getUserById(customerId)
                customerResponse.collect { customer ->
                    val note = customer.customer.note
                    val noteString = note as? String
                    val tags = customer.customer.tags
                    if (noteString.isNullOrBlank()) {
                        Log.i("customer", "Note is empty or null.")
                        createWishListDraftOrder(data, customerId)
                    } else {
                        Log.i("customer", "Note is not empty: $note")
                        addProductToWishList(noteString, data, customerId)
                    }
                    if (tags.isBlank()) {
                        Log.i("customer", "tag is empty or null.")
                        createDraftOrderCart(data, customerId)
                    } else {
                        Log.i("customer", "tag is not empty: $tags")
                        val draftOrderId = tags.toLongOrNull()
                        if (draftOrderId != null) {
                            addProductToCart(draftOrderId, data, customerId)
                        } else {
                            Log.e("cart", "Invalid draft order ID in tag: $tags")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.i("customer", "getCustomerById in view model error is ${e.message} ")
            }
        }
    }

*/

    fun getCustomerById(customerId: Long, data: ProductInfoResponse, isWishlist: Boolean) {
        viewModelScope.launch {
            try {
                val customerResponse = repo.getUserById(customerId)
                customerResponse.collect { customer ->
                    val note = customer.customer.note ?: ""
                    val noteString = note as? String
                    val tags = customer.customer.tags ?: ""

                    if (isWishlist) {
                        if (noteString.isNullOrBlank()) {
                            createWishListDraftOrder(data, customerId)
                        } else {
                            addProductToWishList(noteString, data, customerId)
                        }
                    } else {
                        if (tags.isBlank()) {
                            createDraftOrderCart(data, customerId)
                        } else {
                            val draftOrderId = tags.toLongOrNull()
                            if (draftOrderId != null) {
                                addProductToCart(draftOrderId, data, customerId)
                            } else {
                                Log.e("cart", "Invalid draft order ID in tag: $tags")
                            }
                        }
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


    private fun createDraftOrderCart(data: ProductInfoResponse, customerId: Long) {
        viewModelScope.launch {
            try {
                val draftOrderCart = DraftOrderCart(
                    lineItems = mutableListOf(
                        LineItem(
                            title = data.product.title,
                            price = data.product.variants[0].price.takeIf { it.isNotEmpty() } ?: "0.00",
                            quantity = 1,
                            variantID = data.product.variants[0].id,
                            productID = data.product.id,
                            properties = listOf(
                                Property(
                                    name = "image",
                                    value = data.product.image.src
                                )
                            )
                        )
                    ),
                    customer = Customer(customerId)
                )

                val draftOrderBody = DraftOrderBody(draftOrderCart)

                val draftOrderResponse = repo.createDraftOrderCart(draftOrderBody)
                draftOrderResponse.collect { response ->
                    val draftOrderId = response.draftOrderCart.id

                    val updateTagsBody = UpdateCustomerBody(
                        customer = CustomerTagUpdate(
                            id = customerId,
                            tags = draftOrderId.toString()
                        )
                    )
                    repo.updateCustomerTags(customerId, updateTagsBody).collect {
                        Log.i("cart", "Successfully created cart and updated customer tags with $draftOrderId")
                    }
                }

            } catch (e: Exception) {
                Log.e("cart", "createDraftOrder error: ${e.message}")
            }
        }
    }

    private fun addProductToCart(
        draftOrderId: Long,
        data: ProductInfoResponse,
        customerId: Long
    ) {
        viewModelScope.launch {
            try {
                repo.getDraftOrderCart(draftOrderId).collect { draftOrderBody ->
                    val existingCart = draftOrderBody.draftOrderCart
                    val lineItems = existingCart.lineItems

                    val alreadyExists = isAlreadySavedInCart(lineItems, data.product.title)
                    if (alreadyExists) {
                        _productIsAlreadySaved.emit(true)
                    } else {
                        // Add new product to line items
                        val updatedLineItems = lineItems.toMutableList().apply {
                            add(
                                LineItem(
                                    title = data.product.title,
                                    price = data.product.variants[0].price.takeIf { it.isNotEmpty() } ?: "0.00",
                                    quantity = 1,
                                    variantID = data.product.variants[0].id,
                                    productID = data.product.id,
                                    properties = listOf(
                                        Property(
                                            name = "image",
                                            value = data.product.image.src
                                        )
                                    )
                                )
                            )
                        }

                        // Create updated draft order cart
                        val updatedDraftOrder = DraftOrderCart(
                            id = draftOrderId,
                            lineItems = updatedLineItems,
                            customer = Customer(customerId)
                        )

                        val updateRequest = DraftOrderBody(draftOrderCart = updatedDraftOrder)

                        // Call repository to update the draft order cart
                        repo.updateDraftOrder(draftOrderId, updateRequest).collect {
                            Log.i("cart", "Cart updated successfully with new product")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("cart", "Error in addProductToCart: ${e.message}")
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
    private fun isAlreadySavedInCart(lineItems: List<LineItem>, title: String): Boolean {
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

    fun updateCustomerTags(customerId: Long, newTag: String) {
        viewModelScope.launch {
            try {
                repo.getUserById(customerId).collect { customerResponse ->
                    val currentTag = customerResponse.customer.tags.orEmpty()

                    if (currentTag != newTag) {
                        val updateRequest = UpdateCustomerBody(
                            customer = CustomerTagUpdate(
                                id = customerId,
                                tags = newTag
                            )
                        )

                        repo.updateCustomerTags(customerId, updateRequest).collect { updated ->
                            Log.i("customer", "Successfully updated tag to: ${updated.customer.tags}")
                        }
                    } else {
                        Log.i("customer", "Tag already up-to-date: $currentTag")
                    }
                }
            } catch (e: Exception) {
                Log.e("customer", "Error updating customer tag: ${e.message}")
            }
        }
    }

}