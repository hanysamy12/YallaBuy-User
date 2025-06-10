package com.example.yallabuy_user.data.models

import com.google.gson.annotations.SerializedName

data class OrdersResponse(
	val orders: List<OrdersItem?>? = null
)


data class ShippingAddress(
	val country: String? = null,
	val city: String? = null,
	val address2: String? = null,
	val address1: String? = null,
	val lastName: String? = null,
	val province: Any? = null,
	val phone: Any? = null,
	val name: String? = null,
	val firstName: String? = null,

)

data class ShopMoney(
	val amount: String? = null,
	@SerializedName("currency_code")
	val currencyCode: String? = null
)



data class CurrentTotalPriceSet(
	val shopMoney: ShopMoney? = null,
	val presentmentMoney: PresentmentMoney? = null
)
data class TotalPriceSet(
	val shopMoney: ShopMoney? = null,
	val presentmentMoney: PresentmentMoney? = null
)
data class PresentmentMoney(
	val amount: String? = null,
	val currencyCode: String? = null
)


data class OrdersItem(
	@SerializedName("line_items")
	val lineItems: List<LineItemsItem?>? = null,
	val id: Long? = null,
	@SerializedName("app_id")
	val appId: Long? = null,
	@SerializedName("current_subtotal_price")
	val subtotalPrice: String? = null,
	@SerializedName("closed_at")
	val closedAt: Any? = null,
	val discountCodes: List<Any?>? = null,
	val orderNumber: Int? = null,
	@SerializedName("created_at")
	val createdAt: String? = null,
	val confirmed: Boolean? = null,
	@SerializedName("contact_email")
	val contactEmail: String? = null,
	val company: Any? = null,
	val currency: String? = null,
	@SerializedName("current_total_price")
	val currentTotalPrice: String? = null,

)

data class LineItemsItem(
	val variantTitle: String? = null,
	val title: String? = null,
	val price: String? = null,
	@SerializedName("product_id")
	val productId: Long? = null,
	val name: String? = null,
	val imgUrl : String? = null,
	val currency: String? = null

	)


