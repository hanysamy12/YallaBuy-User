package com.example.yallabuy_user.data.models.wishListDraftOrder



data class UpdateNoteInCustomer(
    val customer: CustomerNoteUpdate
)

data class CustomerNoteUpdate(
    val id: Long,
    val note: String
)