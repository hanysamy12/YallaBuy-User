package com.mariammuhammad.yallabuy.View.Settings

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.yallabuy_user.R
import com.example.yallabuy_user.data.models.settings.SettingsItem
import com.example.yallabuy_user.ui.navigation.ScreenRoute
import com.mariammuhammad.yallabuy.ViewModel.Settings.SettingsViewModel


//We nav controller
//We need to navigate here and tto change the function name
//remove the view Model since it's static data and make everything here

@Composable
fun SettingsScreen(navController: NavController) {
    val settingsItems = listOf(
        SettingsItem(
            title = "Address",
            icon = R.drawable.location_on,
            onClick = {
                navController.navigate(ScreenRoute.Address.route)
            }
        ),
        SettingsItem(
            title = "Currency",
            icon = R.drawable.currency_exchange,
            onClick = {
                navController.navigate(ScreenRoute.Currency.route)
            }
        ),
        SettingsItem(
            title = "Contact us",
            icon = R.drawable.headset_mic,
            onClick = {
                navController.navigate(ScreenRoute.ContactUs.route)
            }
        ),
        SettingsItem(
            title = "About us",
            icon = R.drawable.info,
            onClick = {
                navController.navigate(ScreenRoute.AboutUs.route)
            }
        ),

        SettingsItem(
            title = "Previous Orders",
            icon = R.drawable.app_icon2,
            onClick = {
                navController.navigate(ScreenRoute.PreviousOrders.route)
            }
        )
    )

    Scaffold(
        topBar = { SettingsTopAppBar() },
        containerColor = Color.White
    ) { innerPadding ->
        SettingsListContent(
            settingsItems = settingsItems,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopAppBar() {
    TopAppBar(
        title = {
            Text(
                text = "Settings",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(R.color.dark_blue)
        )
    )
}

@Composable
fun SettingsListContent(
    settingsItems: List<SettingsItem>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        items(settingsItems) { item ->
            SettingsListItem(item = item)
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.5.dp,
                color = Color.LightGray
            )
        }
    }
}

@Composable
fun SettingsListItem(item: SettingsItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { item.onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = item.icon),
            contentDescription = item.title,
            modifier = Modifier.size(24.dp),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = item.title,
            fontSize = 16.sp,
            color = colorResource(id = R.color.dark_blue),
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Filled.ArrowForward,
            contentDescription = "Navigate",
            modifier = Modifier.size(16.dp),
            tint = colorResource(R.color.dark_blue)
        )
    }
}
