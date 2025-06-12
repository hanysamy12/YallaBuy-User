package com.example.yallabuy_user.data.models.cart

import com.google.gson.annotations.SerializedName


data class DraftOrderBody(
    @SerializedName("draft_order")
    val draftOrder: DraftOrder
)

data class LineItem(
    @SerializedName("variant_id")
    var variantID: Long,
    @SerializedName("product_id")
    var productID: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("quantity")
    var quantity: Long,
    @SerializedName("price")
    val price: String,
    val properties: List<Property>

) {
    fun getTotalPrice(): String = (price.toDouble() * quantity).toString()
}

data class Property(
    val name: String,
    val value: String
)

data class DraftOrder(
    @SerializedName("id")
    val Id: Long,
    @SerializedName("note")
    val note: String,
    @SerializedName("line_items")
    var lineItems: MutableList<LineItem>,
    @SerializedName("total_price")
    val totalPrice: String,
    val customer: Customer,
    val currency: String

)

data class Customer(
    val id: Long
)