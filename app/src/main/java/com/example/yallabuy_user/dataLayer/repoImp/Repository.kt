package com.example.yallabuy_user.dataLayer.repoImp


import WishListDraftOrderRequest
import android.util.Log
import com.example.yallabuy_user.dataLayer.data.models.CategoryResponse
import com.example.yallabuy_user.dataLayer.data.models.cart.CreateCustomerCart
import com.example.yallabuy_user.dataLayer.data.models.cart.DraftOrderBody
import com.example.yallabuy_user.dataLayer.data.models.cart.DraftOrderResponse
import com.example.yallabuy_user.dataLayer.data.models.cart.ProductVariant
import com.example.yallabuy_user.dataLayer.data.models.cart.UpdateCustomerBody
import com.example.yallabuy_user.domainLayer.repoInterface.RepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import retrofit2.HttpException

class Repository(private val remoteDataSource: com.example.yallabuy_user.dataLayer.data.remote.RemoteDataSourceInterface) :
    RepositoryInterface {
    override suspend fun getAllCategories(): Flow<CategoryResponse> {
        return remoteDataSource.getAllCategories()
    }

    override suspend fun getAllBrands(): Flow<com.example.yallabuy_user.dataLayer.data.models.BrandResponse> {
        return remoteDataSource.getAllBrands()
    }

    override suspend fun getCategoryProducts(categoryID: Long): Flow<com.example.yallabuy_user.dataLayer.data.models.ProductResponse> {
        return remoteDataSource.getCategoryProducts(categoryID)
    }

    override suspend fun getAllProducts(): Flow<com.example.yallabuy_user.dataLayer.data.models.ProductResponse> {
        return remoteDataSource.getAllProducts()
    }

    override suspend fun getProductById(productId: Long): Flow<com.example.yallabuy_user.dataLayer.data.models.productInfo.ProductInfoResponse> {
        return try {
            val infoResponse = remoteDataSource.getProductInfoById(productId)
            infoResponse
        } catch (e: HttpException) {
            Log.i("error", "getProductInfoById in remote http error ${e.message} ")
            flowOf()
        } catch (e: NullPointerException) {
            Log.i("error", "getProductInfoById in remote null point  error ${e.message} ")
            flowOf()
        }
    }

    override suspend fun getPreviousOrders(userID: Long): Flow<com.example.yallabuy_user.dataLayer.data.models.OrdersResponse> {
        return remoteDataSource.getPreviousOrders(userID)
    }

    override suspend fun getOrderById(orderID: Long): Flow<com.example.yallabuy_user.dataLayer.data.models.OrderDetailsResponse> {
        return remoteDataSource.getOrderById(orderID)
    }

    override suspend fun createUserAccount(email: String, password: String): Flow<String> {
        return try {
            val createUserResponse = remoteDataSource.createUserAccount(email, password)
            Log.i("createUser", "createUserAccount in repo success ")
            createUserResponse
        } catch (e: Exception) {
            Log.i("createUser", "createUserAccount in repo error ${e.message}  ")
            flowOf("error ${e.message}")
        }
    }

    override suspend fun loginUser(email: String, password: String): Flow<String> {
        return try {
            val loginResponse = remoteDataSource.loginUser(email, password)
            loginResponse
        } catch (e: Exception) {
            flowOf("error ${e.message}")
        }
    }

    override suspend fun createUserOnShopify(
        email: String,
        password: String,
        userName: String
    ): Flow<com.example.yallabuy_user.dataLayer.data.models.createUser.CreateUserOnShopifyResponse> {
        return try {
            val response = remoteDataSource.createUserOnShopify(email, password, userName)
            response
        } catch (e: Exception) {
            flowOf()
        }
    }

    override suspend fun getUserDataByEmail(email: String): Flow<com.example.yallabuy_user.dataLayer.data.models.customer.CustomerDataResponse> {
        return try {
            val customer = remoteDataSource.getUserDataByEmail(email)
            customer
        } catch (e: Exception) {
            Log.i("customer", "getUserDataByEmail in repo error is ${e.message} ")
            flowOf()
        }
    }

    override suspend fun getUserById(customerId: Long): Flow<com.example.yallabuy_user.dataLayer.data.models.createUser.CreateUserOnShopifyResponse> {
        return try {
            val customer = remoteDataSource.getCustomerById(customerId)
            customer
        } catch (e: Exception) {
            Log.i("customer", "getUserById in repo error is ${e.message} ")
            flowOf()
        }
    }

    //wishList
    override suspend fun creteWishListDraftOrder(wishListDraftOrderRequest: WishListDraftOrderRequest): Flow<com.example.yallabuy_user.dataLayer.data.models.wishListDraftOrder.response.WishListDraftOrderResponse> {
        return try {
            val wishListDraftOrderResponse =
                remoteDataSource.creteWishListDraftOrder(wishListDraftOrderRequest)
            wishListDraftOrderResponse
        } catch (e: Exception) {
            Log.i("wishList", "creteWishListDraftOrder in repo error is ${e.message} ")
            flowOf()
        }
    }

    override suspend fun getWishListDraftById(wishListDraftOrderId: Long): Flow<com.example.yallabuy_user.dataLayer.data.models.wishListDraftOrder.response.WishListDraftOrderResponse> {
        return try {
            val wishLestDraftOrderResponse =
                remoteDataSource.getWishListDraftById(wishListDraftOrderId)
            Log.i("checkingWishList", "getWishListDraftById:  in repo success ")
            wishLestDraftOrderResponse
        } catch (e: Exception) {
            Log.i("checkingWishList", "getWishListDraftById:  in repo error is ${e.message} ")
            flowOf()
        }
    }

    override suspend fun updateDraftOrder(
        draftOrderId: Long,
        wishListDraftOrderRequest: WishListDraftOrderRequest
    ): Flow<com.example.yallabuy_user.dataLayer.data.models.wishListDraftOrder.response.WishListDraftOrderResponse> {
        return try {
            val wishLestDraftOrderResponse =
                remoteDataSource.updateDraftOrder(draftOrderId, wishListDraftOrderRequest)
            Log.i("wishList", "updateDraftOrder in repo success ")
            wishLestDraftOrderResponse
        } catch (e: Exception) {
            Log.i("wishList", "updateDraftOrder in repo error is ${e.message} ")
            flowOf()
        }
    }

    override suspend fun updateNoteInCustomer(
        customerId: Long,
        updateNoteInCustomer: com.example.yallabuy_user.dataLayer.data.models.wishListDraftOrder.UpdateNoteInCustomer
    ): Flow<com.example.yallabuy_user.dataLayer.data.models.createUser.CreateUserOnShopifyResponse> {
        return try {
            val updatedCustomerResponse =
                remoteDataSource.updateNoteInCustomer(customerId, updateNoteInCustomer)
            updatedCustomerResponse
        } catch (e: Exception) {
            Log.i("wishList", "updateNoteInCustomer in remote error is ${e.message} ")
            flowOf()
        }
    }

    //address
    override suspend fun getAddresses(customerId: Long): Flow<com.example.yallabuy_user.dataLayer.data.models.settings.AddressesResponse> {
        return remoteDataSource.getAddresses(customerId)
    }

    override suspend fun createCustomerAddress(
        customerId: Long,
        newAddressBody: com.example.yallabuy_user.dataLayer.data.models.settings.AddressBody
    ): Flow<com.example.yallabuy_user.dataLayer.data.models.settings.NewAddressResponse> {
        return remoteDataSource.createCustomerAddress(customerId, newAddressBody)
    }

    override suspend fun updateCustomerAddress(
        customerId: Long,
        addressId: Long,
        updatedAddressBody: com.example.yallabuy_user.dataLayer.data.models.settings.AddressBody
    ): Flow<com.example.yallabuy_user.dataLayer.data.models.settings.NewAddressResponse> {
        return remoteDataSource.updateCustomerAddress(customerId, addressId, updatedAddressBody)
    }

    override suspend fun deleteCustomerAddress(customerId: Long, addressId: Long) {
        try {
            remoteDataSource.deleteCustomerAddress(customerId, addressId)
        } catch (e: Exception) {
            Log.e("Repository", "deleteCustomerAddress error: ${e.message}", e)
        }
    }

    //cart
    override suspend fun createDraftOrderCart(draftOrderBody: DraftOrderBody): Flow<DraftOrderBody> {
        return remoteDataSource.createDraftOrder(draftOrderBody)
    }

    override suspend fun getDraftOrderCart(): Flow<DraftOrderResponse> {
        return remoteDataSource.getDraftOrders()
    }

    override suspend fun getDraftOrderCart(draftOrderId: Long): Flow<DraftOrderBody> {
        return remoteDataSource.getDraftOrderCart(draftOrderId)
    }

    override suspend fun updateDraftOrder(
        id: Long,
        draftOrderBody: DraftOrderBody
    ): Flow<DraftOrderBody> {
        return remoteDataSource.updateDraftOrder(id, draftOrderBody)
    }

    override suspend fun deleteDraftOrderCart(id: Long): Flow<Unit> {
        Log.i("newOrder", "deleteDraftOrderCart in repo  ")
        return remoteDataSource.deleteDraftOrder(id)
    }

    override suspend fun getDraftOrderById(draftOrderId: Long): Flow<DraftOrderBody> {
        return remoteDataSource.draftOrderById(draftOrderId)
    }

    override suspend fun createOrder(order: com.example.yallabuy_user.dataLayer.data.models.CreateOrderRequest): Flow<com.example.yallabuy_user.dataLayer.data.models.OrderDetailsResponse> {
        return remoteDataSource.createOrder(order)
    }

    override suspend fun updateCustomerTags(
        customerId: Long,
        customerBody: UpdateCustomerBody
    ): Flow<CreateCustomerCart> {
        return remoteDataSource.updateCustomerTags(customerId, customerBody)
    }

    override suspend fun getProductVariantById(variantId: Long): Flow<ProductVariant> {
        return remoteDataSource.getProductVariantById(variantId)
    }

    override suspend fun getAllCouponsForRule(priceRuleId: Long): Flow<List<com.example.yallabuy_user.dataLayer.data.models.Coupon.DiscountCodeCoupon>> {
        return remoteDataSource.getAllCouponsForRule(priceRuleId)
    }

    override suspend fun fetchPriceRules(): Flow<List<com.example.yallabuy_user.dataLayer.data.models.Coupon.PriceRule>> {
        return  remoteDataSource.fetchPriceRules()
    }


}