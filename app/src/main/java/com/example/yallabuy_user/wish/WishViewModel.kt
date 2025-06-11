package com.example.yallabuy_user.wish

import DraftCustomer
import DraftOrder
import DraftOrderLineItem
import WishListDraftOrderRequest
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yallabuy_user.repo.RepositoryInterface
import com.example.yallabuy_user.utilities.ApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WishViewModel(
    private val repositoryInterface: RepositoryInterface
) : ViewModel(), WishViewModelInterface {

    private val _allWishListProduct : MutableStateFlow<ApiResponse<List<DraftOrderLineItem>>> =
        MutableStateFlow(ApiResponse.Loading)
    val allWishListProduct = _allWishListProduct.asStateFlow()

    private  var deletedList : List<DraftOrderLineItem> = emptyList()
    override fun getAllProductFromWishList(wishListId: Long) {
        viewModelScope.launch {
            try {
                val response = repositoryInterface.getWishListDraftById(wishListId)
                response.collect{ wishListResponse ->
                    _allWishListProduct.emit(ApiResponse.Success(wishListResponse.draft_order.line_items))
                    deletedList = wishListResponse.draft_order.line_items
                }
            }catch (e : Exception){
                Log.i("TAG", "getAllProductFromWishList in viewmodel error is ${e.message} ")
            }
        }
    }

    override fun deleteProductFromWishList(draftOrderId : Long , customerId : Long , title : String ) {
        viewModelScope.launch {
            try {
               val mutableDeletedList = deletedList.toMutableList()
               for (product in mutableDeletedList ){
                   Log.i("TAG", "deleteProductFromWishList product title ${product.title} ")
                   if(product.title.equals(title)){
                       Log.i("TAG", "deleteProductFromWishList product title ${product.title} ")
                       mutableDeletedList.remove(product)
                   }
               }
               val request =  WishListDraftOrderRequest(
                    DraftOrder(
                        line_items = mutableDeletedList.toList() ,
                        customer = DraftCustomer(customerId)
                    )
                )
                 repositoryInterface.updateDraftOrder(draftOrderId ,request  )
                _allWishListProduct.emit(ApiResponse.Success(request.draft_order.line_items))
            }catch (e : Exception){
                Log.i("TAG", "deleteProductFromWishList error in view model ${e.message} ")
            }
        }
    }
}
