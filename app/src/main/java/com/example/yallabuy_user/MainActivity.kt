package com.example.yallabuy_user

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.example.yallabuy_user.orders.OrderCheckoutScreen


import com.example.yallabuy_user.settings.viewmodel.CurrencyViewModel

import com.example.yallabuy_user.ui.navigation.MainScreen
import com.example.yallabuy_user.ui.theme.YallaBuyUserTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContent {
            YallaBuyUserTheme {
                //MainScreen()
                OrderCheckoutScreen(cartId = 1209127043390)
            //    CurrencyScreen(currencyViewModel = viewModel)
            }
        }
    }
}

