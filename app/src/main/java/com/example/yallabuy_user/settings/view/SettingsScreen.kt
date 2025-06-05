package com.mariammuhammad.yallabuy.View.Settings

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yallabuy_user.R
import com.example.yallabuy_user.settings.model.local.SettingsItem
import com.mariammuhammad.yallabuy.ViewModel.Settings.SettingsViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = viewModel(),
    onNavigateToAboutUs: () -> Unit = {},
    onNavigateToContactUs: () -> Unit = {},
    onNavigateToAddress: () -> Unit = {},
    onNavigateToCurrency: () -> Unit = {}
) {



   // LaunchedEffect(key1 = Unit) {
        // Assuming loadSettingsItems is marked @Composable in ViewModel
        settingsViewModel.loadSettingsItems()
    //}
    val originalSettingsItems by settingsViewModel.settingsItems.collectAsState()


    val navigatedSettingsItems = remember(originalSettingsItems, onNavigateToAboutUs, onNavigateToContactUs, onNavigateToAddress, onNavigateToCurrency) {
        originalSettingsItems.map { item ->
            when (item.title) {
                "About us" -> item.copy(onClick = onNavigateToAboutUs)
                "Contact us" -> item.copy(onClick = onNavigateToContactUs)
                "Address" -> item.copy(onClick = onNavigateToAddress)
                "Currency" -> item.copy(onClick = onNavigateToCurrency)
                else -> item
            }
        }
    }

    Scaffold(
        topBar = {
            SettingsTopAppBar()
        },
        containerColor = Color.White
    ) { paddingValues ->
        SettingsListContent(settingsItems = navigatedSettingsItems, paddingValues = paddingValues)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopAppBar() {
    TopAppBar(
        title = { Text("Settings", color = Color.White, fontWeight = FontWeight.Bold) },
        colors = TopAppBarDefaults.topAppBarColors(

            containerColor = colorResource(R.color.dark_blue)
        )
    )
}

@Composable
fun SettingsListContent(settingsItems: List<SettingsItem>, paddingValues: PaddingValues) {
    LazyColumn(
        modifier = Modifier
            .padding(paddingValues)
            .padding(top = 16.dp)
            .fillMaxSize()
    ) {
        items(settingsItems) { item ->
            SettingsListItem(item = item)
            Divider(color = Color.LightGray, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
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
            tint = Color.Unspecified // Use Unspecified for Painters unless tinting source
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

 @Preview(showBackground = true)
 @Composable
 fun SettingsScreenPreview() {
     SettingsScreen()
 }
