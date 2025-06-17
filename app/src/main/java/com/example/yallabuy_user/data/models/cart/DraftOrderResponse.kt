package com.example.yallabuy_user.data.models.cart

import com.google.gson.annotations.SerializedName

data class DraftOrderBody(
    @SerializedName("draft_order")
    val draftOrderCart: DraftOrderCart
)
data class DraftOrderResponse(
    @SerializedName("draft_orders")
    val draftOrderCarts: List<DraftOrderCart>
)

data class DraftOrderCart(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("line_items")
    var lineItems: MutableList<LineItem>,

    @SerializedName("tags")
    val tags: String? = null,

    val customer: Customer? = null,
    @SerializedName("total_price")
    val totalPrice: String? = null,
    val currency: String? = null,

)

data class LineItem(
    @SerializedName("variant_id")
    var variantID: Long,  //check quantity of the variant ID if we can increase or not


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

data class UpdateCustomerBody(
    @SerializedName("customer")
    val customer: CustomerTagUpdate
)

data class CustomerTagUpdate(
    val id: Long,
    val tags: String
)

data class CreateCustomerCart(
    @SerializedName("customer")
    val customer: CustomerTagUpdate
)

data class ProductVariant(
    @SerializedName("variant")
    val variant: VariantDetail
)

data class VariantDetail(
    @SerializedName("id")
    val id: Long,
    @SerializedName("inventory_quantity")
    val inventoryQuantity: Int
)