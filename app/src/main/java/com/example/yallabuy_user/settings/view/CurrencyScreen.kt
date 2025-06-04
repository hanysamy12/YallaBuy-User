package com.example.yallabuy_user.settings.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yallabuy_user.R
import com.example.yallabuy_user.settings.model.local.CurrencyUiState
import com.example.yallabuy_user.settings.viewmodel.CurrencyViewModel


@Composable
fun CurrencyScreen(
    currencyViewModel: CurrencyViewModel = viewModel()
) {
    val uiState by currencyViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CurrencyTopAppBar()  // lama ntf2 3ala el nav I will put it
        },
        containerColor = Color.White
    ) { paddingValues ->
        CurrencyContent(
            uiState = uiState,
            onAmountChange = currencyViewModel::onAmountChange,
            onFromCurrencySelected = currencyViewModel::onFromCurrencySelected,
            onToCurrencySelected = currencyViewModel::onToCurrencySelected,
            onConvertClicked = currencyViewModel::onConvertClicked,
            onRetryFetchClicked = currencyViewModel::onRetryFetchClicked,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyTopAppBar() {
    TopAppBar(
        title = { Text("Currency Converter", color = Color.White, fontWeight = FontWeight.Bold) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(R.color.dark_blue)
        )
        //zorar el nav
    )
}

@Composable
fun CurrencyContent(
    uiState: CurrencyUiState,
    onAmountChange: (String) -> Unit,
    onFromCurrencySelected: (String) -> Unit,
    onToCurrencySelected: (String) -> Unit,
    onConvertClicked: () -> Unit,
    onRetryFetchClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(vertical = 32.dp))
        }

        if (uiState.errorMessage != null && !uiState.isLoading) {
            Text(
                text = uiState.errorMessage,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            if (!uiState.areRatesAvailable) {
                Button(onClick = onRetryFetchClicked) {
                    Text("Retry Fetching Rates")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.amountInput,
            onValueChange = onAmountChange,
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CurrencySelector(
                label = "From",
                selectedCurrency = uiState.fromCurrency,
                availableCurrencies = uiState.availableCurrencies,
                onCurrencySelected = onFromCurrencySelected,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            CurrencySelector(
                label = "To",
                selectedCurrency = uiState.toCurrency,
                availableCurrencies = uiState.availableCurrencies,
                onCurrencySelected = onToCurrencySelected,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Convert Button
        Button(
            onClick = onConvertClicked,
            enabled = uiState.amountInput.isNotEmpty() && uiState.areRatesAvailable && !uiState.isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.mustard),
                contentColor = colorResource(R.color.dark_blue)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Convert Currency", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.conversionResult.isNotEmpty()) {
            Text(
                text = uiState.conversionResult,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.dark_blue),
                textAlign = TextAlign.Center
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencySelector(
    label: String,
    selectedCurrency: String,
    availableCurrencies: List<String>,
    onCurrencySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedCurrency,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
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
                    }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CurrencyScreenPreview() {
    // Create a dummy ViewModel or State for previewing
    val previewState = CurrencyUiState(
        amountInput = "100",
        fromCurrency = "USD",
        toCurrency = "EGP",
        conversionResult = "4,750.00 EGP",
        isLoading = false,
        errorMessage = null,
        rates = mapOf("USD" to 1.0, "EGP" to 47.5, "EUR" to 0.9, "SAR" to 3.75) // Dummy rates
    )
    MaterialTheme {
        Scaffold(topBar = { CurrencyTopAppBar() }) { padding ->
            CurrencyContent(
                uiState = previewState,
                onAmountChange = {},
                onFromCurrencySelected = {},
                onToCurrencySelected = {},
                onConvertClicked = {},
                onRetryFetchClicked = {},
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CurrencyScreenLoadingPreview() {
    val previewState = CurrencyUiState(isLoading = true)
    MaterialTheme {
        Scaffold(topBar = { CurrencyTopAppBar() }) { padding ->
            CurrencyContent(
                uiState = previewState,
                onAmountChange = {},
                onFromCurrencySelected = {},
                onToCurrencySelected = {},
                onConvertClicked = {},
                onRetryFetchClicked = {},
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CurrencyScreenErrorPreview() {
    val previewState = CurrencyUiState(errorMessage = "Failed to load rates. Check connection.", rates = null)
    MaterialTheme {
        Scaffold(topBar = { CurrencyTopAppBar() }) { padding ->
            CurrencyContent(
                uiState = previewState,
                onAmountChange = {},
                onFromCurrencySelected = {},
                onToCurrencySelected = {},
                onConvertClicked = {},
                onRetryFetchClicked = {},
                modifier = Modifier.padding(padding)
            )
        }
    }
}

