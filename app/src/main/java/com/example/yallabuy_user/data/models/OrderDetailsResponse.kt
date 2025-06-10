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


