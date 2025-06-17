package com.example.yallabuy_user.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yallabuy_user.repo.RepositoryInterface
import com.example.yallabuy_user.utilities.ApiResponse
import com.example.yallabuy_user.data.models.CustomCollectionsItem
import com.example.yallabuy_user.data.models.Coupon.DiscountCodeCoupon
import com.example.yallabuy_user.data.models.Coupon.PriceRule
import com.example.yallabuy_user.data.models.SmartCollectionsItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HomeViewModel(private val repo: RepositoryInterface) : ViewModel(), HomeViewModelInterface {
    private val _categories =
        MutableStateFlow<ApiResponse<List<CustomCollectionsItem>>>(ApiResponse.Loading)
    val categories: StateFlow<ApiResponse<List<CustomCollectionsItem>>> = _categories

    private val _brands =
        MutableStateFlow<ApiResponse<List<SmartCollectionsItem>>>(ApiResponse.Loading)
    val brands: StateFlow<ApiResponse<List<SmartCollectionsItem>>> = _brands

    private val _allCoupons =
        MutableStateFlow<ApiResponse<List<DiscountCodeCoupon>>>(ApiResponse.Loading)
    val allCoupons: StateFlow<ApiResponse<List<DiscountCodeCoupon>>> = _allCoupons

    private val _priceRules =
        MutableStateFlow<ApiResponse<List<PriceRule>>>(ApiResponse.Loading)
    val priceRules: StateFlow<ApiResponse<List<PriceRule>>> = _priceRules

    init {
        viewModelScope.launch {
            getAllPriceRulesAndCoupons()
        }
    }
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

    private suspend fun fetchAllCouponsForAllPriceRules(priceRules: List<PriceRule>) {
        try {
            _allCoupons.value = ApiResponse.Loading // Set loading state for coupons
            val allCouponsList = mutableListOf<DiscountCodeCoupon>()
            priceRules.forEach { priceRule ->
                repo.getAllCouponsForRule(priceRule.id)
                    .catch { e -> Log.e("Coupons", "Error fetching coupons for price rule ${priceRule.id}", e) }
                    .collect { coupons -> allCouponsList.addAll(coupons) }
            }
            _allCoupons.value = ApiResponse.Success(allCouponsList)
        } catch (e: Exception) {
            _allCoupons.value = ApiResponse.Failure(e)
        }
    }


//    suspend fun fetchAllCouponsForAllPriceRules() {
//        try {
//            val priceRulesResult = priceRules.value
//            if (priceRulesResult !is ApiResponse.Success) return
//
//            val allCoupons = mutableListOf<DiscountCodeCoupon>()
//
//            priceRulesResult.data.forEach { priceRule ->
//                repo.getAllCouponsForRule(priceRule.id)
//                    .catch { e -> Log.e("Coupons", "Error fetching coupons", e) }
//                    .collect { coupons -> allCoupons.addAll(coupons) }
//            }
//
//            _allCoupons.value = ApiResponse.Success(allCoupons)
//        } catch (e: Exception) {
//            _allCoupons.value = ApiResponse.Failure(e)
//        }
//    }

    private suspend fun getAllPriceRulesAndCoupons() {
        try {
            _priceRules.value = ApiResponse.Loading // Set loading state for price rules
            repo.fetchPriceRules()
                .catch { error -> _priceRules.value = ApiResponse.Failure(error) }
                .collect { rules ->
                    _priceRules.value = ApiResponse.Success(rules)

                    fetchAllCouponsForAllPriceRules(rules)
                }
        } catch (e: Exception) {
            _priceRules.value = ApiResponse.Failure(e)
        }
    }

//    suspend fun getAllPriceRules() {
//        try {
//            repo.fetchPriceRules()
//                .catch { error -> _priceRules.value = ApiResponse.Failure(error) }
//                .collect { rules -> _priceRules.value = ApiResponse.Success(rules) }
//        } catch (e: Exception) {
//            _priceRules.value = ApiResponse.Failure(e)
//        }
//    }
}
