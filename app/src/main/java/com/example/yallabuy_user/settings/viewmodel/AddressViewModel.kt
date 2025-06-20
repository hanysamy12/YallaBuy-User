package com.example.yallabuy_user.settings.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yallabuy_user.data.models.settings.Address
import com.example.yallabuy_user.data.models.settings.AddressBody
import com.example.yallabuy_user.data.models.settings.AddressesResponse
import com.example.yallabuy_user.data.models.settings.NewAddressResponse
import com.example.yallabuy_user.repo.RepositoryInterface
import com.example.yallabuy_user.utilities.ApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddressViewModel(
    private val repository: RepositoryInterface,
    ) : ViewModel(), IAddressViewModel {

    private val _addressState = MutableStateFlow<ApiResponse<AddressesResponse>>(ApiResponse.Loading)
    override val addressState: StateFlow<ApiResponse<AddressesResponse>> = _addressState.asStateFlow()

    val _addressesList = MutableStateFlow<List<Address>>(emptyList())
     override val addressesList = _addressesList.asStateFlow()

    private val _singleAddressState = MutableStateFlow<ApiResponse<NewAddressResponse>>(ApiResponse.Loading)
    override val singleAddressState: StateFlow<ApiResponse<NewAddressResponse>> = _singleAddressState.asStateFlow()

    private val _createUpdateState = MutableStateFlow<ApiResponse<NewAddressResponse>>(ApiResponse.Loading)
    override val createUpdateState: StateFlow<ApiResponse<NewAddressResponse>> = _createUpdateState.asStateFlow()

    private val _deleteState = MutableStateFlow<ApiResponse<Unit>>(ApiResponse.Loading)
    override val deleteState: StateFlow<ApiResponse<Unit>> = _deleteState.asStateFlow()

    private var customerId: Long =0L

      override fun  setCustomerId(id: Long) {
            customerId = id
        }

    override fun getCustomerId() = customerId

    override fun getAddresses() {
        viewModelScope.launch {
            _addressState.value = ApiResponse.Loading
            repository.getAddresses(customerId)
                .catch { e -> _addressState.value = ApiResponse.Failure(e) }
                .collect { response ->
                    run {
                        _addressesList.value = response.addresses
                        _addressState.value = ApiResponse.Success(response)
                    }
                }
        }
    }

    override fun createAddress(addressBody: AddressBody) {
        if (customerId == 0L) {
            //Log.e("AddressViewModel", "CustomerId not set before creating address!")
            return
        }
        viewModelScope.launch {
            //Log.i("TAG", "createAddress: $addressBody")
            _createUpdateState.value = ApiResponse.Loading

            repository.createCustomerAddress(customerId, addressBody)
                .catch { e -> _createUpdateState.value = ApiResponse.Failure(e) }
                .collect { response ->
                    val newAddress = response.address

                    _addressesList.update { list ->
                        val newAddress = response.address
                        val updatedList = if (newAddress.default) {
                            list.map { it.copy(default = false) } + newAddress
                        } else {
                            list + newAddress
                        }
                        updatedList
                    }
                    _createUpdateState.value = ApiResponse.Success(response)
                }
        }
    }

    override fun updateAddress(addressId: Long, updatedAddressBody: AddressBody) {
        viewModelScope.launch {
            _createUpdateState.value = ApiResponse.Loading
            repository.updateCustomerAddress(customerId, addressId, updatedAddressBody)
                .catch { e -> _createUpdateState.value = ApiResponse.Failure(e) }

                .collect { response ->
                    response.address.let { updated ->
                        _addressesList.update { list ->
                            if (updated.default) {
                                list.map { it.copy(default = (it.id == updated.id)) }
                            } else {
                                list.map { if (it.id == updated.id) updated else it }
                            }
                        }
                    }

                    _createUpdateState.value = ApiResponse.Success(response) }
        }
    }

    override fun deleteAddress(addressId: Long) {
        viewModelScope.launch {
            _deleteState.value = ApiResponse.Loading
            try {
                repository.deleteCustomerAddress(customerId, addressId)
                _addressesList.update { list ->
                    list.filterNot { it.id == addressId }
                }
                _deleteState.value = ApiResponse.Success(Unit)
            } catch (e: Exception) {
                _deleteState.value = ApiResponse.Failure(e)
            }
        }
    }
    override fun setDefaultAddress(selectedAddress: Address) {
        viewModelScope.launch {
            if (selectedAddress.default) return@launch

            val updatedAddress = selectedAddress.copy(default = true)

            val updatedBody = AddressBody(address = updatedAddress)

            _createUpdateState.value = ApiResponse.Loading

            repository.updateCustomerAddress(customerId, selectedAddress.id, updatedBody)
                .catch { e ->
                    _createUpdateState.value = ApiResponse.Failure(e)
                }
                .collect { response ->
                    val newDefaultId = response.address.id
                    _addressesList.update { list ->
                        list.map { address ->
                            address.copy(default = (address.id == newDefaultId))
                        }
                    }

                    _createUpdateState.value = ApiResponse.Success(response)
                }
        }
    }

}

