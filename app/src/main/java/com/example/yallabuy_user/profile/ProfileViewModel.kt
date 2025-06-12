package com.example.yallabuy_user.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.yallabuy_user.authentication.login.CustomerIdPreferences
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
        _logoutState.value = true
    }

}


data class ProfileUiState(
    val profileImageUrl: String = ""
)
