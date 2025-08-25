package com.example.yallabuy_user.presentationLayer.settings.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.yallabuy_user.R
import com.example.yallabuy_user.presentationLayer.authentication.login.CustomerIdPreferences
import com.example.yallabuy_user.dataLayer.data.models.settings.Address
import com.example.yallabuy_user.dataLayer.data.models.settings.AddressBody
import com.example.yallabuy_user.presentationLayer.settings.viewmodel.AddressViewModel
import com.example.yallabuy_user.presentationLayer.ApiResponse
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddressFormScreen(
    navController: NavController,
    viewModel: AddressViewModel = koinViewModel(),
    addressId: Long?,
    fullAddress: String?,
    city: String?,
    country: String?
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var cityText by remember { mutableStateOf(city ?: "") }
    var countryText by remember { mutableStateOf(country ?: "") }
    var showValidationError by remember { mutableStateOf(false) }
    var address by remember {
        mutableStateOf(fullAddress?.let { viewModel.trimAddressFromZip(it) } ?: "")
    }
    val context = LocalContext.current
    val createUpdateState by viewModel.createUpdateState.collectAsState()

    viewModel.setCustomerId(CustomerIdPreferences.getData(context))

    LaunchedEffect(createUpdateState) {
        if (createUpdateState is ApiResponse.Success) {
            navController.navigate("address") {
                popUpTo("address_form") { inclusive = true }
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Add Address Form",
            modifier = Modifier.fillMaxWidth(),
            color = colorResource(R.color.dark_turquoise),
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("Recipient First Name") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Recipient Last Name") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = phone, onValueChange = { input -> phone = input.filter { it.isDigit() } },
            label = { Text("Recipient Phone") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Full Address") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = cityText, onValueChange = { cityText = it }, label = { Text("City") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = countryText, onValueChange = { countryText = it }, label = { Text("Country") })

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { navController.popBackStack() }) {
                Text("Cancel")
            }

            Button(onClick = {
                if (firstName.isBlank() || lastName.isBlank() || phone.isBlank() ||
                    address.isBlank() || cityText.isBlank() || countryText.isBlank()
                ) {
                    showValidationError = true
                } else {
                    val newAddress =
                        com.example.yallabuy_user.dataLayer.data.models.settings.Address(
                            id = addressId ?: 0,
                            customerId = viewModel.getCustomerId(),
                            firstName = firstName,
                            lastName = lastName,
                            phone = phone,
                            fullAddress = address,
                            city = cityText,
                            country = countryText,
                            default = false
                        )
                    viewModel.createAddress(
                        com.example.yallabuy_user.dataLayer.data.models.settings.AddressBody(
                            newAddress
                        )
                    )
                }
            }) {
                Text("Add")
            }
        }

        if (showValidationError) {
            MissingFieldsDialog { showValidationError = false }
        }
    }
}
