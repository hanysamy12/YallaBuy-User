package com.example.yallabuy_user.data.models.Coupon

import com.google.gson.annotations.SerializedName

//start data //end date //usageCount
data class PriceRule(
    @SerializedName("id")
    val id: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("value_type")
    val valueType: String, // "fixed_amount" or "percentage"
    @SerializedName("value")
    val value: String, // "-20.0"
    @SerializedName("usage_limit")
    val usageLimit: Int?,  //?
    @SerializedName("starts_at")
    val startsAt: String,
    @SerializedName("ends_at")
    val endsAt: String?,
)

data class PriceRulesResponse(
    val price_rules: List<PriceRule>
)

data class DiscountCodeCoupon(
    @SerializedName("id")
    val id: Long,
    @SerializedName("price_rule_id")
    val priceRuleId: Long,
    @SerializedName("code")
    val code: String,
    @SerializedName("usage_count")
    val usageCount: Long, //?
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String?,
)

data class DiscountCodesResponse(
    @SerializedName("discount_codes")
    val discountCodes: List<DiscountCodeCoupon>
)

data class CouponItem(
    val imageResId: Int,
    val code: String //code like "SALE20"
)

data class CouponValidationResult(
    val isValid: Boolean,
    val message: String,
    val discountValue: Double = 0.0,
    val valueType: String = "" // "fixed_amount" or "percentage"
)



//val coupons = listOf(
//    Coupon("ZIAD40", "SUMMER10", "fixed_amount", -22.0, "2025-06-02", "2025-06-06", "https://example.com/image1.jpg"),
//    Coupon("ZIAD30", "ZIAD30", "percentage", -30.0, "2025-06-06", "2025-06-11", "https://example.com/image2.jpg")
//)