package com.example.yallabuy_user.data.models

import com.google.gson.annotations.SerializedName


data class OrderDetailsResponse(
    val order: Order? = null
)

data class Order(
    @SerializedName("line_items")
    val lineItems: List<LineItemsItem?>? = null,
    val id: Long? = null,
    @SerializedName("current_total_price_set")
    val currentTotalPriceSet: CurrentTotalPriceSet? = null,
    val currency: String? = null,
    @SerializedName("shipping_address")
    val shippingAddress: ShippingAddress? = null,
    val email: String? = null,
    @SerializedName("total_price")
    val totalPrice: String? = null,

    )


data class CreateOrderRequest(
    @SerializedName("order")
    val order: CreateOrder
)

data class CreateOrder(
    @SerializedName("line_items")
    val lineItems: List<CreateLineItem>,
    @SerializedName("discount_codes")
    val discountCodes: List<DiscountCode>? = null,
    @SerializedName("shipping_address")
    val shippingAddress: CreateShippingAddress,
    val customer: CreateCustomer,
    val transactions: List<CreateTransaction>,
    @SerializedName("financial_status")
    val financialStatus: String,
    @SerializedName("fulfillment_status")
    val fulfillmentStatus: String,
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

