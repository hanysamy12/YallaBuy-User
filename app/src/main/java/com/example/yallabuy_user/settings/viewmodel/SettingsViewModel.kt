package com.mariammuhammad.yallabuy.ViewModel.Settings


import androidx.lifecycle.ViewModel
import com.example.yallabuy_user.R
import com.example.yallabuy_user.data.models.settings.SettingsItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel : ViewModel() {
    private val _settingsItems = MutableStateFlow<List<SettingsItem>>(emptyList())
    val settingsItems: StateFlow<List<SettingsItem>> = _settingsItems.asStateFlow()


     fun loadSettingsItems() {
        _settingsItems.value = listOf(
            SettingsItem(
                title = "Address",
                icon = R.drawable.location_on,
                onClick = {  println("Address clicked") }
            ),
            SettingsItem(
                title = "Currency",
                icon = R.drawable.currency_exchange,
                onClick = {
                    println("Currency clicked")
                }
            ),
            SettingsItem(
                title = "Contact us",
                icon = R.drawable.headset_mic,
                onClick = {
                    println("Contact us clicked")
                }
            ),
            SettingsItem(
                title = "About us",
                icon = R.drawable.info,
                onClick = {  println("About us clicked") }
            )
        )
    }

}
