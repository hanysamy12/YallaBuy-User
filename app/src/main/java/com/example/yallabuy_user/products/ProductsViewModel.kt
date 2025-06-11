package com.example.yallabuy_user.products

import android.util.Log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yallabuy_user.data.models.CustomCollectionsItem
import com.example.yallabuy_user.data.models.ProductsItem
import com.example.yallabuy_user.repo.RepositoryInterface
import com.example.yallabuy_user.settings.viewmodel.CurrencyConversionManager
import com.example.yallabuy_user.utilities.ApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Locale

private const val TAG = "ProductsViewModel"
//currency conversion
//get currency code if it's in egyptian I will put it as it is
//if different currency than EGP so I will check the time if it more than 24 hours I will hit the api and get the new rate
//save the rate and date in the shared preference and do here the conversion based on the currency

class ProductsViewModel(private val repo: RepositoryInterface,
                        private val currencyConversionManager: CurrencyConversionManager
) : ViewModel(),
    ProductsViewModelInterface {

    private val _categories =
        MutableStateFlow<ApiResponse<List<CustomCollectionsItem>>>(ApiResponse.Loading)
    val categories: StateFlow<ApiResponse<List<CustomCollectionsItem>>> = _categories

    private val _products = MutableStateFlow<ApiResponse<List<ProductsItem>>>(ApiResponse.Loading)
    val products: StateFlow<ApiResponse<List<ProductsItem>>> = _products

    private var originalProducts: List<ProductsItem> = emptyList()

    override suspend fun getAllCategories() {
        try {
            repo.getAllCategories().map { it.customCollections.orEmpty().filterNotNull() }
                .catch { error -> _categories.value = ApiResponse.Failure(error) }
                .collect { categories -> _categories.value = ApiResponse.Success(categories) }
        } catch (e: Exception) {
            _categories.value = ApiResponse.Failure(e)
        }
    }

    override suspend fun getProducts(vendorName: String?) {
        try {
            repo.getAllProducts()
                .map { it.products.orEmpty().filterNotNull() }
                .catch { error -> _products.value = ApiResponse.Failure(error) }
                .map { products ->
                    vendorName?.let { vendor ->
                        products.filter {
                            it.vendor?.trim()?.equals(vendor, ignoreCase = true) == true
                        }
                    } ?: products
                }
                .map { products ->
                    convertProductPrices(products)
                }
                .collect { convertedProducts ->
                    originalProducts = convertedProducts
                    _products.value = ApiResponse.Success(convertedProducts)
                }
        } catch (e: Exception) {
            _products.value = ApiResponse.Failure(e)
        }
    }


    private suspend fun convertProductPrices(products: List<ProductsItem>): List<ProductsItem> {
        Log.i("```TAG```", "convertProductPrices: first log")
        val convertedProducts = mutableListOf<ProductsItem>()
        Log.d("```TAG```", "convertProductPrices: ${products.size}")
        for (product in products) {
            Log.d("````TAG````", "product varaiant count: ${product.variants?.size}")
            val convertedVariants = product.variants?.map { variant ->
                variant?.let {
                    Log.d("```TAG```", "convertProductPrices: variant is not null - ${variant.id}")
                    val originalPrice = it.price?.toDoubleOrNull() ?: 0.0
                    val convertedPrice = currencyConversionManager.convertAmount(originalPrice)
                    Log.i("```TAG```", "convertProductPrices: original price is $originalPrice, converted price is $convertedPrice")
                    it.copy(price = "%.2f".format(convertedPrice))
                }
            }
            convertedProducts.add(product.copy(variants = convertedVariants))
        }
        return convertedProducts
    }

    //get category products then get all products and filter by Product ID
    override suspend fun getCategoryProducts(categoryID: Long) {
        try {
            repo.getCategoryProducts(categoryID).map { it.products.orEmpty().filterNotNull() }
                .catch { error -> _products.value = ApiResponse.Failure(error) }
                .collect { products ->
                    getCategoryProductFromAllProducts(products).let {
                        _products.value = ApiResponse.Success(it)
                        originalProducts = it
                    }
                }
        } catch (e: Exception) {
            _products.value = ApiResponse.Failure(e)
        }
    }

    override fun showSubCategoryProduct(subCategory: String) {
        val filteredList = originalProducts.filter {
            it.productType?.trim()?.equals(subCategory, ignoreCase = true) == true
        }
        _products.value = ApiResponse.Success(filteredList)
    }

    override fun showFilteredProduct(minPrice: Float, maxPrice: Float) {
        val filteredList = originalProducts.filter { product ->
            (product.variants?.get(0)?.price?.toFloatOrNull() ?: Float.MAX_VALUE) <= maxPrice
        }
        _products.value = ApiResponse.Success(filteredList)
    }

    private suspend fun getCategoryProductFromAllProducts(
        categoryProducts: List<ProductsItem>,
    ): List<ProductsItem> {
        val categoryProductIDs = categoryProducts.mapNotNull { it.id }
        val allProducts = repo.getAllProducts().map { it.products.orEmpty().filterNotNull() }
            .catch { error -> _products.value = ApiResponse.Failure(error) }
            .first()
        val filtered = allProducts.filter { it.id in categoryProductIDs }

        return convertProductPrices(filtered)
    }

    override fun searchForProduct(productName: String) {
        viewModelScope.launch {
            val filterProducts = originalProducts.filter { product ->
                product.title?.contains(productName.toUpperCase(Locale.ROOT)) ?: false
            }
            _products.emit(ApiResponse.Success(filterProducts))
        }
    }
}