package com.example.yallabuy_user.data.models.createUser

data class Customer(
    val addresses: List<Any>,
    val admin_graphql_api_id: String,
    val created_at: String,
    val currency: String,
    val email: String,
    val email_marketing_consent: EmailMarketingConsent,
    val first_name: String,
    val id: Long,
    val last_name: String,
    val last_order_id: Any,
    val last_order_name: Any,
    val multipass_identifier: Any,
    val note: Any,
    val orders_count: Int,
    val phone: String,
    val sms_marketing_consent: SmsMarketingConsent,
    val state: String,
    val tags: String,
    val tax_exempt: Boolean,
    val tax_exemptions: List<Any>,
    val total_spent: String,
    val updated_at: String,
    val verified_email: Boolean
)