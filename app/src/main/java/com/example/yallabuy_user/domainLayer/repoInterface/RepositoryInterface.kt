package com.example.yallabuy_user.domainLayer.repoInterface


import WishListDraftOrderRequest
import com.example.yallabuy_user.dataLayer.data.models.CategoryResponse
import com.example.yallabuy_user.dataLayer.data.models.cart.CreateCustomerCart
import com.example.yallabuy_user.dataLayer.data.models.cart.DraftOrderBody
import com.example.yallabuy_user.dataLayer.data.models.cart.DraftOrderResponse
import com.example.yallabuy_user.dataLayer.data.models.cart.ProductVariant
import com.example.yallabuy_user.dataLayer.data.models.cart.UpdateCustomerBody
import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {
    suspend fun getAllCategories(): Flow<CategoryResponse>
    suspend fun getAllBrands(): Flow<com.example.yallabuy_user.dataLayer.data.models.BrandResponse>
    suspend fun getCategoryProducts(categoryID: Long): Flow<com.example.yallabuy_user.dataLayer.data.models.ProductResponse>
    suspend fun getAllProducts(): Flow<com.example.yallabuy_user.dataLayer.data.models.ProductResponse>
    suspend fun getProductById(productId : Long) : Flow<com.example.yallabuy_user.dataLayer.data.models.productInfo.ProductInfoResponse>
    suspend fun getPreviousOrders(userID : Long) : Flow<com.example.yallabuy_user.dataLayer.data.models.OrdersResponse>
    suspend fun getOrderById(orderID : Long) : Flow<com.example.yallabuy_user.dataLayer.data.models.OrderDetailsResponse>
    suspend fun createUserAccount(email: String, password: String): Flow<String>
    suspend fun loginUser(email : String , password : String) : Flow<String>
    suspend fun createUserOnShopify(email: String, password: String, userName: String) : Flow<com.example.yallabuy_user.dataLayer.data.models.createUser.CreateUserOnShopifyResponse>
    suspend fun getUserDataByEmail(email : String) : Flow<com.example.yallabuy_user.dataLayer.data.models.customer.CustomerDataResponse>
    suspend fun getUserById(customerId : Long) : Flow<com.example.yallabuy_user.dataLayer.data.models.createUser.CreateUserOnShopifyResponse>
    suspend fun creteWishListDraftOrder(wishListDraftOrderRequest: WishListDraftOrderRequest) : Flow<com.example.yallabuy_user.dataLayer.data.models.wishListDraftOrder.response.WishListDraftOrderResponse>
    suspend fun updateNoteInCustomer(customerId : Long,updateNoteInCustomer: com.example.yallabuy_user.dataLayer.data.models.wishListDraftOrder.UpdateNoteInCustomer) : Flow<com.example.yallabuy_user.dataLayer.data.models.createUser.CreateUserOnShopifyResponse>
    suspend fun getWishListDraftById(wishListDraftOrderId : Long) : Flow<com.example.yallabuy_user.dataLayer.data.models.wishListDraftOrder.response.WishListDraftOrderResponse>
    suspend fun updateDraftOrder(draftOrderId : Long, wishListDraftOrderRequest: WishListDraftOrderRequest): Flow<com.example.yallabuy_user.dataLayer.data.models.wishListDraftOrder.response.WishListDraftOrderResponse>

    suspend fun getAddresses(customerId: Long): Flow<com.example.yallabuy_user.dataLayer.data.models.settings.AddressesResponse>
    suspend fun createCustomerAddress(customerId: Long, newAddressBody: com.example.yallabuy_user.dataLayer.data.models.settings.AddressBody): Flow<com.example.yallabuy_user.dataLayer.data.models.settings.NewAddressResponse>
    suspend fun updateCustomerAddress(customerId: Long, addressId: Long, updatedAddressBody: com.example.yallabuy_user.dataLayer.data.models.settings.AddressBody
    ): Flow<com.example.yallabuy_user.dataLayer.data.models.settings.NewAddressResponse>
    suspend fun deleteCustomerAddress(customerId: Long, addressId: Long)

    suspend fun createDraftOrderCart(draftOrderBody: DraftOrderBody): Flow<DraftOrderBody>
    suspend fun getDraftOrderCart(): Flow<DraftOrderResponse>
    suspend fun getDraftOrderCart(draftOrderId: Long): Flow<DraftOrderBody>
    suspend fun updateDraftOrder(id: Long, draftOrderBody: DraftOrderBody): Flow<DraftOrderBody>
    suspend fun deleteDraftOrderCart(id: Long): Flow<Unit>
    suspend fun getDraftOrderById(draftOrderId: Long): Flow<DraftOrderBody>
    suspend fun createOrder(order: com.example.yallabuy_user.dataLayer.data.models.CreateOrderRequest): Flow<com.example.yallabuy_user.dataLayer.data.models.OrderDetailsResponse>
    suspend fun updateCustomerTags(customerId: Long, customerBody: UpdateCustomerBody): Flow<CreateCustomerCart>
    suspend fun getProductVariantById(variantId: Long): Flow<ProductVariant>


    suspend fun getAllCouponsForRule(priceRuleId: Long): Flow<List<com.example.yallabuy_user.dataLayer.data.models.Coupon.DiscountCodeCoupon>>
    suspend fun fetchPriceRules(): Flow<List<com.example.yallabuy_user.dataLayer.data.models.Coupon.PriceRule>>
}