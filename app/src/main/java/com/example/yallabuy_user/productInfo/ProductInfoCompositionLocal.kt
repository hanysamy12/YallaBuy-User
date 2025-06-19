package com.example.yallabuy_user.productInfo

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavController


val LocalProductInfoViewModel
= staticCompositionLocalOf<ProductInfoViewModel> {
    error("No ProductInfoViewModel provided")
}

val LocalNavController = staticCompositionLocalOf<NavController> {
    error("No nav controller found")
}

