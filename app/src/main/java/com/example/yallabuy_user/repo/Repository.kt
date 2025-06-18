package com.example.yallabuy_user.repo


import WishListDraftOrderRequest
import android.util.Log
import com.example.yallabuy_user.data.models.BrandResponse
import com.example.yallabuy_user.data.models.CategoryResponse
import com.example.yallabuy_user.data.models.CreateOrderRequest
import com.example.yallabuy_user.data.models.Coupon.DiscountCodeCoupon
import com.example.yallabuy_user.data.models.OrderDetailsResponse
import com.example.yallabuy_user.data.models.OrdersResponse
import com.example.yallabuy_user.data.models.Coupon.PriceRule
import com.example.yallabuy_user.data.models.ProductResponse
import com.example.yallabuy_user.data.models.cart.CreateCustomerCart
import com.example.yallabuy_user.data.models.cart.DraftOrderBody
import com.example.yallabuy_user.data.models.cart.DraftOrderResponse
import com.example.yallabuy_user.data.models.cart.ProductVariant
import com.example.yallabuy_user.data.models.cart.UpdateCustomerBody
import com.example.yallabuy_user.data.models.createUser.CreateUserOnShopifyResponse
import com.example.yallabuy_user.data.models.customer.CustomerDataResponse
import com.example.yallabuy_user.data.models.productInfo.ProductInfoResponse
import com.example.yallabuy_user.data.models.wishListDraftOrder.UpdateNoteInCustomer
import com.example.yallabuy_user.data.models.wishListDraftOrder.response.WishListDraftOrderResponse
import com.example.yallabuy_user.data.models.settings.AddressBody
import com.example.yallabuy_user.data.models.settings.AddressesResponse
import com.example.yallabuy_user.data.models.settings.NewAddressResponse
import com.example.yallabuy_user.data.remote.RemoteDataSourceInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import retrofit2.HttpException

class Repository(private val remoteDataSource: RemoteDataSourceInterface) : RepositoryInterface {
    override suspend fun getAllCategories(): Flow<CategoryResponse> {
        return remoteDataSource.getAllCategories()
    }

    override suspend fun getAllBrands(): Flow<BrandResponse> {
        return remoteDataSource.getAllBrands()
    }

    override suspend fun getCategoryProducts(categoryID: Long): Flow<ProductResponse> {
        return remoteDataSource.getCategoryProducts(categoryID)
    }

    override suspend fun getAllProducts(): Flow<ProductResponse> {
        return remoteDataSource.getAllProducts()
    }

    override suspend fun getProductById(productId: Long): Flow<ProductInfoResponse> {
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

    override suspend fun getPreviousOrders(userID: Long): Flow<OrdersResponse> {
        return remoteDataSource.getPreviousOrders(userID)
    }

    override suspend fun getOrderById(orderID: Long): Flow<OrderDetailsResponse> {
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
    ): Flow<CreateUserOnShopifyResponse> {
        return try {
            val response = remoteDataSource.createUserOnShopify(email, password, userName)
            response
        } catch (e: Exception) {
            flowOf()
        }
    }

    override suspend fun getUserDataByEmail(email: String): Flow<CustomerDataResponse> {
        return try {
            val customer = remoteDataSource.getUserDataByEmail(email)
            customer
        } catch (e: Exception) {
            Log.i("customer", "getUserDataByEmail in repo error is ${e.message} ")
            flowOf()
        }
    }

    override suspend fun getUserById(customerId: Long): Flow<CreateUserOnShopifyResponse> {
        return try {
            val customer = remoteDataSource.getCustomerById(customerId)
            customer
        } catch (e: Exception) {
            Log.i("customer", "getUserById in repo error is ${e.message} ")
            flowOf()
        }
    }

    //wishList
    override suspend fun creteWishListDraftOrder(wishListDraftOrderRequest: WishListDraftOrderRequest): Flow<WishListDraftOrderResponse> {
        return try {
            val wishListDraftOrderResponse =
                remoteDataSource.creteWishListDraftOrder(wishListDraftOrderRequest)
            wishListDraftOrderResponse
        } catch (e: Exception) {
            Log.i("wishList", "creteWishListDraftOrder in repo error is ${e.message} ")
            flowOf()
        }
    }

    override suspend fun getWishListDraftById(wishListDraftOrderId: Long): Flow<WishListDraftOrderResponse> {
        return try {
            val wishLestDraftOrderResponse =
                remoteDataSource.getWishListDraftById(wishListDraftOrderId)
            Log.i("wishList", "getWishListDraftById:  in repo success ")
            wishLestDraftOrderResponse
        } catch (e: Exception) {
            Log.i("wishList", "getWishListDraftById:  in repo error is ${e.message} ")
            flowOf()
        }
    }

    override suspend fun updateDraftOrder(
        draftOrderId: Long,
        wishListDraftOrderRequest: WishListDraftOrderRequest
    ): Flow<WishListDraftOrderResponse> {
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
        updateNoteInCustomer: UpdateNoteInCustomer
    ): Flow<CreateUserOnShopifyResponse> {
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
    override suspend fun getAddresses(customerId: Long): Flow<AddressesResponse> {
        return remoteDataSource.getAddresses(customerId)
    }

    override suspend fun createCustomerAddress(
        customerId: Long,
        newAddressBody: AddressBody
    ): Flow<NewAddressResponse> {
        return remoteDataSource.createCustomerAddress(customerId, newAddressBody)
    }

    override suspend fun updateCustomerAddress(
        customerId: Long,
        addressId: Long,
        updatedAddressBody: AddressBody
    ): Flow<NewAddressResponse> {
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
        return remoteDataSource.deleteDraftOrder(id)
    }

    override suspend fun getDraftOrderById(draftOrderId: Long): Flow<DraftOrderBody> {
        return remoteDataSource.draftOrderById(draftOrderId)
    }

    override suspend fun createOrder(order: CreateOrderRequest): Flow<OrderDetailsResponse> {
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

    override suspend fun getAllCouponsForRule(priceRuleId: Long): Flow<List<DiscountCodeCoupon>> {
        return remoteDataSource.getAllCouponsForRule(priceRuleId)
    }

    override suspend fun fetchPriceRules(): Flow<List<PriceRule>> {
        return  remoteDataSource.fetchPriceRules()
    }


}