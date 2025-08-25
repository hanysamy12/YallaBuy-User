package com.example.yallabuy_user.presentationLayer.wish

interface WishViewModelInterface {

    fun getAllProductFromWishList(wishListId : Long)
    fun deleteProductFromWishList(draftOrderId : Long , customerId : Long , title : String )
}