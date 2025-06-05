package com.example.yallabuy_user

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.lifecycle.ViewModelProvider
import com.example.yallabuy_user.settings.model.remote.CurrencyPreferenceManager
import com.example.yallabuy_user.settings.model.repository.CurrencyRepository
import com.example.yallabuy_user.settings.model.repository.FakeCurrencyRepository
import com.example.yallabuy_user.settings.view.CurrencyScreen

import com.example.yallabuy_user.settings.viewmodel.CurrencyViewModel


import com.example.yallabuy_user.settings.view.CurrencyScreen

import com.example.yallabuy_user.ui.navigation.MainScreen
import com.example.yallabuy_user.ui.theme.YallaBuyUserTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        // Fake CurrencyPreferenceManager with simple in-memory state
        val fakePreferenceManager = object : CurrencyPreferenceManager {
            private val currencyFlow = MutableStateFlow("USD")

            override suspend fun getPreferredCurrency(): String = currencyFlow.value

            override suspend fun setPreferredCurrency(currencyCode: String) {
                currencyFlow.value = currencyCode
            }

            override val preferredCurrencyFlow: Flow<String> = currencyFlow
        }

        // Create ViewModel manually using the fake manager
        val fakeViewModel = CurrencyViewModel(fakePreferenceManager)



        setContent {
            YallaBuyUserTheme {
                //MainScreen()

              //  CurrencyScreen(currencyViewModel = viewModel)
                CurrencyScreen(viewModel = fakeViewModel)



            }
        }
    }
}

