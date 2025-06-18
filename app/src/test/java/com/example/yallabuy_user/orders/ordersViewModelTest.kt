package com.example.yallabuy_user.orders

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.yallabuy_user.data.models.LineItemsItem
import com.example.yallabuy_user.data.models.Order
import com.example.yallabuy_user.data.models.OrderDetailsResponse
import com.example.yallabuy_user.data.models.OrdersItem
import com.example.yallabuy_user.data.models.OrdersResponse
import com.example.yallabuy_user.repo.RepositoryInterface
import com.example.yallabuy_user.utilities.ApiResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ordersViewModelTest {
    private lateinit var repository: RepositoryInterface
    private lateinit var viewModel: OrdersViewModel

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        viewModel = OrdersViewModel(repository)
    }

    @Test
    fun getPreviewsOrders_returnPreOrdersForCustomer() = runTest {
        val mockSimpleOrdersResponse = OrdersResponse(
            orders = listOf(
                OrdersItem(
                    id = 1001,
                    orderNumber = 1,
                    lineItems = listOf(
                        LineItemsItem(
                            title = "T-Shirt",
                            price = "20.00"
                        ),
                        LineItemsItem(
                            title = "Shoes",
                            price = "25.00"
                        )
                    )
                ),
                OrdersItem(
                    id = 1002,
                    orderNumber = 2,
                    lineItems = listOf(
                        LineItemsItem(
                            title = "Mug",
                            price = "15.00"
                        ),
                        LineItemsItem(
                            title = "Hat",
                            price = "10.00"
                        )

                    )
                )
            )
        )
        coEvery { repository.getPreviousOrders(1L) } returns flowOf(mockSimpleOrdersResponse)
        viewModel.getPreviousOrders(ApplicationProvider.getApplicationContext())
        val result = viewModel.orders.value
        val expectedList = mockSimpleOrdersResponse.orders?.filterNotNull()
        val actualList = (result as ApiResponse.Success).data
        assertThat(actualList, `is`(expectedList))

    }

    @Test
    fun getPreviewsOrderById_returnRightOrder() = runTest {
        val fakeOrder = OrderDetailsResponse(
            order = Order(
                id = 1L,
                currency = "EGP",
                totalPrice = "100.00",
                lineItems = listOf(
                    LineItemsItem(
                        title = "T-Shirt",
                        price = "20.00"
                    ),
                    LineItemsItem(
                        title = "Shoes",
                        price = "25.00"
                    )
                )
            )

        )
        coEvery { repository.getOrderById(1L) } returns flowOf(fakeOrder)
        viewModel.getOrderById(1L)
        val result = viewModel.orderProducts.value
        val expectedOrder = fakeOrder.order
        val actualOrder = (result as ApiResponse.Success).data
        assertThat(actualOrder, `is`(expectedOrder))

    }
}