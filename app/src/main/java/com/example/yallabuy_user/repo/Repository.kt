package com.example.yallabuy_user.repo


import WishListDraftOrderRequest
import android.util.Log
import com.example.yallabuy_user.data.models.BrandResponse
import com.example.yallabuy_user.data.models.CategoryResponse
import com.example.yallabuy_user.data.models.OrderDetailsResponse
import com.example.yallabuy_user.data.models.OrdersResponse
import com.example.yallabuy_user.data.models.ProductResponse
import com.example.yallabuy_user.data.models.cart.DraftOrderBody
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
        return  remoteDataSource.getAllCategories()
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
        }catch ( e : HttpException){
            Log.i("error", "getProductInfoById in remote http error ${e.message} ")
            flowOf()
        } catch (e : NullPointerException){
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
            val createUserResponse = remoteDataSource.createUserAccount(email , password)
            Log.i("createUser", "createUserAccount in repo success ")
            createUserResponse
        }catch (e : Exception){
            Log.i("createUser", "createUserAccount in repo error ${e.message}  ")
           flowOf( "error ${e.message}")
        }
    }

    override suspend fun loginUser(email: String, password: String): Flow<String> {
        return try {
            val loginResponse = remoteDataSource.loginUser(email , password)
            loginResponse
        }catch (e : Exception){
            flowOf("error ${e.message}")
        }
    }

    override suspend fun createUserOnShopify(
        email: String,
        password: String,
        userName: String
    ): Flow<CreateUserOnShopifyResponse> {
        return try {
            val response = remoteDataSource.createUserOnShopify(email , password , userName)
            response
        }catch (e : Exception){
            flowOf()
        }
    }

    override suspend fun getUserDataByEmail(email: String): Flow<CustomerDataResponse> {
        return try {
            val customer = remoteDataSource.getUserDataByEmail(email)
            customer
        }catch (e : Exception){
            Log.i("customer", "getUserDataByEmail in repo error is ${e.message} ")
            flowOf()
        }
    }


    override suspend fun getUserById(customerId: Long): Flow<CreateUserOnShopifyResponse> {
        return try {
            val customer = remoteDataSource.getCustomerById(customerId)
            customer
        }catch (e : Exception){
            Log.i("customer", "getUserById in repo error is ${e.message} ")
        }
    }
    override suspend fun getCustomerAddressById(
        customerId: Long,
        addressId: Long
    ): Flow<NewAddressResponse> {
        return try {
            remoteDataSource.getCustomerAddressById(customerId, addressId)
        } catch (e: Exception) {
            Log.e("Repository", "getCustomerAddressById error: ${e.message}", e)
            flowOf()
        }
    }

    override suspend fun creteWishListDraftOrder(wishListDraftOrderRequest: WishListDraftOrderRequest): Flow<WishListDraftOrderResponse> {
        return try {
            val wishListDraftOrderResponse = remoteDataSource.creteWishListDraftOrder(wishListDraftOrderRequest)
            wishListDraftOrderResponse
        }catch (e : Exception){
            Log.i("wishList", "creteWishListDraftOrder in repo error is ${e.message} ")
        }
    }
    override suspend fun getAddresses(customerId: Long): Flow<AddressesResponse> {
        return try {
            remoteDataSource.getAddresses(customerId)
        } catch (e: Exception) {
            Log.e("Repository", "getAddresses error: ${e.message}", e)
            flowOf()
        }
    }

    override suspend fun createCustomerAddress(
        customerId: Long,
        newAddressBody: AddressBody
    ): Flow<NewAddressResponse> {
        return try {
            remoteDataSource.createCustomerAddress(customerId, newAddressBody)
        } catch (e: Exception) {
            Log.e("Repository", "createCustomerAddress error: ${e.message}", e)
            flowOf()
        }
    }

    override suspend fun updateNoteInCustomer(
        customerId: Long,
        updateNoteInCustomer: UpdateNoteInCustomer
    ): Flow<CreateUserOnShopifyResponse> {
        return try {
            val updatedCustomerResponse = remoteDataSource.updateNoteInCustomer(customerId , updateNoteInCustomer)
            updatedCustomerResponse
        }catch (e : Exception){
            Log.i("wishList", "updateNoteInCustomer in remote error is ${e.message} ")
        }
    }
    override suspend fun updateCustomerAddress(
        customerId: Long,
        addressId: Long,
        updatedAddressBody: AddressBody
    ): Flow<NewAddressResponse> {
        return try {
            remoteDataSource.updateCustomerAddress(customerId, addressId, updatedAddressBody)
        } catch (e: Exception) {
            Log.e("Repository", "updateCustomerAddress error: ${e.message}", e)
            flowOf()
        }
    }

    override suspend fun deleteCustomerAddress(customerId: Long, addressId: Long) {
        try {
            remoteDataSource.deleteCustomerAddress(customerId, addressId)
        } catch (e: Exception) {
            Log.e("Repository", "deleteCustomerAddress error: ${e.message}", e)
        }
    }


    //cart
    override suspend fun createDraftOrder(draftOrderBody: DraftOrderBody): Flow<DraftOrderBody> {
        return try {
            remoteDataSource.createDraftOrder(draftOrderBody)
        } catch (e: HttpException) {
            Log.i("CartRepo", "createDraftOrder http error: ${e.message}")

            flowOf()
        } catch (e: NullPointerException) {
            Log.i("CartRepo", "createDraftOrder null error: ${e.message}")
            flowOf()
        }
    }

    override suspend fun getWishListDraftById(wishListDraftOrderId: Long): Flow<WishListDraftOrderResponse> {
        return try {
            val wishLestDraftOrderResponse = remoteDataSource.getWishListDraftById(wishListDraftOrderId)
            Log.i("wishList", "getWishListDraftById:  in repo success ")
            wishLestDraftOrderResponse
        }catch (e : Exception){
            Log.i("wishList", "getWishListDraftById:  in repo error is ${e.message} ")
        }
    }
    override suspend fun getDraftOrder(id: Long): Flow<DraftOrderBody> {
        return try {
            remoteDataSource.getDraftOrder(id)
        } catch (e: HttpException) {
            Log.i("CartRepo", "getDraftOrder http error: ${e.message}")
            flowOf()
        } catch (e: NullPointerException) {
            Log.i("CartRepo", "getDraftOrder null error: ${e.message}")
            flowOf()
        }
    }

    override suspend fun updateDraftOrder(draftOrderId: Long , wishListDraftOrderRequest: WishListDraftOrderRequest): Flow<WishListDraftOrderResponse> {
        return try {
            val wishLestDraftOrderResponse = remoteDataSource.updateDraftOrder(draftOrderId , wishListDraftOrderRequest)
            Log.i("wishList", "updateDraftOrder in repo success ")
            wishLestDraftOrderResponse
        }catch (e : Exception){
            Log.i("wishList", "updateDraftOrder in repo error is ${e.message} ")
        }
    }
    override suspend fun updateDraftOrder(id: Long, draftOrderBody: DraftOrderBody): Flow<DraftOrderBody> {
        return try {
            remoteDataSource.updateDraftOrder(id, draftOrderBody)
        } catch (e: HttpException) {
            Log.i("CartRepo", "updateDraftOrder http error: ${e.message}")
            flowOf()
        } catch (e: NullPointerException) {
            Log.i("CartRepo", "updateDraftOrder null error: ${e.message}")
            flowOf()
        }
    }

    override suspend fun deleteDraftOrder(id: Long): Flow<Unit> {
        return try {
            remoteDataSource.deleteDraftOrder(id)
        } catch (e: HttpException) {
            Log.i("CartRepo", "deleteDraftOrder http error: ${e.message}")
            flowOf()
        } catch (e: NullPointerException) {
            Log.i("CartRepo", "deleteDraftOrder null error: ${e.message}")
            flowOf()
        }
    }
}