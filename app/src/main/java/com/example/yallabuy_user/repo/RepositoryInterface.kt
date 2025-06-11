package com.example.yallabuy_user.repo


import WishListDraftOrderRequest
import com.example.yallabuy_user.data.models.BrandResponse
import com.example.yallabuy_user.data.models.CategoryResponse
import com.example.yallabuy_user.data.models.OrderDetailsResponse
import com.example.yallabuy_user.data.models.OrdersResponse
import com.example.yallabuy_user.data.models.ProductResponse
import com.example.yallabuy_user.data.models.createUser.CreateUserOnShopifyResponse
import com.example.yallabuy_user.data.models.customer.CustomerDataResponse
import com.example.yallabuy_user.data.models.productInfo.ProductInfoResponse
import com.example.yallabuy_user.data.models.wishListDraftOrder.UpdateNoteInCustomer
import com.example.yallabuy_user.data.models.wishListDraftOrder.response.WishListDraftOrderResponse
import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {
    suspend fun getAllCategories(): Flow<CategoryResponse>
    suspend fun getAllBrands(): Flow<BrandResponse>
    suspend fun getCategoryProducts(categoryID: Long): Flow<ProductResponse>
    suspend fun getAllProducts(): Flow<ProductResponse>
    suspend fun getProductById(productId : Long) : Flow<ProductInfoResponse>
    suspend fun getPreviousOrders(userID : Long) : Flow<OrdersResponse>
    suspend fun getOrderById(orderID : Long) : Flow<OrderDetailsResponse>

    suspend fun createUserAccount(email: String, password: String): Flow<String>
    suspend fun loginUser(email : String , password : String) : Flow<String>
    suspend fun createUserOnShopify(email: String, password: String, userName: String) : Flow<CreateUserOnShopifyResponse>
    suspend fun getUserDataByEmail(email : String) : Flow<CustomerDataResponse>
    suspend fun getUserById(customerId : Long) : Flow<CreateUserOnShopifyResponse>
    suspend fun creteWishListDraftOrder(wishListDraftOrderRequest: WishListDraftOrderRequest) : Flow<WishListDraftOrderResponse>
    suspend fun updateNoteInCustomer(customerId : Long,updateNoteInCustomer: UpdateNoteInCustomer) : Flow<CreateUserOnShopifyResponse>
    suspend fun getWishListDraftById(wishListDraftOrderId : Long) : Flow<WishListDraftOrderResponse>
    suspend fun updateDraftOrder(draftOrderId : Long , wishListDraftOrderRequest: WishListDraftOrderRequest): Flow<WishListDraftOrderResponse>
}
