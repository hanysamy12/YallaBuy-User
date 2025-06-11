package com.example.yallabuy_user.data.models.wishListDraftOrder.response

import DraftOrderLineItem

data class DraftOrder(
    val admin_graphql_api_id: String,
    val allow_discount_codes_in_checkout: Boolean,
    val api_client_id: Long,
    val applied_discount: Any,
    //val b2b?: Boolean,
    val billing_address: Any,
    val completed_at: Any,
    val created_at: String,
    val created_on_api_version_handle: String,
    val currency: String,
    val customer: Customer,
    val email: String,
    val id: Long,
    val invoice_sent_at: Any,
    val invoice_url: String,
    val line_items: List<DraftOrderLineItem>,
    val name: String,
    val note: Any,
    val note_attributes: List<Any>,
    val order_id: Any,
    val payment_terms: Any,
    val shipping_address: Any,
    val shipping_line: Any,
    val status: String,
    val subtotal_price: String,
    val tags: String,
    val tax_exempt: Boolean,
    val tax_lines: List<Any>,
    val taxes_included: Boolean,
    val total_price: String,
    val total_tax: String,
    val updated_at: String
)