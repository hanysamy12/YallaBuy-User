package com.example.yallabuy_user.repo

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.yallabuy_user.data.models.BrandResponse
import com.example.yallabuy_user.data.models.Image
import com.example.yallabuy_user.data.models.LineItemsItem
import com.example.yallabuy_user.data.models.OrdersItem
import com.example.yallabuy_user.data.models.OrdersResponse
import com.example.yallabuy_user.data.models.ProductImage
import com.example.yallabuy_user.data.models.ProductResponse
import com.example.yallabuy_user.data.models.ProductsItem
import com.example.yallabuy_user.data.models.SmartCollectionsItem
import com.example.yallabuy_user.data.models.VariantsItem
import com.example.yallabuy_user.data.remote.RemoteDataSourceInterface
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepositoryTest {
    private lateinit var remoteDataSource: RemoteDataSourceInterface
    private lateinit var repository: RepositoryInterface

    @Before
    fun setUp() {
        remoteDataSource = mockk(relaxed = true)
        repository = Repository(remoteDataSource)
    }

    @Test
    fun getAllBrands_returnTheeSmartCollections() = runTest {
        val fakeResponse = BrandResponse(
            smartCollections = listOf(
                SmartCollectionsItem(
                    id = 1L,
                    title = "Nike",
                    image = Image(src = "https://example.com/nike.png")
                ),
                SmartCollectionsItem(
                    id = 2L,
                    title = "Adidas",
                    image = Image(src = "https://example.com/adidas.png")
                ),
                SmartCollectionsItem(
                    id = 3L,
                    title = "Puma",
                    image = Image(src = "https://example.com/puma.png")
                )
            )
        )

        coEvery { remoteDataSource.getAllBrands() } returns flowOf(fakeResponse)

        val result = repository.getAllBrands().first()
        val brands = result.smartCollections.orEmpty()

        assertThat(brands.size, `is`(3))
        assertThat(brands[0]?.title, `is`("Nike"))
        assertThat(brands[1]?.title, `is`("Adidas"))
        assertThat(brands[2]?.title, `is`("Puma"))

    }

    @Test
    fun getAllProducts_returnThreeLineItems() = runTest {
        val fakeProductResponse = ProductResponse(
            products = listOf(
                ProductsItem(
                    id = 1L,
                    title = "Product 1",
                    vendor = "Nike",
                    productType = "Shoes",
                    image = ProductImage(src = "https://example.com/product1.jpg"),
                    variants = listOf(
                        VariantsItem(price = "100.0")
                    )
                ),
                ProductsItem(
                    id = 2L,
                    title = "Product 2",
                    vendor = "Adidas",
                    productType = "Shirt",
                    image = ProductImage(src = "https://example.com/product2.jpg"),
                    variants = listOf(
                        VariantsItem(price = "200.0")
                    )
                ),
                ProductsItem(
                    id = 3L,
                    title = "Product 3",
                    vendor = "Puma",
                    productType = "Pants",
                    image = ProductImage(src = "https://example.com/product3.jpg"),
                    variants = listOf(
                        VariantsItem(price = "150.0")
                    )
                )
            )
        )
        coEvery { remoteDataSource.getAllProducts() } returns flowOf(fakeProductResponse)
        val result = repository.getAllProducts().first()
        val products = result.products.orEmpty()
        assertThat(products.size, `is`(3))
        assertThat(products[0]?.title, `is`("Product 1"))
        assertThat(products[1]?.title, `is`("Product 2"))
        assertThat(products[2]?.title, `is`("Product 3"))
    }

    @Test
    fun getPreviousOrders_returnTwoOrders() = runTest {

        val fakeOrdersResponse = OrdersResponse(
            orders = listOf(
                OrdersItem(
                    id = 1L,
                    currentTotalPrice = "120.00",
                    lineItems = listOf(
                        LineItemsItem(
                            title = "Running Shoes",
                            price = "70.00"
                        ),
                        LineItemsItem(
                            title = "Socks Pack",
                            price = "50.00"
                        )
                    )
                ),
                OrdersItem(
                    id = 2L,
                    currentTotalPrice = "200.00",
                    lineItems = listOf(
                        LineItemsItem(
                            title = "Gym Bag",
                            price = "120.00"
                        ),
                        LineItemsItem(
                            title = "Water Bottle",
                            price = "80.00"
                        )
                    )
                )
            )
        )
        coEvery { remoteDataSource.getPreviousOrders(1L) } returns flowOf(fakeOrdersResponse)
        val result = repository.getPreviousOrders(1L).first()
        val orders = result.orders.orEmpty()
        assertThat(orders.size, `is`(2))
        assertThat(orders[0]?.currentTotalPrice, `is`("120.00"))
        assertThat(orders[1]?.currentTotalPrice, `is`("200.00"))
        assertThat(orders[0]?.lineItems?.get(0)?.title , `is`("Running Shoes"))
        assertThat(orders[0]?.lineItems?.get(1)?.title , `is`("Socks Pack"))
        assertThat(orders[1]?.lineItems?.get(0)?.title , `is`("Gym Bag"))
        assertThat(orders[1]?.lineItems?.get(1)?.title , `is`("Water Bottle"))

    }
}