package com.example.yallabuy_user.data.models

import com.google.gson.annotations.SerializedName

data class BrandResponse(
	@SerializedName("smart_collections")
	val smartCollections: List<SmartCollectionsItem?>? = null
)

data class SmartCollectionsItem(
	val image: Image? = null,
	val bodyHtml: String? = null,
	val handle: String? = null,
	val rules: List<RulesItem?>? = null,
	val title: String? = null,
	val publishedScope: String? = null,
	val templateSuffix: Any? = null,
	val updatedAt: String? = null,
	val disjunctive: Boolean? = null,
	val adminGraphqlApiId: String? = null,
	val id: Long? = null,
	val publishedAt: String? = null,
	val sortOrder: String? = null
)

data class RulesItem(
	val condition: String? = null,
	val column: String? = null,
	val relation: String? = null
)

data class Image(
	val src: String? = null,
	val alt: String? = null,
	val width: Int? = null,
	val createdAt: String? = null,
	val height: Int? = null
)

