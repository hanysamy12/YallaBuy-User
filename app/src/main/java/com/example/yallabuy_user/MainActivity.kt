package com.example.yallabuy_user

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.yallabuy_user.settings.model.repository.CurrencyRepository


import com.example.yallabuy_user.settings.viewmodel.CurrencyViewModel

import com.example.yallabuy_user.ui.navigation.MainScreen
import com.example.yallabuy_user.ui.theme.YallaBuyUserTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()



        setContent {
            YallaBuyUserTheme {
                MainScreen()
            //    CurrencyScreen(currencyViewModel = viewModel)
            }
        }
    }
}

