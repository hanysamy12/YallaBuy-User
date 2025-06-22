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

    private val _isFirstProductInCart = MutableStateFlow(false)
    val isFirstProductInCart = _isFirstProductInCart.asStateFlow()

    private val _resetWishListSharedPreference = MutableStateFlow(false)
    val resetWishListSharedPreference = _resetWishListSharedPreference.asStateFlow()

    private var wishListDraftOrderIdGlobal: Long = 0L
    private var cartDraftOrderIdGlobal: Long = 0L
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

    fun getCustomerById(customerId: Long, data: ProductInfoResponse, isWishlist: Boolean) {
        viewModelScope.launch {
            try {
                val customerResponse = repo.getUserById(customerId)
                customerResponse.collect { customer ->
                    val note = customer.customer.note ?: ""
                    val noteString = note as? String
                    val tags = customer.customer.tags ?: ""

                    if (isWishlist) {
                        if (noteString.isNullOrBlank() || noteString == "") {
                            createWishListDraftOrder(data, customerId)
                        } else {
                            addProductToWishList(noteString, data, customerId)
                        }
                    } else {
                        if (tags.isBlank()) {
                            createDraftOrderCart(data, customerId)
                        } else {
                            _isFirstProductInCart.emit(false)
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
                                ),
                                LineItemProperty(
                                    name = "productId",
                                    value = productIdGlobal.toString()
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
                    updateNoteInCustomer(wishListDraftOrderId.toString(), customerId)
                    _isFirstProductInWishList.emit(true)
                    wishListDraftOrderIdGlobal = wishListDraftOrderId
                    getWishListDraftOrderId()
                }
            } catch (e: Exception) {
                Log.i("checkingWishList", "createWishListDraftOrder in view model error is ${e.message} ")
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
                            price = data.product.variants[0].price.takeIf { it.isNotEmpty() }
                                ?: "0.00",
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
                    _isFirstProductInCart.emit(true)
                    cartDraftOrderIdGlobal = draftOrderId ?: 0L
                    Log.i("newOrder", "createDraftOrderCart id = $cartDraftOrderIdGlobal")
                    repo.updateCustomerTags(customerId, updateTagsBody).collect {
                        Log.i(
                            "cart",
                            "Successfully created cart and updated customer tags with $draftOrderId"
                        )
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
//                        _productIsAlreadySaved.emit(true)
                    } else {
                        // Add new product to line items
                        val updatedLineItems = lineItems.toMutableList().apply {
                            add(
                                LineItem(
                                    title = data.product.title,
                                    price = data.product.variants[0].price.takeIf { it.isNotEmpty() }
                                        ?: "0.00",
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


    private fun updateNoteInCustomer(wishListDraftOrderId: String, customerId: Long) {
        Log.i("checkingWishList", "updateNoteInCustomer function called ")
        viewModelScope.launch {
            try {
                val updateNoteInCustomer = UpdateNoteInCustomer(
                    CustomerNoteUpdate(
                        customerId,
                        wishListDraftOrderId
                    )
                )
                repo.updateNoteInCustomer(customerId, updateNoteInCustomer)
                    .collect { updatedCustomer ->
                        Log.i(
                            "checkingWishList",
                            "updateNoteInCustomer success with new note = ${updatedCustomer.customer.note} "
                        )
                    }
            } catch (e: Exception) {
                Log.e("checkingWishList", "deleteProductFromWishList failed", e)
                Log.i("checkingWishList", "Exception type: ${e::class.java.simpleName}")
                Log.i("checkingWishList", "LocalizedMessage: ${e.localizedMessage}")
            }
        }
    }

     fun isAlreadySaved(wishListId : Long , productId : Long) {
         Log.i("checkingWishList", "isAlreadySaved wish list id = $wishListId ")
         viewModelScope.launch {
            if (wishListId != 0L) {
                try {
                    Log.i("checkingWishList", "isAlreadySaved product id = $productId ")
                    repo.getWishListDraftById(wishListId).collect{
                        Log.i("checkingWishList", "isAlreadySaved in collector ")
                        val response = it.draft_order.line_items
                         .filter {product ->
                             Log.i("checkingWishList", "isAlreadySaved in  response ")
                             product.properties?.get(1)?.value == productId.toString()
                        }
                        for(product in response){
                            Log.i("checkingWishList", "isAlreadySaved response have  ${response.get(0).properties?.get(1)?.value} ")
                        }
                        if(response.isNotEmpty()){
                             Log.i("checkingWishList", "isAlreadySaved found product ${response.get(0).properties?.get(1)?.value} ")
                             _productIsAlreadySaved.emit(true)
                         }
                    }
                } catch (e: Exception) {
                    Log.i("checkingWishList", "isAlreadySaved error in viewModel is ${e.message} ")
                }
            }else {
                Log.i("checkingWishList", "isAlreadySaved shared pref is null ")
                _productIsAlreadySaved.emit(false)
            }
        }
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

    fun deleteProductFromWishList(customerId : Long , productTitle : String , wishListId : Long){
        viewModelScope.launch {
            try {
                repo.getWishListDraftById(wishListId).collect{ response ->

                    if(response.draft_order.line_items.size == 1){
                        repo.deleteDraftOrderCart(wishListId)
                        updateNoteInCustomer(" ",customerId )
                        _resetWishListSharedPreference.emit(true)
                    }else {
                        val product = response.draft_order.line_items
                            .filter {
                                it.title.equals(productTitle)
                            }
                        val mutableDeletedList =  response.draft_order.line_items.toMutableList()
                        mutableDeletedList.remove(product[0])
                     val wishListDraftOrderRequest = WishListDraftOrderRequest(
                         DraftOrder(mutableDeletedList.toList() , DraftCustomer(customerId))
                     )
                     repo.updateDraftOrder(wishListId , wishListDraftOrderRequest )
                     _productIsAlreadySaved.emit(false)
                     _resetWishListSharedPreference.emit(false)
                    }
                }
            }catch (e : Exception){
                Log.i("checkingWishList", "deleteProductFromWishList error in viewModel ${e.localizedMessage} ")
            }

        }

    }

    fun getCartDraftOrderId(): Long {
        Log.i("newOrder", "getCartDraftOrderId in view model cart id $cartDraftOrderIdGlobal ")
        return cartDraftOrderIdGlobal
    }

}