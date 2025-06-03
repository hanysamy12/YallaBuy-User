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
	val variants: List<VariantsItem?>? = null,
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

data class VariantsItem(
	val inventoryManagement: String? = null,
	val requiresShipping: Boolean? = null,
	val oldInventoryQuantity: Int? = null,
	val createdAt: String? = null,
	val title: String? = null,
	val updatedAt: String? = null,
	val inventoryItemId: Long? = null,
	val price: String? = null,
	val productId: Long? = null,
	val option3: Any? = null,
	val option1: String? = null,
	val id: Long? = null,
	val option2: String? = null,
	val grams: Int? = null,
	val sku: String? = null,
	val barcode: Any? = null,
	val inventoryQuantity: Int? = null,
	val compareAtPrice: Any? = null,
	val taxable: Boolean? = null,
	val fulfillmentService: String? = null,
	val weight: Any? = null,
	val inventoryPolicy: String? = null,
	val weightUnit: String? = null,
	val adminGraphqlApiId: String? = null,
	val position: Int? = null,
	val imageId: Any? = null
)
