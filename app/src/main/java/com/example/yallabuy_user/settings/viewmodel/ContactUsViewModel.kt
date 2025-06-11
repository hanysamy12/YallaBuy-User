package com.mariammuhammad.yallabuy.ViewModel.Settings

import androidx.lifecycle.ViewModel
import com.example.yallabuy_user.data.models.settings.ContactUs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ContactUsViewModel : ViewModel() {

    private val _contacts = MutableStateFlow<List<ContactUs>>(emptyList())
    val contacts: StateFlow<List<ContactUs>> = _contacts.asStateFlow()

    init {
        loadContacts()
    }

    private fun loadContacts() {
        _contacts.value = listOf(
            ContactUs(
                name = "Hany Samy",
                phone = "01226902530",
                email = "hanysamy111@outlook.com"
            ),
            ContactUs(
                name = "Moaz Mamdouh",
                phone = "01095030319",
                email = "moaz.mamdoouh@gmail.com"
            ),
            ContactUs(
                name = "Ziad Elshemy",
                phone = "01067058501",
                email = "ziadmohamedelshemy@gmail.com"
            ),
            ContactUs(
                name = "Mariam Muhammad",
                phone = "01123456789",
                email = "mariammuhammad911@gmail.com"
            )
        )
    }
}
