package com.example.yallabuy_user.Settings

import com.example.yallabuy_user.data.models.settings.Address
import com.example.yallabuy_user.data.models.settings.AddressBody
import com.example.yallabuy_user.data.models.settings.AddressesResponse
import com.example.yallabuy_user.data.models.settings.NewAddressResponse
import com.example.yallabuy_user.repo.RepositoryInterface
import com.example.yallabuy_user.settings.viewmodel.AddressViewModel
import com.example.yallabuy_user.utilities.ApiResponse
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
class AddressViewModelTest {

    private val testDispatcher = StandardTestDispatcher()



    private lateinit var repository: RepositoryInterface
    private lateinit var viewModel: AddressViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk<RepositoryInterface>(relaxed = true)
        viewModel = AddressViewModel(repository)
        viewModel.setCustomerId(123L)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun runCurrentTest(block: suspend TestScope.() -> Unit) = runTest(testDispatcher) { block() }

    @Test
    fun `getAddresses emits Success and updates addressesList`() = runCurrentTest {
        val mockAddresses = listOf(Address(
            id = 1,
            default = false,
            customerId = 123L,
            phone = "0100000000",
            firstName = "Mariam",
            lastName = "Mo",
            fullAddress = "123 Helwan",
            city = "Cairo",
            country = "Egypt"
        ))
        val response = AddressesResponse(addresses = mockAddresses)

        coEvery { repository.getAddresses(123L) } returns flowOf(response)

        viewModel.getAddresses()
        advanceUntilIdle()

        assert(viewModel.addressState.value is ApiResponse.Success)
        assertEquals(mockAddresses, viewModel.addressesList.value)
    }

    @Test
    fun `createAddress adds new address and emits Success`() = runCurrentTest {
        val newAddress = Address(
            id = 2,
            default = false,
            customerId = 123L,
            phone = "0123456789",
            firstName = "Mariam",
            lastName = "Mo",
            fullAddress = "456 Corniche St",
            city = "Giza",
            country = "Egypt"
        )
        val addressBody = AddressBody(address = newAddress)
        val response = NewAddressResponse(address = newAddress)

        coEvery { repository.createCustomerAddress(123L, addressBody) } returns flowOf(response)

        viewModel.createAddress(addressBody)
        advanceUntilIdle()

        assert(viewModel.createUpdateState.value is ApiResponse.Success)

        assert(viewModel.addressesList.value.any { it.id == newAddress.id })
    }

    @Test
    fun `updateAddress modifies address in list and emits Success`() = runCurrentTest {
        val oldAddress = Address(
            id = 3,
            default = false,
            customerId = 123L,
            phone = "0111111111",
            firstName = "Old",
            lastName = "User",
            fullAddress = "Old St",
            city = "Old City",
            country = "Egypt"
        )

        val updatedAddress = oldAddress.copy(
            phone = "0222222222",
            default = true
        )
        val updatedBody = AddressBody(address = updatedAddress)
        val response = NewAddressResponse(address = updatedAddress)

        viewModel._addressesList.value = listOf(oldAddress)

        coEvery { repository.updateCustomerAddress(123L, 3L, updatedBody) } returns flowOf(response)

        viewModel.updateAddress(3L, updatedBody)
        advanceUntilIdle()

        val list = viewModel.addressesList.value
        assert(list.any { it.id == 3L && it.default })
        assert(viewModel.createUpdateState.value is ApiResponse.Success)
    }

    @Test
    fun `deleteAddress removes address and emits Success`() = runCurrentTest {
        val addressToDelete = Address(
            id = 4,
            default = false,
            customerId = 123L,
            phone = "0109999999",
            firstName = "Delete",
            lastName = "Me",
            fullAddress = "Bye St",
            city = "Alex",
            country = "Egypt"
        )

        viewModel._addressesList.value = listOf(addressToDelete)

        coEvery { repository.deleteCustomerAddress(123L, 4L) } returns Unit

        viewModel.deleteAddress(4L)
        advanceUntilIdle()

        assert(viewModel.deleteState.value is ApiResponse.Success)
        assert(!viewModel.addressesList.value.contains(addressToDelete))
    }

}