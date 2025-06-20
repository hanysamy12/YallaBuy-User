@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.yallabuy_user.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.yallabuy_user.R
import com.example.yallabuy_user.data.models.settings.SettingsItem
import com.example.yallabuy_user.ui.navigation.ScreenRoute
import com.mariammuhammad.yallabuy.View.Settings.SettingsListItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = koinViewModel(),
    setTopBar: ((@Composable () -> Unit)) -> Unit
) {

    val context = LocalContext.current
    val logoutState by viewModel.logoutState.collectAsState()


    LaunchedEffect(logoutState) {
        if (logoutState) {
            navController.navigate(ScreenRoute.Login.route) {
                popUpTo(0) { inclusive = true } // Clear entire back stack
            }
        }
        setTopBar {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Profile", color = Color.White,
                        fontFamily = FontFamily(Font(R.font.caprasimo_regular)),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF3B9A94)
                ),
                navigationIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_app),
                        contentDescription = "App Icon",
                        tint = Color.Unspecified, // Optional: set tint if needed
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            )
        }
    }


    Column(
        modifier = Modifier
            .padding(6.dp)
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
                icon = R.drawable.settings,
                onClick = {
                    navController.navigate(ScreenRoute.Settings.route)
                }
            )
        )


        SettingsListItem(
            item = SettingsItem(
                title = "Previous Orders",
                icon = R.drawable.ic_app,
                onClick = {
                    navController.navigate(ScreenRoute.PreviousOrders.route)
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
                    viewModel.logout(context)
                    navController.navigate(ScreenRoute.Login.route) {
                        popUpTo(ScreenRoute.Profile.route) { inclusive = true }
                    }
                }
            )
        )
    }

}
