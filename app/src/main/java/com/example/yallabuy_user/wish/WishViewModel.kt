package com.example.yallabuy_user.wish

import DraftCustomer
import DraftOrder
import DraftOrderLineItem
import WishListDraftOrderRequest
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yallabuy_user.data.models.wishListDraftOrder.CustomerNoteUpdate
import com.example.yallabuy_user.data.models.wishListDraftOrder.UpdateNoteInCustomer
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

    private val _resetWishListSharedPreference = MutableStateFlow(false)
    val resetWishListSharedPreference = _resetWishListSharedPreference.asStateFlow()

    private  var deletedList : List<DraftOrderLineItem> = emptyList()

    override fun getAllProductFromWishList(wishListId: Long) {
        Log.i("wishList", "getAllProductFromWishList function called ")
        viewModelScope.launch {
            try {
                if(wishListId != 0L) {
                    Log.i("wishList", "getAllProductFromWishList not empty" )
                    val response = repositoryInterface.getWishListDraftById(wishListId)
                    response.collect { wishListResponse ->
                        _allWishListProduct.emit(ApiResponse.Success(wishListResponse.draft_order.line_items))
                        deletedList = wishListResponse.draft_order.line_items
                    }
                }else{
                   _allWishListProduct.emit(ApiResponse.Success(emptyList()))
                }
            }catch (e : Exception){
                Log.i("wishList", "getAllProductFromWishList in viewmodel error is ${e.message} ")
            }
        }
    }

    override fun deleteProductFromWishList(draftOrderId : Long , customerId : Long , title : String ) {
        viewModelScope.launch {
            try {
               repositoryInterface.getWishListDraftById(draftOrderId).collect{ response ->
                  if(response.draft_order.line_items.size == 1){
                      Log.i("wishList", "deleteProductFromWishList list ${deletedList.size} ")
                      repositoryInterface.deleteDraftOrderCart(draftOrderId)
                      repositoryInterface.updateNoteInCustomer(customerId , UpdateNoteInCustomer(
                          CustomerNoteUpdate(customerId , "")
                      ))
                      _resetWishListSharedPreference.emit(true)
                      _allWishListProduct.emit(ApiResponse.Success(emptyList()))
                  }else {
                      val product = response.draft_order.line_items
                          .filter {
                              it.title.equals(title)
                          }
                      val mutableDeletedList =  response.draft_order.line_items.toMutableList()
                      mutableDeletedList.remove(product[0])
                      val request =  WishListDraftOrderRequest(
                          DraftOrder(
                              line_items = mutableDeletedList.toList() ,
                              customer = DraftCustomer(customerId)
                          )
                      )
                      repositoryInterface.updateDraftOrder(draftOrderId ,request  ).collect{draftOrder ->
                          _allWishListProduct.emit(ApiResponse.Success(draftOrder.draft_order.line_items))
                          _resetWishListSharedPreference.emit(false)
                      }
                  }
               }
            }catch (e : Exception){
                Log.e("wishList", "deleteProductFromWishList failed", e)
                Log.i("wishList", "Exception type: ${e::class.java.simpleName}")
                Log.i("wishList", "LocalizedMessage: ${e.localizedMessage}")            }
        }
    }
}
