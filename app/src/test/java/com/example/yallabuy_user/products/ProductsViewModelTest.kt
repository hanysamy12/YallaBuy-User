package com.example.yallabuy_user.products

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.yallabuy_user.data.models.ProductImage
import com.example.yallabuy_user.data.models.ProductResponse
import com.example.yallabuy_user.data.models.ProductsItem
import com.example.yallabuy_user.repo.RepositoryInterface
import com.example.yallabuy_user.utilities.ApiResponse
import com.example.yallabuy_user.utilities.CurrencyConversionManager
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductsViewModelTest {
    private lateinit var repository: RepositoryInterface
    private lateinit var viewModel: ProductsViewModel
    private lateinit var currencyConversionManager: CurrencyConversionManager

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        currencyConversionManager = mockk(relaxed = true)
        viewModel = ProductsViewModel(repository,currencyConversionManager)
    }

    @Test
    fun getProducts_returnVendorProducts() = runTest {
        val fakeProductResponse = ProductResponse(
            products = listOf(
                ProductsItem(
                    id = 1L,
                    title = "Nike Air Max",
                    vendor = "Nike",
                    productType = "Shoes",
                    image = ProductImage(src = "https://example.com/nike.jpg"),
                    status = "active"
                ), ProductsItem(
                    id = 2L,
                    title = "Adidas Ultra Boost",
                    vendor = "Adidas",
                    productType = "Shoes",
                    image = ProductImage(src = "https://example.com/adidas.jpg"),
                    status = "active"
                ), ProductsItem(
                    id = 3L,
                    title = "Puma RS-X",
                    vendor = "Puma",
                    productType = "Shoes",
                    image = ProductImage(src = "https://example.com/puma.jpg"),
                    status = "active"
                )
            )
        )
        coEvery { repository.getAllProducts() } returns flowOf(
            ProductResponse(
                products = fakeProductResponse.products?.filter { it?.vendor == "Nike" })
        )

        viewModel.getProducts(vendorName = "Nike")
        val result = viewModel.products.value
        val expectedList = fakeProductResponse.products?.filter { it?.vendor == "Nike" }
        val actualList = (result as ApiResponse.Success).data
        assertThat(actualList, `is`(expectedList))
    }

    @Test
    fun getCategoryProducts_returnCorrectCategoryProducts() = runTest {
        val fakeProductResponse = ProductResponse(
            products = listOf(
                ProductsItem(
                    id = 1L,
                    title = "Nike Air Max",
                    vendor = "Men",
                    productType = "Shoes",
                    image = ProductImage(src = "https://example.com/nike.jpg"),
                    status = "active"
                ), ProductsItem(
                    id = 2L,
                    title = "Adidas Ultra Boost",
                    vendor = "Women",
                    productType = "Shoes",
                    image = ProductImage(src = "https://example.com/adidas.jpg"),
                    status = "active"
                ), ProductsItem(
                    id = 3L,
                    title = "Puma RS-X",
                    vendor = "Puma",
                    productType = "Shoes",
                    image = ProductImage(src = "https://example.com/puma.jpg"),
                    status = "active"
                )
            )
        )
        val fakeCategoryProducts = ProductResponse(
            products = listOf(
                ProductsItem(
                    id = 1L,
                    title = "Nike Air Max",
                    vendor = "Men",
                    productType = "Shoes",
                    image = ProductImage(src = "https://example.com/nike.jpg"),
                    status = "active"
                )
            )
        )
        coEvery { repository.getAllProducts() } returns flowOf(fakeProductResponse)
        coEvery { repository.getCategoryProducts(1L) } returns flowOf(fakeCategoryProducts)

        viewModel.getCategoryProducts(1L)
        val result = viewModel.products.value
        val expectedList = fakeProductResponse.products?.filter { it?.vendor == "Men" }
        val actualList = (result as ApiResponse.Success).data
        assertThat(actualList, `is`(expectedList))

    }
}