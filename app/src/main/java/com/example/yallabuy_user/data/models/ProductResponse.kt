package com.example.yallabuy_user.data.models

import com.google.gson.annotations.SerializedName

data class ProductResponse(
	val products: List<ProductsItem?>? = null
)

data class OptionsItem(
	val productId: Long? = null,
	val name: String? = null,
	val id: Long? = null,
	val position: Int? = null
)

data class ProductsItem(
	val image: ProductImage? = null,
	val bodyHtml: String? = null,
	val images: List<ImagesItem?>? = null,
	val createdAt: String? = null,
	val handle: String? = null,
	val title: String? = null,
	val tags: String? = null,
	val publishedScope: String? = null,
	@SerializedName("product_type")
	val productType: String? = null,
	val templateSuffix: Any? = null,
	val updatedAt: String? = null,
	val vendor: String? = null,
	val adminGraphqlApiId: String? = null,
	val options: List<OptionsItem?>? = null,
	val id: Long? = null,
	val publishedAt: String? = null,
	val status: String? = null
)

data class ImagesItem(
	val updatedAt: String? = null,
	val src: String? = null,
	val productId: Long? = null,
	val adminGraphqlApiId: String? = null,
	val alt: String? = null,
	val width: Int? = null,
	val createdAt: String? = null,
	val id: Long? = null,
	val position: Int? = null,
	val height: Int? = null
)

data class ProductImage(
	val updatedAt: String? = null,
	val src: String? = null,
	val productId: Long? = null,
	val adminGraphqlApiId: String? = null,
	val alt: String? = null,
	val width: Int? = null,
	val createdAt: String? = null,
	val id: Long? = null,
	val position: Int? = null,
	val height: Int? = null
)

