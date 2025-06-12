package com.example.yallabuy_user.data.models.settings

import androidx.annotation.DrawableRes

data class SettingsItem(
    val title: String,
    @DrawableRes val icon: Int,
    val onClick: () -> Unit
)
