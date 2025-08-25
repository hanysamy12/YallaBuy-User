package com.example.yallabuy_user.presentationLayer.orders

import androidx.compose.runtime.staticCompositionLocalOf


val LocalOrdersViewModel = staticCompositionLocalOf<NewOrderViewModel> {
    error("orders view model did not provided ")

}