package com.example.yallabuy_user.Settings

import com.mariammuhammad.yallabuy.ViewModel.Settings.ContactUsViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import kotlin.test.Test

class ContactUsViewModelTest {

    private lateinit var viewModel: ContactUsViewModel

    @Before
    fun setup() {
        viewModel = ContactUsViewModel()
    }

    @Test
    fun `contacts should be initialized with 4 entries`() = runTest {
        val contacts = viewModel.contacts.value

        assertEquals(4, contacts.size)
        assertTrue(contacts.any { it.name == "Mariam Muhammad" })
        assertEquals("01123456789", contacts.find { it.name == "Mariam Muhammad" }?.phone)
        assertEquals("mariammuhammad911@gmail.com", contacts.find { it.name == "Mariam Muhammad" }?.email)
    }
}