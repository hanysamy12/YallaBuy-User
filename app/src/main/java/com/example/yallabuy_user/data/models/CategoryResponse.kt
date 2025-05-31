package com.example.yallabuy_user.data.models

import com.google.gson.annotations.SerializedName

data class CategoryResponse(
	@SerializedName("custom_collections")
	val customCollections: List<CustomCollectionsItem?>? = null
)

data class CustomCollectionsItem(
    val publishedScope: String? = null,
    val image: Image? = null,
    val bodyHtml: Any? = null,
    val templateSuffix: Any? = null,
    val updatedAt: String? = null,
    val adminGraphqlApiId: String? = null,
    val handle: String? = null,
    val id: Long? = null,
    val title: String? = null,
    val publishedAt: String? = null,
    val sortOrder: String? = null
)


