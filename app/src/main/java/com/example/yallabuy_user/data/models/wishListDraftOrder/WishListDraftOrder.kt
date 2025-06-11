data class WishListDraftOrderRequest(
    val draft_order: DraftOrder
)

data class DraftOrder(
    val line_items: List<DraftOrderLineItem>,
    val customer: DraftCustomer? = null,
)

data class DraftOrderLineItem(
    val title: String? = null,
    val price: String? = null,
    val variant_id: Long? = null,
    val quantity: Int,
    val properties: List<LineItemProperty>? = null
)

data class LineItemProperty(
    val name: String,
    val value: String
)

data class DraftCustomer(
    val id: Long
)
