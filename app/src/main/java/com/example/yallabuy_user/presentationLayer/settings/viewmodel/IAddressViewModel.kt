package com.example.yallabuy_user.presentationLayer.settings.viewmodel

import com.example.yallabuy_user.dataLayer.data.models.settings.Address
import com.example.yallabuy_user.dataLayer.data.models.settings.AddressBody
import com.example.yallabuy_user.dataLayer.data.models.settings.AddressesResponse
import com.example.yallabuy_user.dataLayer.data.models.settings.NewAddressResponse
import com.example.yallabuy_user.presentationLayer.ApiResponse
import kotlinx.coroutines.flow.StateFlow

interface IAddressViewModel {
    val addressState: StateFlow<ApiResponse<com.example.yallabuy_user.dataLayer.data.models.settings.AddressesResponse>>
    val addressesList: StateFlow<List<com.example.yallabuy_user.dataLayer.data.models.settings.Address>>
    val singleAddressState: StateFlow<ApiResponse<com.example.yallabuy_user.dataLayer.data.models.settings.NewAddressResponse>>
    val createUpdateState: StateFlow<ApiResponse<com.example.yallabuy_user.dataLayer.data.models.settings.NewAddressResponse>>
    val deleteState: StateFlow<ApiResponse<Unit>>
    fun setCustomerId(id: Long)
    fun getCustomerId(): Long
    fun getAddresses()
    fun createAddress(addressBody: com.example.yallabuy_user.dataLayer.data.models.settings.AddressBody)
    fun updateAddress(addressId: Long, updatedAddressBody: com.example.yallabuy_user.dataLayer.data.models.settings.AddressBody)
    fun deleteAddress(addressId: Long)
    fun setDefaultAddress(selectedAddress: com.example.yallabuy_user.dataLayer.data.models.settings.Address)
}