package com.example.yallabuy_user.data.remote


import WishListDraftOrderRequest
import com.example.yallabuy_user.data.models.BrandResponse
import com.example.yallabuy_user.data.models.CategoryResponse
import com.example.yallabuy_user.data.models.OrderDetailsResponse
import com.example.yallabuy_user.data.models.OrdersResponse
import com.example.yallabuy_user.data.models.ProductResponse
import com.example.yallabuy_user.data.models.cart.DraftOrderBody
import com.example.yallabuy_user.data.models.cart.DraftOrderResponse
import com.example.yallabuy_user.data.models.createUser.CreateUserOnShopifyResponse
import com.example.yallabuy_user.data.models.customer.CustomerDataResponse
import com.example.yallabuy_user.data.models.productInfo.ProductInfoResponse
import com.example.yallabuy_user.data.models.wishListDraftOrder.UpdateNoteInCustomer
import com.example.yallabuy_user.data.models.wishListDraftOrder.response.WishListDraftOrderResponse
import com.example.yallabuy_user.data.models.settings.AddressBody
import com.example.yallabuy_user.data.models.settings.AddressesResponse
import com.example.yallabuy_user.data.models.settings.NewAddressResponse
import kotlinx.coroutines.flow.Flow

interface RemoteDataSourceInterface {
    suspend fun getAllCategories(): Flow<CategoryResponse>
    suspend fun getAllBrands(): Flow<BrandResponse>
    suspend fun getCategoryProducts(categoryID :Long): Flow<ProductResponse>
    suspend fun getAllProducts(): Flow<ProductResponse>
    suspend fun getProductInfoById(productId : Long) : Flow<ProductInfoResponse>
    suspend fun getPreviousOrders(userID : Long) : Flow<OrdersResponse>
    suspend fun getOrderById(orderID : Long) : Flow<OrderDetailsResponse>
    suspend fun createUserAccount(email: String, password: String) : Flow<String>
    suspend fun loginUser(email : String , password : String) : Flow<String>
    suspend fun createUserOnShopify(email: String, password: String, userName: String) : Flow<CreateUserOnShopifyResponse>
    suspend fun getUserDataByEmail(email : String) : Flow<CustomerDataResponse>
    suspend fun getCustomerById(customerId : Long) : Flow<CreateUserOnShopifyResponse>
    suspend fun creteWishListDraftOrder(wishListDraftOrderRequest: WishListDraftOrderRequest) : Flow<WishListDraftOrderResponse>
    suspend fun updateNoteInCustomer(customerId : Long,updateNoteInCustomer: UpdateNoteInCustomer) : Flow<CreateUserOnShopifyResponse>
    suspend fun getWishListDraftById(wishListDraftOrderId : Long) : Flow<WishListDraftOrderResponse>
    suspend fun updateDraftOrder(draftOrderId : Long , wishListDraftOrderRequest: WishListDraftOrderRequest): Flow<WishListDraftOrderResponse>
    suspend fun getCustomerAddressById(customerId: Long, addressId: Long): Flow<NewAddressResponse>
    suspend fun getAddresses(customerId: Long): Flow<AddressesResponse>
    suspend fun createCustomerAddress(customerId: Long, newAddressBody: AddressBody): Flow<NewAddressResponse>
    suspend fun updateCustomerAddress(customerId: Long, addressId: Long, updatedAddressBody: AddressBody): Flow<NewAddressResponse>
    suspend fun deleteCustomerAddress(customerId: Long, addressId: Long)

    suspend fun createDraftOrder(draftOrderBody: DraftOrderBody): Flow<DraftOrderBody>
    suspend fun getDraftOrder(): Flow<DraftOrderResponse>
    suspend fun updateDraftOrder(id: Long, draftOrderBody: DraftOrderBody): Flow<DraftOrderBody>
    suspend fun deleteDraftOrder(id: Long): Flow<Unit>

    

}