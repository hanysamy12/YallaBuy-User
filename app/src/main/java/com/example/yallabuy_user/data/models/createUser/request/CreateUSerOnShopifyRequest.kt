package com.example.yallabuy_user.data.models.createUser.request

import com.google.gson.annotations.SerializedName


data class CreateUSerOnShopifyRequest(
    val customer: CustomerRequest
)
data class CustomerRequest(
    @SerializedName("first_name")
    val firstName: String,
    val email: String,

    val password: String? = null,
    @SerializedName("password_confirmation")
    val passwordConfirmation: String? = null,

    @SerializedName("verified_email")
    val verifiedEmail: Boolean = true,
    @SerializedName("last_name")
    val lastName: String? = null
)