package com.mariammuhammad.yallabuy.View.Settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.yallabuy_user.R
import com.example.yallabuy_user.authentication.login.CustomerIdPreferences
import com.example.yallabuy_user.data.models.settings.SettingsItem
import com.example.yallabuy_user.ui.navigation.ScreenRoute


//We nav controller
//We need to navigate here and tto change the function name
//remove the view Model since it's static data and make everything here

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, setTopBar: ((@Composable () -> Unit)) -> Unit) {

    LaunchedEffect(Unit) {
        setTopBar {
            setTopBar {
                CenterAlignedTopAppBar(
                    title = { Text("Settings") },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = colorResource(R.color.teal_80)
                    ),
                    navigationIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    contentDescription = "Back"

                                )
                            }
                            Icon(
                                painter = painterResource(id = R.drawable.ic_app),
                                contentDescription = "App Icon",
                                tint = Color.Unspecified,
                                //modifier = Modifier.padding(start = 5.dp)
                            )
                        }
                    }

                )
            }
        }
    }
    var showLoginDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val settingsItems = listOf(
        SettingsItem(
            title = "Address",
            icon = R.drawable.location_on,
            onClick = {
                if (CustomerIdPreferences.getData(context) != 0L) {
                    navController.navigate(ScreenRoute.Address.route)
                } else {
                    showLoginDialog = true

                }
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

    )


    SettingsListContent(
        settingsItems = settingsItems,
        modifier = Modifier.padding(6.dp)
    )

    if (showLoginDialog) {
        LoginPromptDialog(
            onDismiss = { showLoginDialog = false },
            onConfirm = {
                showLoginDialog = false
                navController.navigate(ScreenRoute.Login.route)
            }
        )
    }

}


@Composable
fun LoginPromptDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sign Up Required") },
        text = { Text("You need to sign up or log in to access your address.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Sign Up")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
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
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Navigate",
            modifier = Modifier.size(16.dp),
            tint = colorResource(R.color.dark_blue)
        )
    }
}
