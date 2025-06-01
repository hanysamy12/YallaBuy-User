package com.example.yallabuy_user.wish

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.yallabuy_user.home.HomeViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun WishScreen(navController: NavController, homeViewModel: HomeViewModel = koinViewModel()) {
    Text("Wish")

}
