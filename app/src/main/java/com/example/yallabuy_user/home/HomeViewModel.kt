package com.example.yallabuy_user.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.yallabuy_user.repo.RepositoryInterface
import com.example.yallabuy_user.utilities.ApiResponse
import com.example.yallabuy_user.data.models.CustomCollectionsItem
import com.example.yallabuy_user.data.models.SmartCollectionsItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class HomeViewModel(private val repo: RepositoryInterface) : ViewModel(), HomeViewModelInterface {
    private val _categories =
        MutableStateFlow<ApiResponse<List<CustomCollectionsItem>>>(ApiResponse.Loading)
    val categories: StateFlow<ApiResponse<List<CustomCollectionsItem>>> = _categories

    private val _brands =
        MutableStateFlow<ApiResponse<List<SmartCollectionsItem>>>(ApiResponse.Loading)
    val brands: StateFlow<ApiResponse<List<SmartCollectionsItem>>> = _brands

    override suspend fun getAllCategories() {
        try {
            Log.i("TAG", "getAllCategories: ")
            repo.getAllCategories()
                .map { it.customCollections.orEmpty().filterNotNull()}
                .catch { error -> _categories.value = ApiResponse.Failure(error) }
                .collect{categories -> _categories.value = ApiResponse.Success(categories)
                    Log.i("TAG", "Collect: $categories")
                }

        } catch (e: Exception) {
            _categories.value = ApiResponse.Failure(e)
        }

    }

    override suspend fun getAllBrands() {
        try {
            repo.getAllBrands()
                .map { it.smartCollections.orEmpty().filterNotNull()}
                .catch { error -> _brands.value = ApiResponse.Failure(error) }
                .collect{brands -> _brands.value = ApiResponse.Success(brands)}
        } catch (e: Exception) {
            _categories.value = ApiResponse.Failure(e)
        }

    }
}
