package com.example.yallabuy_user.data.models.settings

import com.google.gson.annotations.SerializedName

data class Address(
    @SerializedName("id")
    var id: Long = 0,
    @SerializedName("customer_id")
    var customerId: Long=0,
    val phone: String,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    //val company: String?,
    @field:SerializedName("address1")
    var fullAddress: String,

    var default: Boolean = false,

    val city: String,

    val country: String,

) : AddressDisplayInfo {
    //full address in a formatted string
    fun getAddressString(): String = "$firstName $lastName\n$fullAddress"

    override fun getRecipientFullName(): String = "$firstName $lastName"

    override fun getAddressLine(): String = "$fullAddress"

    override fun getDetailedDescription(): String = "Recipient Name: $firstName $lastName" +
            "\nRecipient Phone Number: $phone\n$fullAddress"

}

data class AddressBody(
    @SerializedName("address")
    var address: Address
)

data class AddressesResponse(
    @field:SerializedName("addresses")
    var addresses: List<Address>
)

data class NewAddressResponse(
    @field:SerializedName("customer_address")
    var address: Address
)

class DeleteResponse