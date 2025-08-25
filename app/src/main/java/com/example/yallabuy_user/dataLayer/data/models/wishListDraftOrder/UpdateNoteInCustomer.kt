package com.example.yallabuy_user.dataLayer.data.models.wishListDraftOrder



data class UpdateNoteInCustomer(
    val customer: com.example.yallabuy_user.dataLayer.data.models.wishListDraftOrder.CustomerNoteUpdate
)

data class CustomerNoteUpdate(
    val id: Long,
    val note: String
)