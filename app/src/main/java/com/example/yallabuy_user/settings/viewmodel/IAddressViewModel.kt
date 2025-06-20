package com.example.yallabuy_user.settings.viewmodel

import com.example.yallabuy_user.data.models.settings.Address
import com.example.yallabuy_user.data.models.settings.AddressBody
import com.example.yallabuy_user.data.models.settings.AddressesResponse
import com.example.yallabuy_user.data.models.settings.NewAddressResponse
import com.example.yallabuy_user.utilities.ApiResponse
import kotlinx.coroutines.flow.StateFlow

interface IAddressViewModel {
    val addressState: StateFlow<ApiResponse<AddressesResponse>>
    val addressesList: StateFlow<List<Address>>
    val singleAddressState: StateFlow<ApiResponse<NewAddressResponse>>
    val createUpdateState: StateFlow<ApiResponse<NewAddressResponse>>
    val deleteState: StateFlow<ApiResponse<Unit>>
    fun setCustomerId(id: Long)
    fun getCustomerId(): Long
    fun getAddresses()
    fun createAddress(addressBody: AddressBody)
    fun updateAddress(addressId: Long, updatedAddressBody: AddressBody)
    fun deleteAddress(addressId: Long)
    fun setDefaultAddress(selectedAddress: Address)
}