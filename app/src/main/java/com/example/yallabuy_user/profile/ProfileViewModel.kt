package com.example.yallabuy_user.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel() : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadProfile()
    }

    private fun loadProfile() {
        _uiState.value = ProfileUiState(
        )
    }

    fun logout() {

    }
}

data class ProfileUiState(
    val profileImageUrl: String = ""
)
