package com.example.yallabuy_user.settings.view

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.yallabuy_user.R
import com.example.yallabuy_user.settings.viewmodel.CurrencyViewModel
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CurrencyScreen(
    viewModel: CurrencyViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    setTopBar: @Composable (content: @Composable () -> Unit) -> Unit
) {
    val selectedCurrency by viewModel.selectedCurrency.collectAsState()
    val currencyState by viewModel.currencyState.collectAsState()
    val availableCurrencies = listOf("USD", "EUR", "EGP", "SAR")
    setTopBar {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    "Currency", color = Color.White,
                    fontFamily = FontFamily(Font(R.font.caprasimo_regular)),
                    fontWeight = FontWeight.Bold
                )
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = colorResource(R.color.teal_80)
            ),
            navigationIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(onClick = onNavigateBack) {
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Choose your preferred currency",
            style = MaterialTheme.typography.titleMedium,
            color = colorResource(R.color.dark_turquoise),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        CurrencyPreferenceDropdown(
            selectedCurrency = selectedCurrency,
            availableCurrencies = availableCurrencies,
            onCurrencySelected = viewModel::selectCurrency
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyPreferenceDropdown(
    selectedCurrency: String,
    availableCurrencies: List<String>,
    onCurrencySelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedCurrency,
            onValueChange = {},
            readOnly = true,
            label = { Text("Preferred Currency", color = colorResource(R.color.dark_turquoise)) },
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null
                )
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                focusedBorderColor = colorResource(R.color.dark_turquoise),
                unfocusedBorderColor = colorResource(R.color.dark_turquoise).copy(alpha = 0.7f),
                focusedLabelColor = colorResource(R.color.dark_turquoise),
                unfocusedLabelColor = colorResource(R.color.dark_turquoise).copy(alpha = 0.7f),
                focusedTextColor = colorResource(R.color.dark_turquoise),
                unfocusedTextColor = colorResource(R.color.dark_turquoise)
            ),
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            availableCurrencies.forEach { currencyCode ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = currencyCode,
                            color = colorResource(R.color.dark_turquoise)
                        )
                    },
                    onClick = {
                        onCurrencySelected(currencyCode)
                        expanded = false
                    },
                    colors = if (currencyCode == selectedCurrency) {
                        MenuDefaults.itemColors(textColor = colorResource(R.color.mustard))
                    } else {
                        MenuDefaults.itemColors()
                    }
                )
            }
        }
    }
}
