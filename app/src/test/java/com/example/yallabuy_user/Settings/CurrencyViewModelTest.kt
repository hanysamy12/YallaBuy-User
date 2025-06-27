package com.example.yallabuy_user.Settings

import com.example.yallabuy_user.repo.ICurrencyRepository
import com.example.yallabuy_user.settings.viewmodel.CurrencyViewModel
import com.example.yallabuy_user.utilities.ApiResponse
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CurrencyViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: ICurrencyRepository
    private lateinit var viewModel: CurrencyViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        repository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun runCurrentTest(block: suspend TestScope.() -> Unit) = runTest(dispatcher) { block() }

    @Test
    fun `init loads preferred currency and sets selectedCurrency`() = runCurrentTest {
        coEvery { repository.getPreferredCurrency() } returns "EUR"
        coEvery { repository.setPreferredCurrency("EUR") } returns Unit
        coEvery { repository.getCurrencyRate("EGP", "EUR") } returns 33.0

        viewModel = CurrencyViewModel(repository)
        advanceUntilIdle()

        assertEquals("EUR", viewModel.selectedCurrency.value)
        assert(viewModel.currencyState.value is ApiResponse.Success)
        assertEquals(33.0, (viewModel.currencyState.value as ApiResponse.Success).data)
    }

    @Test
    fun `selectCurrency sets currency and updates rate`() = runCurrentTest {
        coEvery { repository.getPreferredCurrency() } returns "EGP"

        coEvery { repository.setPreferredCurrency("USD") } returns Unit
        coEvery { repository.getCurrencyRate("EGP", "USD") } returns 30.0

        viewModel = CurrencyViewModel(repository)
        advanceUntilIdle()

        viewModel.selectCurrency("USD")
        advanceUntilIdle()

        assertEquals("USD", viewModel.selectedCurrency.value)

        val state = viewModel.currencyState.value
        assert(state is ApiResponse.Success)
        assertEquals(30.0, (state as ApiResponse.Success).data)
    }


    @Test
    fun `selectCurrency sets Failure when repository throws exception`() = runCurrentTest {
        coEvery { repository.getPreferredCurrency() } returns "EGP"
        coEvery { repository.setPreferredCurrency("USD") } throws RuntimeException("Network error")

        viewModel = CurrencyViewModel(repository)
        advanceUntilIdle()

        viewModel.selectCurrency("USD")
        advanceUntilIdle()

        val state = viewModel.currencyState.value
        assert(state is ApiResponse.Failure)

        val exception = (state as ApiResponse.Failure).error
        assert(exception is RuntimeException)
        assertEquals("Network error", exception.message)
    }

    }
