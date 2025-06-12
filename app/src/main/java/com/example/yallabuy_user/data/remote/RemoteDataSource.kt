package com.example.yallabuy_user.data.remote

import WishListDraftOrderRequest
import android.util.Log
import com.example.yallabuy_user.data.models.BrandResponse
import com.example.yallabuy_user.data.models.CategoryResponse
import com.example.yallabuy_user.data.models.OrderDetailsResponse
import com.example.yallabuy_user.data.models.OrdersResponse
import com.example.yallabuy_user.data.models.ProductResponse
import com.example.yallabuy_user.data.models.cart.DraftOrderBody
import com.example.yallabuy_user.data.models.cart.DraftOrderResponse
import com.example.yallabuy_user.data.models.createUser.CreateUserOnShopifyResponse
import com.example.yallabuy_user.data.models.createUser.request.CreateUSerOnShopifyRequest
import com.example.yallabuy_user.data.models.createUser.request.CustomerRequest
import com.example.yallabuy_user.data.models.customer.CustomerDataResponse
import com.example.yallabuy_user.data.models.productInfo.ProductInfoResponse
import com.example.yallabuy_user.data.models.wishListDraftOrder.UpdateNoteInCustomer
import com.example.yallabuy_user.data.models.wishListDraftOrder.response.WishListDraftOrderResponse
import com.example.yallabuy_user.data.models.settings.AddressBody
import com.example.yallabuy_user.data.models.settings.AddressesResponse
import com.example.yallabuy_user.data.models.settings.NewAddressResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import retrofit2.HttpException


class RemoteDataSource(
    private val service: ApiService,
    private val fireBaseService: FireBaseService
) : RemoteDataSourceInterface {


    override suspend fun getAllCategories(): Flow<CategoryResponse> {
        val categories = service.getAllCategories()
        return flowOf(categories)
    }

    override suspend fun getAllBrands(): Flow<BrandResponse> {
        val brands = service.getAllBrands()
        return flowOf(brands)
    }

    override suspend fun getCategoryProducts(categoryID: Long): Flow<ProductResponse> {
        val products = service.getCategoryProducts(categoryID)
        return flowOf(products)
    }

    override suspend fun getAllProducts(): Flow<ProductResponse> {
        val products = service.getAllProducts()
        return flowOf(products)
    }

    override suspend fun getProductInfoById(productId: Long): Flow<ProductInfoResponse> {
        return try {
            val infoResponse = service.getProductById(productId)
            flowOf(infoResponse)
        } catch (e: HttpException) {
            Log.i("error", "getProductInfoById in remote http error ${e.message} ")
            flowOf()
        } catch (e: NullPointerException) {
            Log.i("error", "getProductInfoById in remote null point  error ${e.message} ")
            flowOf()
        }
    }


    override suspend fun getPreviousOrders(userID: Long): Flow<OrdersResponse> {
        val orders = service.getPreviousOrders(userID)
        return flowOf(orders)
    }

    override suspend fun getOrderById(orderID: Long): Flow<OrderDetailsResponse> {
        val order = service.getOrderById(orderID)
        return flowOf(order)
    }

    override suspend fun createUserAccount(email: String, password: String): Flow<String> {
        return try {
            val createAccountResponse = fireBaseService.createUserAccount(email, password)
            Log.i("createUser", "createUserAccount in remote data source success   ")
            createAccountResponse
        } catch (e: Exception) {
            Log.i("createUser", "createUserAccount in remote data source error ${e.message}  ")
            flowOf("error ${e.message} ")
        }
    }

    override suspend fun loginUser(email: String, password: String): Flow<String> {
        return try {
            val loginResponse = fireBaseService.loginUser(email, password)
            Log.i("login", "loginUser in remote $loginResponse ")
            flowOf(loginResponse)
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
            val customer = CustomerRequest(userName, email, password, password)
            val request = CreateUSerOnShopifyRequest(customer)
            val response = service.createUserOnShopify(request)
            flowOf(response)
        } catch (e: HttpException) {
            if (e.code() == 422) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.d("ShopifyError", "Error details: $errorBody")
            }
            flowOf()
        }
    }

    override suspend fun getUserDataByEmail(email: String): Flow<CustomerDataResponse> {
        return try {
            val customer = service.getUserDataByEmail(email)
            flowOf(customer)
        } catch (e: Exception) {
            Log.i("customer", "getUserDataByEmail in remote error is ${e.message} ")
            flowOf()
        }
    }


    override suspend fun getCustomerById(customerId: Long): Flow<CreateUserOnShopifyResponse> {
        return try {
            val customer = service.getCustomerById(customerId)
            flowOf(customer)
        }catch (e : Exception){
            Log.i("customer", "getCustomerById in remote error is ${e.message} ")
            flowOf()
        }
    }
//    override suspend fun getCustomerAddressById(
//        customerId: Long,
//        addressId: Long
//    ): Flow<NewAddressResponse> = flow {
//        val response = service.getCustomerAddressById(customerId, addressId)
//        emit(response)
//    }

    override suspend fun getAddresses(
        customerId: Long
    ): Flow<AddressesResponse> = flow {
        val response = service.getAddresses(customerId)
        emit(response)
    }

    override suspend fun createCustomerAddress(
        customerId: Long,
        newAddressBody: AddressBody
    ): Flow<NewAddressResponse> = flow {
        val response = service.createCustomerAddress(customerId, newAddressBody)
        emit(response)
    }

    override suspend fun updateCustomerAddress(
        customerId: Long,
        addressId: Long,
        updatedAddressBody: AddressBody
    ): Flow<NewAddressResponse> = flow {
        val response = service.updateCustomerAddress(customerId, addressId, updatedAddressBody)
        emit(response)
    }

    override suspend fun deleteCustomerAddress(
        customerId: Long,
        addressId: Long
    ) {
        service.deleteCustomerAddress(customerId, addressId)
    }


    //cart
    override suspend fun createDraftOrder(draftOrderBody: DraftOrderBody): Flow<DraftOrderBody> {
        val response = service.createDraftOrder(draftOrderBody)
        return flowOf(response)

    }


    override suspend fun getDraftOrder(): Flow<DraftOrderResponse> {
        val response = service.getDraftOrders()
        return flowOf(response)
    }


    override suspend fun updateDraftOrder(
        id: Long,
        draftOrderBody: DraftOrderBody
    ): Flow<DraftOrderBody> {
        val response = service.updateDraftOrder(draftOrderBody, id)
        return flowOf(response)

    }


    override suspend fun deleteDraftOrder(id: Long): Flow<Unit> {
         service.deleteDraftOrder(id)
        return flowOf(Unit)
    }



    override suspend fun creteWishListDraftOrder(wishListDraftOrderRequest: WishListDraftOrderRequest): Flow<WishListDraftOrderResponse> {
        return try {
            val wishListDraftOrderResponse = service.createWishListDraftOrder(wishListDraftOrderRequest)
            flowOf(wishListDraftOrderResponse)
        }catch (e : Exception){
            Log.i("wishList", "creteWishListDraftOrder in remote error is ${e.message} ")
            flowOf()
        }
    }

    override suspend fun updateDraftOrder(draftOrderId: Long ,wishListDraftOrderRequest: WishListDraftOrderRequest): Flow<WishListDraftOrderResponse> {
       return try {
           val wishLestDraftOrderResponse = service.updateDraftOrder(draftOrderId , wishListDraftOrderRequest)
           flowOf(wishLestDraftOrderResponse)
       }catch (e : Exception){
           Log.i("wishList", "updateDraftOrder in remote error is ${e.message} ")
           flowOf()
       }
    }
    override suspend fun getWishListDraftById(wishListDraftOrderId: Long): Flow<WishListDraftOrderResponse> {
        return try {
            val wishLestDraftOrderResponse = service.getWishListDraftById(wishListDraftOrderId)
            flowOf(wishLestDraftOrderResponse)
        }catch (e : Exception){
            Log.i("wishList", "getWishListDraftById:  in remote error is ${e.message} ")
            flowOf()
        }
    }
    override suspend fun updateNoteInCustomer(customerId : Long,updateNoteInCustomer: UpdateNoteInCustomer): Flow<CreateUserOnShopifyResponse> {
        return try {
            val updatedCustomerResponse = service.updateNoteInCustomer(customerId , updateNoteInCustomer)
            flowOf(updatedCustomerResponse)
        }catch (e : Exception){
            Log.i("wishList", "updateNoteInCustomer in remote error is ${e.message} ")
            flowOf()
        }
    }

}
