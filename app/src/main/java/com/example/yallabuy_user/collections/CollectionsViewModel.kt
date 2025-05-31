package com.example.yallabuy_user.collections

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.yallabuy_user.data.models.CustomCollectionsItem
import com.example.yallabuy_user.data.models.ProductsItem
import com.example.yallabuy_user.repo.RepositoryInterface
import com.example.yallabuy_user.utilities.ApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private const val TAG = "CollectionsViewModel"


class CollectionsViewModel(private val repo: RepositoryInterface) : ViewModel(),
    CollectionsViewModelInterface {

    private val _categories =
        MutableStateFlow<ApiResponse<List<CustomCollectionsItem>>>(ApiResponse.Loading)
    val categories: StateFlow<ApiResponse<List<CustomCollectionsItem>>> = _categories

    private val _products = MutableStateFlow<ApiResponse<List<ProductsItem>>>(ApiResponse.Loading)
    val products: StateFlow<ApiResponse<List<ProductsItem>>> = _products

    private var originalProducts: List<ProductsItem> = emptyList()

    override suspend fun getAllCategories() {
        try {
            repo.getAllCategories()
                .map { it.customCollections.orEmpty().filterNotNull() }
                .catch { error -> _categories.value = ApiResponse.Failure(error) }
                .collect { categories -> _categories.value = ApiResponse.Success(categories) }
        } catch (e: Exception) {
            _categories.value = ApiResponse.Failure(e)
        }
    }

    override suspend fun getCategoryProducts(categoryID: Long) {
        try {
            repo.getCategoryProducts(categoryID)
                .map { it.products.orEmpty().filterNotNull() }
                .catch { error -> _products.value = ApiResponse.Failure(error) }
                .collect { products ->
                    originalProducts = products
                    _products.value = ApiResponse.Success(products)

                }

        } catch (e: Exception) {
            _products.value = ApiResponse.Failure(e)
        }
    }

    override fun showSubCategoryProduct(subCategory: String) {
       Log.i(TAG, "showSubCategoryProduct: $subCategory")
        val filteredList = originalProducts.filter {
            it.productType?.trim()?.equals(subCategory, ignoreCase = true) == true
        }
        _products.value = ApiResponse.Success(filteredList)
        Log.i(TAG, "showSubCategoryProduct: ${_products.value}")
    }
}