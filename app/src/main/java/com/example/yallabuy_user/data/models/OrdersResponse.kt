package com.example.yallabuy_user.data.models

import com.google.gson.annotations.SerializedName

data class OrdersResponse(
	val orders: List<OrdersItem?>? = null
)

data class PriceSet(
	val shopMoney: ShopMoney? = null,
	val presentmentMoney: PresentmentMoney? = null
)

data class ShippingAddress(
	val zip: Any? = null,
	val country: String? = null,
	val city: String? = null,
	val address2: Any? = null,
	val address1: String? = null,
	val latitude: Any? = null,
	val lastName: String? = null,
	val provinceCode: Any? = null,
	val countryCode: String? = null,
	val province: Any? = null,
	val phone: Any? = null,
	val name: String? = null,
	val company: Any? = null,
	val firstName: String? = null,
	val longitude: Any? = null
)

data class ShopMoney(
	val amount: String? = null,
	val currencyCode: String? = null
)

data class TotalCashRoundingPaymentAdjustmentSet(
	val shopMoney: ShopMoney? = null,
	val presentmentMoney: PresentmentMoney? = null
)

data class TotalDiscountSet(
	val shopMoney: ShopMoney? = null,
	val presentmentMoney: PresentmentMoney? = null
)

data class CurrentSubtotalPriceSet(
	val shopMoney: ShopMoney? = null,
	val presentmentMoney: PresentmentMoney? = null
)

data class SubtotalPriceSet(
	val shopMoney: ShopMoney? = null,
	val presentmentMoney: PresentmentMoney? = null
)

data class EmailMarketingConsent(
	val consentUpdatedAt: Any? = null,
	val state: String? = null,
	val optInLevel: String? = null
)

data class DefaultAddress(
	val zip: Any? = null,
	val country: String? = null,
	val address2: Any? = null,
	val city: String? = null,
	val address1: String? = null,
	val lastName: String? = null,
	val provinceCode: Any? = null,
	val countryCode: String? = null,
	val jsonMemberDefault: Boolean? = null,
	val province: Any? = null,
	val phone: Any? = null,
	val name: String? = null,
	val countryName: String? = null,
	val company: Any? = null,
	val id: Long? = null,
	val customerId: Long? = null,
	val firstName: String? = null
)

data class TotalTaxSet(
	val shopMoney: ShopMoney? = null,
	val presentmentMoney: PresentmentMoney? = null
)

data class TotalDiscountsSet(
	val shopMoney: ShopMoney? = null,
	val presentmentMoney: PresentmentMoney? = null
)

data class CurrentTotalPriceSet(
	val shopMoney: ShopMoney? = null,
	val presentmentMoney: PresentmentMoney? = null
)

data class CurrentTotalTaxSet(
	val shopMoney: ShopMoney? = null,
	val presentmentMoney: PresentmentMoney? = null
)

data class TotalCashRoundingRefundAdjustmentSet(
	val shopMoney: ShopMoney? = null,
	val presentmentMoney: PresentmentMoney? = null
)

data class TotalPriceSet(
	val shopMoney: ShopMoney? = null,
	val presentmentMoney: PresentmentMoney? = null
)

data class CurrentTotalDiscountsSet(
	val shopMoney: ShopMoney? = null,
	val presentmentMoney: PresentmentMoney? = null
)

data class PresentmentMoney(
	val amount: String? = null,
	val currencyCode: String? = null
)

data class Customer(
	val note: String? = null,
	val taxExempt: Boolean? = null,
	val emailMarketingConsent: EmailMarketingConsent? = null,
	@SerializedName("created_at")
	val createdAt: String? = null,
	@SerializedName("last_name")
	val lastName: String? = null,
	val multipassIdentifier: Any? = null,
	val verifiedEmail: Boolean? = null,
	val tags: String? = null,
	val smsMarketingConsent: Any? = null,
	val defaultAddress: DefaultAddress? = null,
	val updatedAt: String? = null,
	val phone: Any? = null,
	val adminGraphqlApiId: String? = null,
	val taxExemptions: List<Any?>? = null,
	val currency: String? = null,
	val id: Long? = null,
	val state: String? = null,
	@SerializedName("first_name")
	val firstName: String? = null,
	val email: String? = null
)

data class OrdersItem(
	val cancelledAt: Any? = null,
	val confirmationNumber: String? = null,
	val totalCashRoundingRefundAdjustmentSet: TotalCashRoundingRefundAdjustmentSet? = null,
	val fulfillmentStatus: Any? = null,
	val originalTotalAdditionalFeesSet: Any? = null,
	val currentTotalDiscountsSet: CurrentTotalDiscountsSet? = null,
	val billingAddress: Any? = null,
	val lineItems: List<LineItemsItem?>? = null,
	val originalTotalDutiesSet: Any? = null,
	val presentmentCurrency: String? = null,
	val totalDiscountsSet: TotalDiscountsSet? = null,
	val totalCashRoundingPaymentAdjustmentSet: TotalCashRoundingPaymentAdjustmentSet? = null,
	val locationId: Any? = null,
	val sourceUrl: Any? = null,
	val landingSite: Any? = null,
	val sourceIdentifier: Any? = null,
	val reference: Any? = null,
	val merchantBusinessEntityId: String? = null,
	val number: Int? = null,
	@SerializedName("created_at")
	val checkoutId: Any? = null,
	@SerializedName("checkout_token")
	val checkoutToken: Any? = null,
	val taxLines: List<Any?>? = null,
	val currentTotalDiscounts: String? = null,
	val merchantOfRecordAppId: Any? = null,
	val customerLocale: Any? = null,
	val currentTotalAdditionalFeesSet: Any? = null,
	val id: Long? = null,
	@SerializedName("app_id")
	val appId: Long? = null,
	@SerializedName("current_subtotal_price")
	val subtotalPrice: String? = null,
	@SerializedName("closed_at")
	val closedAt: Any? = null,
	val orderStatusUrl: String? = null,
	val currentTotalPriceSet: CurrentTotalPriceSet? = null,
	val deviceId: Any? = null,
	val test: Boolean? = null,
	val totalShippingPriceSet: TotalShippingPriceSet? = null,
	@SerializedName("current_subtotal_price_set")
	val subtotalPriceSet: SubtotalPriceSet? = null,
	val taxExempt: Boolean? = null,
	val paymentGatewayNames: List<String?>? = null,
	val totalTax: String? = null,
	val tags: String? = null,
	val currentSubtotalPriceSet: CurrentSubtotalPriceSet? = null,
	val currentTotalTax: String? = null,
	val shippingLines: List<Any?>? = null,
	val phone: Any? = null,
	val userId: Any? = null,
	val noteAttributes: List<Any?>? = null,
	val name: String? = null,
	val cartToken: Any? = null,
	val totalTaxSet: TotalTaxSet? = null,
	val landingSiteRef: Any? = null,
	val discountCodes: List<Any?>? = null,
	val estimatedTaxes: Boolean? = null,
	val note: Any? = null,
	val currentSubtotalPrice: String? = null,
	val currentTotalTaxSet: CurrentTotalTaxSet? = null,
	val totalOutstanding: String? = null,
	val orderNumber: Int? = null,
	val discountApplications: List<Any?>? = null,
	val createdAt: String? = null,
	val totalLineItemsPriceSet: TotalLineItemsPriceSet? = null,
	val taxesIncluded: Boolean? = null,
	val buyerAcceptsMarketing: Boolean? = null,
	val paymentTerms: Any? = null,
	val confirmed: Boolean? = null,
	val totalWeight: Int? = null,
	@SerializedName("contact_email")
	val contactEmail: String? = null,
	val refunds: List<Any?>? = null,
	val totalDiscounts: String? = null,
	val fulfillments: List<Any?>? = null,
	val clientDetails: Any? = null,
	val poNumber: Any? = null,
	val referringSite: Any? = null,
	val updatedAt: String? = null,
	val processedAt: String? = null,
	val company: Any? = null,
	val currency: String? = null,
	val shippingAddress: ShippingAddress? = null,
	val browserIp: Any? = null,
	val email: String? = null,
	val sourceName: String? = null,
	val totalPriceSet: TotalPriceSet? = null,
	val currentTotalDutiesSet: Any? = null,
	val totalPrice: String? = null,
	val totalLineItemsPrice: String? = null,
	val dutiesIncluded: Boolean? = null,
	val totalTipReceived: String? = null,
	val token: String? = null,
	val cancelReason: Any? = null,
	val currentTotalPrice: String? = null,
	val adminGraphqlApiId: String? = null,
	val financialStatus: String? = null,
	val customer: Customer? = null
)

data class TotalLineItemsPriceSet(
	val shopMoney: ShopMoney? = null,
	val presentmentMoney: PresentmentMoney? = null
)

data class LineItemsItem(
	val variantTitle: String? = null,
	val fulfillmentStatus: Any? = null,
	val totalDiscount: String? = null,
	val giftCard: Boolean? = null,
	val requiresShipping: Boolean? = null,
	val totalDiscountSet: TotalDiscountSet? = null,
	val title: String? = null,
	val attributedStaffs: List<Any?>? = null,
	val productExists: Boolean? = null,
	val variantId: Long? = null,
	val taxLines: List<Any?>? = null,
	val price: String? = null,
	val vendor: String? = null,
	val productId: Long? = null,
	val id: Long? = null,
	val grams: Int? = null,
	val sku: String? = null,
	val fulfillableQuantity: Int? = null,
	val quantity: Int? = null,
	val fulfillmentService: String? = null,
	val taxable: Boolean? = null,
	val variantInventoryManagement: String? = null,
	val discountAllocations: List<Any?>? = null,
	val currentQuantity: Int? = null,
	val adminGraphqlApiId: String? = null,
	val name: String? = null,
	val priceSet: PriceSet? = null,
	val properties: List<Any?>? = null,
	val duties: List<Any?>? = null
)

data class TotalShippingPriceSet(
	val shopMoney: ShopMoney? = null,
	val presentmentMoney: PresentmentMoney? = null
)

