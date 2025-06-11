package com.example.yallabuy_user.settings.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yallabuy_user.data.models.settings.AddressBody
import com.example.yallabuy_user.data.models.settings.AddressesResponse
import com.example.yallabuy_user.data.models.settings.NewAddressResponse
import com.example.yallabuy_user.repo.RepositoryInterface
import com.example.yallabuy_user.utilities.ApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AddressViewModel(
    private val repository: RepositoryInterface,
    ) : ViewModel() {

    private val _addressState = MutableStateFlow<ApiResponse<AddressesResponse>>(ApiResponse.Loading)
    val addressState: StateFlow<ApiResponse<AddressesResponse>> = _addressState.asStateFlow()

    private val _singleAddressState = MutableStateFlow<ApiResponse<NewAddressResponse>>(ApiResponse.Loading)
    val singleAddressState: StateFlow<ApiResponse<NewAddressResponse>> = _singleAddressState.asStateFlow()

    private val _createUpdateState = MutableStateFlow<ApiResponse<NewAddressResponse>>(ApiResponse.Loading)
    val createUpdateState: StateFlow<ApiResponse<NewAddressResponse>> = _createUpdateState.asStateFlow()

    private val _deleteState = MutableStateFlow<ApiResponse<Unit>>(ApiResponse.Loading)
    val deleteState: StateFlow<ApiResponse<Unit>> = _deleteState.asStateFlow()

    private var customerId: Long =0L

      fun  setCustomerId(id: Long) {
            customerId = id
        }

    fun getCustomerId() = customerId

    fun getAddresses() {
        viewModelScope.launch {
            _addressState.value = ApiResponse.Loading
            repository.getAddresses(customerId)
                .catch { e -> _addressState.value = ApiResponse.Failure(e) }
                .collect { response -> _addressState.value = ApiResponse.Success(response) }
        }
    }

    fun getAddressById( addressId: Long) {
        viewModelScope.launch {
            _singleAddressState.value = ApiResponse.Loading
            repository.getCustomerAddressById(customerId, addressId)
                .catch { e -> _singleAddressState.value = ApiResponse.Failure(e) }
                .collect { response -> _singleAddressState.value = ApiResponse.Success(response) }
        }
    }

    fun createAddress( addressBody: AddressBody) {
        viewModelScope.launch {
            Log.i("TAG", "createAddress: $addressBody")
            _createUpdateState.value = ApiResponse.Loading
            repository.createCustomerAddress(customerId, addressBody)
                .catch { e -> _createUpdateState.value = ApiResponse.Failure(e) }
                .collect { response -> _createUpdateState.value = ApiResponse.Success(response) }
        }
    }

    fun updateAddress( addressId: Long, updatedAddressBody: AddressBody) {
        viewModelScope.launch {
            _createUpdateState.value = ApiResponse.Loading
            repository.updateCustomerAddress(customerId, addressId, updatedAddressBody)
                .catch { e -> _createUpdateState.value = ApiResponse.Failure(e) }
                .collect { response -> _createUpdateState.value = ApiResponse.Success(response) }
        }
    }

    fun deleteAddress(addressId: Long) {
        viewModelScope.launch {
            _deleteState.value = ApiResponse.Loading
            try {
                repository.deleteCustomerAddress(customerId, addressId)
                _deleteState.value = ApiResponse.Success(Unit)
            } catch (e: Exception) {
                _deleteState.value = ApiResponse.Failure(e)
            }
        }
    }
}

//class AddressViewModelFactory(
//    private val addressRepository: IAddressRepository,
//    private val customerId: Long
//) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(AddressViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return AddressViewModel(addressRepository, customerId) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}
