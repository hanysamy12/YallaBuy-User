package com.example.yallabuy_user.data.models.productInfo

data class Image(
    val admin_graphql_api_id: String,
    val alt: String,
    val created_at: String,
    val height: Int,
    val id: Long,
    val position: Int,
    val product_id: Long,
    var src: String,
    val updated_at: String,
    val variant_ids: List<Any>,
    val width: Int
)