package com.example.yallabuy_user.dataLayer.data.models.productInfo

data class Product(
    val admin_graphql_api_id: String,
    val body_html: String,
    val created_at: String,
    val handle: String,
    val id: Long,
    val image: com.example.yallabuy_user.dataLayer.data.models.productInfo.Image,
    val images: List<com.example.yallabuy_user.dataLayer.data.models.productInfo.Image>,
    val options: List<com.example.yallabuy_user.dataLayer.data.models.productInfo.Option>,
    val product_type: String,
    val published_at: String,
    val published_scope: String,
    val status: String,
    val tags: String,
    val template_suffix: Any,
    val title: String,
    val updated_at: String,
    val variants: List<com.example.yallabuy_user.dataLayer.data.models.productInfo.Variant>,
    val vendor: String
)