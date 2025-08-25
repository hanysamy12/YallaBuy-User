package com.mariammuhammad.yallabuy.ViewModel.Settings

import androidx.lifecycle.ViewModel
import com.example.yallabuy_user.dataLayer.data.models.settings.ContactUs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ContactUsViewModel : ViewModel() {

    private val _contacts = MutableStateFlow<List<com.example.yallabuy_user.dataLayer.data.models.settings.ContactUs>>(emptyList())
    val contacts: StateFlow<List<com.example.yallabuy_user.dataLayer.data.models.settings.ContactUs>> = _contacts.asStateFlow()

    init {
        loadContacts()
    }

    private fun loadContacts() {
        _contacts.value = listOf(
            com.example.yallabuy_user.dataLayer.data.models.settings.ContactUs(
                name = "Hany Samy",
                phone = "01226902530",
                email = "hanysamy111@outlook.com"
            ),
            com.example.yallabuy_user.dataLayer.data.models.settings.ContactUs(
                name = "Moaz Mamdouh",
                phone = "01095030319",
                email = "moaz.mamdoouh@gmail.com"
            ),
            com.example.yallabuy_user.dataLayer.data.models.settings.ContactUs(
                name = "Ziad Elshemy",
                phone = "01067058501",
                email = "ziadmohamedelshemy@gmail.com"
            ),
            com.example.yallabuy_user.dataLayer.data.models.settings.ContactUs(
                name = "Mariam Muhammad",
                phone = "01123456789",
                email = "mariammuhammad911@gmail.com"
            )
        )
    }
}
