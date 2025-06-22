package com.example.yallabuy_user.profile

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.yallabuy_user.authentication.login.CustomerIdPreferences
import com.example.yallabuy_user.cart.viewmodel.CartSharedPreference
import com.example.yallabuy_user.wish.WishListIdPref
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel() : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    private val _logoutState = MutableStateFlow(false)
    val logoutState: StateFlow<Boolean> = _logoutState



    init {
        loadProfile()
    }

    private fun loadProfile() {
        _uiState.value = ProfileUiState(
        )
    }

    fun logout(context: Context) {
        CustomerIdPreferences.saveCustomerID(context, 0L)
        WishListIdPref.saveWishListID(context,0L)
        Log.i("checkingWishList", "Logout saving shared preference  ")
        CartSharedPreference.saveCartID(context,0L)
        _logoutState.value = true
    }

    fun getUserName(context: Context) :String?
    {
        return CustomerIdPreferences.getCustomerName(context)
    }
}


data class ProfileUiState(
    val profileImageUrl: String = ""
)
