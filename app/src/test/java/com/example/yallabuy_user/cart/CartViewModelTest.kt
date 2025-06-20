package com.example.yallabuy_user.cart

import com.example.yallabuy_user.cart.viewmodel.CartViewModel
import com.example.yallabuy_user.data.models.cart.DraftOrderBody
import com.example.yallabuy_user.data.models.cart.DraftOrderCart
import com.example.yallabuy_user.data.models.cart.DraftOrderResponse
import com.example.yallabuy_user.data.models.cart.LineItem
import com.example.yallabuy_user.repo.RepositoryInterface
import com.example.yallabuy_user.utilities.ApiResponse
import com.example.yallabuy_user.utilities.CurrencyConversionManager
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {

    private val dispatcher = StandardTestDispatcher()


    private lateinit var repo: RepositoryInterface
    private lateinit var currencyManager: CurrencyConversionManager
    private lateinit var viewModel: CartViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        repo = mockk(relaxed = true)
        currencyManager = mockk(relaxed = true)
        viewModel = CartViewModel(repo, currencyManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun runBlock(block: suspend TestScope.() -> Unit) = runTest(dispatcher) { block() }

    @Test
    fun `fetchCartByDraftOrderId emits Success`() = runBlock {
        val lineItems = mutableListOf(createLineItem())
        val draftCart = DraftOrderCart(id = 1, lineItems = lineItems)
        val response = DraftOrderBody(draftOrderCart = draftCart)

        coEvery { repo.getDraftOrderCart(1L) } returns flowOf(response)

        viewModel.fetchCartByDraftOrderId(1L)
        advanceUntilIdle()

        val result = viewModel.cartState.value
        assert(result is ApiResponse.Success)
        assertEquals(1L, (result as ApiResponse.Success).data.draftOrderCart.id)
    }


    @Test
    fun `removeItemFromCart deletes draft when last item is removed`() = runBlock {

        val item = createLineItem(variantId = 101)
        val draft = DraftOrderCart(id = 5, lineItems = mutableListOf(item))
        viewModel.setDraftOrdersManually(draft)

        coEvery { repo.deleteDraftOrderCart(5L) } returns flowOf(Unit)

        viewModel.removeItemFromCart(5L, 101)
        advanceUntilIdle()

        val state = viewModel.cartState.value
        assertTrue(state is ApiResponse.Success)
        assertEquals(5L, (state).data.draftOrderCart.id)
        assertEquals(0, viewModel.draftOrders.value.let {
            (it as? ApiResponse.Success)?.data?.draftOrderCarts?.size ?: -1
        })
    }


    @Test
    fun `convertItemPrices updates convertedPrices correctly`() = runTest {
        val item = createLineItem(price = "50.0", quantity = 2)
        val cart = DraftOrderCart(id = 3, lineItems = mutableListOf(item))

        coEvery { currencyManager.convertAmount(100.0) } returns 300.0

        viewModel.convertItemPrices(listOf(cart))
        advanceUntilIdle()

        val actual = viewModel.convertedPrices[item.variantID]
        assertEquals("300.00", actual)
    }

    @Test
    fun `decreaseItemQuantity decreases quantity if more than one`() = runBlock {
        val item = createLineItem(variantId = 101, quantity = 2)
        val draft = DraftOrderCart(id = 10, lineItems = mutableListOf(item))
        val updatedItem = item.copy(quantity = 1)
        val updatedDraft = draft.copy(lineItems = mutableListOf(updatedItem))
        val updatedBody = DraftOrderBody(updatedDraft)

        viewModel.setDraftOrdersManually(draft)

        coEvery {
            repo.updateDraftOrder(eq(10L), ofType<DraftOrderBody>())
        } returns flowOf(updatedBody)

        viewModel.decreaseItemQuantity(10L, 101)
        advanceUntilIdle()

        val state = viewModel.cartState.value
        assert(state is ApiResponse.Success)
        assertEquals(1, (state as ApiResponse.Success).data.draftOrderCart.lineItems.first().quantity)
    }


    /*
        @Test
    fun `increaseItemQuantity increases quantity if available`() = runBlock {

        val item = createLineItem(variantId = 101, quantity = 1)
        val originalCart = DraftOrderCart(id = 2, lineItems = mutableListOf(item))
        val updatedItem = item.copy(quantity = 2)
        val updatedCart = originalCart.copy(lineItems = mutableListOf(updatedItem))
        val updatedBody = DraftOrderBody(updatedCart)
        val variant = ProductVariant(VariantDetail(id = 101, inventoryQuantity = 5))

        viewModel.setDraftOrdersManually(originalCart)

        coEvery { repo.getProductVariantById(101) } returns flowOf(variant)

        coEvery {
            repo.updateDraftOrder(
                eq(2L),
                ofType<DraftOrderBody>()
            )
        } returns flowOf(updatedBody)

        viewModel.increaseItemQuantity(2L, 101)
        advanceUntilIdle()

        val state = viewModel.cartState.value
        assert(state is ApiResponse.Success)
        assertEquals(
            2,
            (state as ApiResponse.Success).data.draftOrderCart.lineItems.first().quantity
        )
    }
     */
    private fun createLineItem(
        variantId: Long = 101,
        productId: Long = 1,
        quantity: Int = 1,
        price: String = "100.0"
    ) = LineItem(
        variantID = variantId,
        productID = productId,
        title = "Test Product",
        quantity = quantity,
        price = price,
        properties = emptyList()
    )

    private fun CartViewModel.setDraftOrdersManually(cart: DraftOrderCart) {
        val draftFlow = this::class.java.getDeclaredField("_draftOrders")
        draftFlow.isAccessible = true

        (draftFlow.get(this) as MutableStateFlow<ApiResponse<DraftOrderResponse>>).value =
            ApiResponse.Success(DraftOrderResponse(listOf(cart)))
    }
}