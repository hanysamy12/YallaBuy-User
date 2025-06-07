@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.yallabuy_user.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.yallabuy_user.R
import com.example.yallabuy_user.home.HomeViewModel
import com.example.yallabuy_user.settings.model.local.SettingsItem
import com.example.yallabuy_user.ui.navigation.ScreenRoute
import com.mariammuhammad.yallabuy.View.Settings.SettingsListItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = koinViewModel()
) {
    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text("My Account", color = Color.White, fontWeight = FontWeight.Bold)
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = colorResource(R.color.dark_blue)
//                )
//            )
//        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color.White)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Image(
                painter = painterResource(id = R.drawable.person_pin_circle),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(32.dp))

            SettingsListItem(
                item = SettingsItem(
                    title = "Settings",
                    icon = R.drawable.headset_mic,  // Replace with actual icon
                    onClick = {
                        navController.navigate(ScreenRoute.Settings.route)
                    }
                )
            )

            Divider(
                color = Color.LightGray,
                thickness = 0.5.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            SettingsListItem(
                item = SettingsItem(
                    title = "Logout",
                    icon = R.drawable.location_on,  // I will replace with actual icon
                    onClick = {
                        viewModel.logout()
                        navController.navigate(ScreenRoute.Home.route) {
                            popUpTo(ScreenRoute.Profile.route) { inclusive = true }
                        }
                    }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
//
//@Composable
//fun ProfileScreen(
//    navController: NavController,
//    viewModel: ProfileViewModel = koinViewModel()
//) {
//    Column(
//        modifier = Modifier
//            //.fillMaxSize()
//            .padding(16.dp)
//            .verticalScroll(rememberScrollState()),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Spacer(modifier = Modifier.height(32.dp))
//
//        Image(
//            painter = painterResource(id = R.drawable.person_pin_circle),
//            contentDescription = "Profile Picture",
//            modifier = Modifier
//                .size(120.dp)
//                .clip(CircleShape)
//                .background(colorResource(R.color.white)),
//            contentScale = ContentScale.Crop
//        )
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        SettingsItem(title = "Settings") {
//            navController.navigate(ScreenRoute.Settings.route)
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Button(
//            onClick = {
//                viewModel.logout()
//                navController.navigate(ScreenRoute.Home.route) {
//                    popUpTo(ScreenRoute.Profile.route) { inclusive = true }
//                }
//            },
//            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.dark_blue))
//        ) {
//            Text("Logout", color = Color.White)
//        }
//    }
//}
//
//@Composable
//fun SettingsItem(title: String, onClick: () -> Unit) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 8.dp)
//            .clickable(onClick = onClick),
//        elevation = CardDefaults.cardElevation(4.dp)
//    ) {
//        Row(modifier = Modifier.padding(16.dp)) {
//            Text(text = title, style = MaterialTheme.typography.bodyLarge)
//        }
//    }
//}