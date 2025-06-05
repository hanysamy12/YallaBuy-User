package com.example.yallabuy_user.settings.view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import com.example.yallabuy_user.settings.model.remote.CurrencyPreferenceManager
import com.example.yallabuy_user.settings.viewmodel.CurrencyViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.compose.material3.MenuDefaults


@Composable
fun CurrencyScreen(
    viewModel: CurrencyViewModel = viewModel() // use Hilt/Koin for injection
) {
    val selectedCurrency by viewModel.preferredCurrency.collectAsState()
    val availableCurrencies = viewModel.availableCurrencies

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Choose your preferred currency",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        CurrencyPreferenceDropdown(
            selectedCurrency = selectedCurrency,
            availableCurrencies = availableCurrencies,
            onCurrencySelected = viewModel::onCurrencySelected
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyPreferenceDropdown(
    selectedCurrency: String,
    availableCurrencies: List<String>,
    onCurrencySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedCurrency,
            onValueChange = {},
            readOnly = true,
            label = { Text("Preferred Currency") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
            ),
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            availableCurrencies.forEach { currencyCode ->
                DropdownMenuItem(
                    text = { Text(currencyCode) },
                    onClick = {
                        onCurrencySelected(currencyCode)
                        expanded = false
                    },

                    colors = if (currencyCode == selectedCurrency) {
                        MenuDefaults.itemColors(textColor = colorResource(R.color.mustard)) // highlight
                    } else {
                        MenuDefaults.itemColors()
                    }

                )
            }
        }
    }
}



@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun CurrencyPreferenceSettingPreview() {
    // Dummy ViewModel for preview
    class PreviewViewModel : CurrencyViewModel(object : CurrencyPreferenceManager {

        private val flow = MutableStateFlow("EGP")
        override suspend fun getPreferredCurrency(): String = flow.value
        override suspend fun setPreferredCurrency(currencyCode: String) { flow.value = currencyCode }
        override val preferredCurrencyFlow: Flow<String> = flow
    })

    MaterialTheme {
        CurrencyScreen(viewModel = PreviewViewModel())
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun CurrencyPreferenceSettingUSDPreview() {
    // dummy ViewModel for preview
    class PreviewViewModel : CurrencyViewModel(object : CurrencyPreferenceManager {
        private val flow = MutableStateFlow("USD")
        override suspend fun getPreferredCurrency(): String = flow.value
        override suspend fun setPreferredCurrency(currencyCode: String) { flow.value = currencyCode }
        override val preferredCurrencyFlow: Flow<String> = flow
    })

    MaterialTheme {
        CurrencyScreen(viewModel = PreviewViewModel())
    }
}
