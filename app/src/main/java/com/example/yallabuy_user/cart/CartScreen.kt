package com.example.yallabuy_user.cart

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.yallabuy_user.home.HomeViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun CartScreen(navController: NavController, homeViewModel: HomeViewModel = koinViewModel()) {
    Text("Cart")

}
