package com.example.yallabuy_user.settings.view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yallabuy_user.R
import com.example.yallabuy_user.settings.viewmodel.CurrencyViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CurrencyScreen(
    viewModel: CurrencyViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val selectedCurrency by viewModel.selectedCurrency.collectAsState()
    val currencyState by viewModel.currencyState.collectAsState()
    val availableCurrencies = listOf("USD", "EUR", "EGP", "SAR")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Currency Preference",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.dark_blue)
                )
            )
        },
        containerColor = Color.White
    ) {  paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Choose your preferred currency",
                style = MaterialTheme.typography.titleMedium,
                color = colorResource(R.color.dark_blue),
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
            label = { Text("Preferred Currency", color = colorResource(R.color.dark_blue)) },
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                focusedBorderColor = colorResource(R.color.dark_blue),
                unfocusedBorderColor = colorResource(R.color.dark_blue).copy(alpha = 0.7f),
                focusedLabelColor = colorResource(R.color.dark_blue),
                unfocusedLabelColor = colorResource(R.color.dark_blue).copy(alpha = 0.7f),
                focusedTextColor = colorResource(R.color.dark_blue),
                unfocusedTextColor = colorResource(R.color.dark_blue)
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
                            color = colorResource(R.color.dark_blue)
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
//
//@SuppressLint("ViewModelConstructorInComposable")
//@Preview(showBackground = true)
//@Composable
//fun CurrencyPreferenceSettingPreview() {
//    // Dummy ViewModel for preview
//    class PreviewViewModel : CurrencyViewModel(object : CurrencyPreferenceManager {
//
//        private val flow = MutableStateFlow("EGP")
//        override suspend fun getPreferredCurrency(): String = flow.value
//        override suspend fun setPreferredCurrency(currencyCode: String) { flow.value = currencyCode }
//        override val preferredCurrencyFlow: Flow<String> = flow
//    })
//
//    MaterialTheme {
//        CurrencyScreen(viewModel = PreviewViewModel())
//    }
//}
//
//@SuppressLint("ViewModelConstructorInComposable")
//@Preview(showBackground = true)
//@Composable
//fun CurrencyPreferenceSettingUSDPreview() {
//    // dummy ViewModel for preview
//    class PreviewViewModel : CurrencyViewModel(object : CurrencyPreferenceManager {
//        private val flow = MutableStateFlow("USD")
//        override suspend fun getPreferredCurrency(): String = flow.value
//        override suspend fun setPreferredCurrency(currencyCode: String) { flow.value = currencyCode }
//        override val preferredCurrencyFlow: Flow<String> = flow
//    })
//
//    MaterialTheme {
//        CurrencyScreen(viewModel = PreviewViewModel())
//    }
//}
