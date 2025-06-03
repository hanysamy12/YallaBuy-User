package com.example.yallabuy_user.productInfo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yallabuy_user.data.models.productInfo.ProductInfoResponse
import com.example.yallabuy_user.repo.RepositoryInterface
import com.example.yallabuy_user.utilities.ApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ProductInfoViewModel(
    private val repo : RepositoryInterface
) : ViewModel() {

    private val _productInfo : MutableStateFlow<ApiResponse<ProductInfoResponse>>
    = MutableStateFlow(ApiResponse.Loading)

    val productInfo = _productInfo.asStateFlow()

    fun getProductInfoById(productId : Long){
        viewModelScope.launch {
            try {
                val response = repo.getProductById(productId)
                response.collect{ productInfo ->
                    _productInfo.emit(ApiResponse.Success(productInfo))
                }
            }catch ( e : HttpException){
                Log.i("error", "getProductInfoById in view model http error ${e.message} ")
            } catch (e : NullPointerException){
                Log.i("error", "getProductInfoById in view model null point  error ${e.message} ")
            }
        }
    }
}