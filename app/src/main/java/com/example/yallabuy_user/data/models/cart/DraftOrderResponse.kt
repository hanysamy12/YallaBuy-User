package com.example.yallabuy_user.data.models.cart

import com.google.gson.annotations.SerializedName

data class DraftOrderBody(
    @SerializedName("draft_order")
    val draftOrder: DraftOrder
)
data class DraftOrderResponse(
    @SerializedName("draft_orders")
    val draftOrders: List<DraftOrder>
)

data class DraftOrder(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("line_items")
    var lineItems: MutableList<LineItem>,

    @SerializedName("tags")
    val tags: String? = null,

    val customer: Customer? = null
)

data class LineItem(
    @SerializedName("variant_id")
    var variantID: Long,

    @SerializedName("product_id")
    var productID: Long,

    @SerializedName("title")
    val title: String,

    @SerializedName("quantity")
    var quantity: Int,

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

data class Customer(
    val id: Long
)