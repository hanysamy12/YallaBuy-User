package com.example.yallabuy_user.dataLayer.data.models.settings

interface AddressDisplayInfo {
    fun getRecipientFullName(): String
    fun getAddressLine(): String
    fun getDetailedDescription(): String
}