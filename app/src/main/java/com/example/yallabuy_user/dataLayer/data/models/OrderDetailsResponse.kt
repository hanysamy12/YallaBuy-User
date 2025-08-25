package com.example.yallabuy_user.dataLayer.data.models

import com.google.gson.annotations.SerializedName


data class OrderDetailsResponse(
    val order: com.example.yallabuy_user.dataLayer.data.models.Order? = null
)

data class Order(
    @SerializedName("line_items")
    val lineItems: List<com.example.yallabuy_user.dataLayer.data.models.LineItemsItem?>? = null,
    val id: Long? = null,
    @SerializedName("current_total_price_set")
    val currentTotalPriceSet: com.example.yallabuy_user.dataLayer.data.models.CurrentTotalPriceSet? = null,
    val currency: String? = null,
    @SerializedName("shipping_address")
    val shippingAddress: com.example.yallabuy_user.dataLayer.data.models.ShippingAddress? = null,
    val email: String? = null,
    @SerializedName("total_price")
    val totalPrice: String? = null,

    )


data class CreateOrderRequest(
    @SerializedName("order")
    val order: com.example.yallabuy_user.dataLayer.data.models.CreateOrder
)

data class CreateOrder(
    @SerializedName("line_items")
    val lineItems: List<com.example.yallabuy_user.dataLayer.data.models.CreateLineItem>,
    @SerializedName("discount_codes")
    val discountCodes: List<com.example.yallabuy_user.dataLayer.data.models.DiscountCode>? = null,
    @SerializedName("shipping_address")
    val shippingAddress: com.example.yallabuy_user.dataLayer.data.models.CreateShippingAddress,
    val customer: com.example.yallabuy_user.dataLayer.data.models.CreateCustomer,
    val transactions: List<com.example.yallabuy_user.dataLayer.data.models.CreateTransaction>,
    @SerializedName("financial_status")
    val financialStatus: String,
//    @SerializedName("fulfillment_status")
//    val fulfillmentStatus: String,
    @SerializedName("send_receipt")
    val sendReceipt: Boolean,
    @SerializedName("send_fulfillment_receipt")
    val sendFulfillmentReceipt: Boolean,
    //val currency: String
)

data class CreateLineItem(
    @SerializedName("variant_id")
    val variantId: Long,
    val quantity: Int
)

data class CreateShippingAddress(
    val id : Long,
//    @SerializedName("first_name")
//    val firstName: String,
//    @SerializedName("last_name")
//    val lastName: String,
//    val address1: String,
//    val city: String,
//    val country: String,
//    val phone: String
)

data class CreateCustomer(
    val id: Long,
   // val email: String
)

data class CreateTransaction(
    val kind: String,
    val status: String,
    val amount: String,
    val gateway: String
)

data class DiscountCode(
    val code: String
)

