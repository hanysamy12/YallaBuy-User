package com.example.yallabuy_user.repo


import android.util.Log
import com.example.yallabuy_user.data.models.BrandResponse
import com.example.yallabuy_user.data.models.CategoryResponse
import com.example.yallabuy_user.data.models.OrderDetailsResponse
import com.example.yallabuy_user.data.models.OrdersResponse
import com.example.yallabuy_user.data.models.ProductResponse
import com.example.yallabuy_user.data.models.createUser.CreateUserOnShopifyResponse
import com.example.yallabuy_user.data.models.customer.CustomerDataResponse
import com.example.yallabuy_user.data.models.productInfo.ProductInfoResponse
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
}