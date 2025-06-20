package com.example.yallabuy_user.settings.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.yallabuy_user.R
import com.example.yallabuy_user.data.models.settings.Address
import com.example.yallabuy_user.data.models.settings.AddressBody
import com.example.yallabuy_user.data.models.settings.AddressesResponse
import com.example.yallabuy_user.settings.viewmodel.AddressViewModel
import com.example.yallabuy_user.utilities.ApiResponse
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressScreen(
    customerId: Long,
    onNavigateBack: () -> Unit = {},
    onNavigateToMap: () -> Unit,
    setTopBar: ((@Composable () -> Unit)) -> Unit
) {
    val viewModel: AddressViewModel =
        koinViewModel()// remember { AddressViewModel(getKoin(), customerId) }
    viewModel.setCustomerId(customerId)

    val addressState by viewModel.addressState.collectAsState()

//    val addressList = when (addressState) {
//        is ApiResponse.Success -> (addressState as ApiResponse.Success).data.addresses
//        else -> emptyList()
//    }

    var showDialog by remember { mutableStateOf(false) }
    var addressToEdit: Address? by remember { mutableStateOf(null) }
    var showEditConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(customerId) {
        viewModel.getAddresses()
        setTopBar {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Address", color = Color.White,
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
    }
    Scaffold(
        //topBar = { AddressTopBar(onNavigateBack = onNavigateBack) },

        floatingActionButton = {
            ExpandableFab(
                onAddAddressClick = {
                    addressToEdit = null
                    showDialog = true
                },
                onAddByMapClick = {
                    onNavigateToMap()
                }
            )
        }
    ) { paddingValues ->
        AddressScreenContent(
            addressState = addressState,
            viewModel = viewModel,
            paddingValues = paddingValues,
            onEditAddress = { address ->
                addressToEdit = address
                showEditConfirmation = true
            }
        )
    }

    if (showEditConfirmation) {
        EditConfirmationDialog(
            onDismiss = { showEditConfirmation = false },
            onConfirm = {
                showEditConfirmation = false
                showDialog = true
            }
        )
    }

    if (showDialog) { //for add/edit form
        EditAddressDialog(
            address = addressToEdit,
            onDismiss = {
                showDialog = false
                addressToEdit = null
            },
            onConfirm = { newAddress ->
                if (addressToEdit != null) {
                    viewModel.updateAddress(newAddress.id, AddressBody(newAddress))
                } else {
                    viewModel.createAddress(AddressBody(newAddress))
                }
                showDialog = false
                addressToEdit = null
            }
        )
    }
}


@Composable
fun AddressScreenContent(
    addressState: ApiResponse<AddressesResponse>,
    viewModel: AddressViewModel,
    paddingValues: PaddingValues,
    onEditAddress: (Address) -> Unit,
) {

    val addressList = viewModel.addressesList.collectAsStateWithLifecycle()


    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
    ) {

        when (addressState) {
            is ApiResponse.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is ApiResponse.Failure -> {
                val error = addressState.error
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${error.message}", color = Color.Red)
                }
            }

            is ApiResponse.Success -> {
                val addresses = addressState.data.addresses

                if (addresses.isEmpty()) {
                    EmptyAddressList()
                } else {

                    AddressList(
                        addresses = viewModel.addressesList.collectAsState().value,
                        onEditAddress = onEditAddress,
                        onDeleteAddress = { viewModel.deleteAddress(it) },
                        onSetDefault = { address -> viewModel.setDefaultAddress(address) }
                    )
                }
            }
        }
    }
}


@Composable
fun AddressList(
    addresses: List<Address>,
    onEditAddress: (Address) -> Unit,
    onDeleteAddress: (Long) -> Unit,
    onSetDefault: (Address) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(addresses) { address ->
            AddressItem(
                address = address,
                onEdit = { onEditAddress(address) },
                onDelete = { onDeleteAddress(address.id) },
                onSetDefault = { onSetDefault(address) }
            )
        }
    }
}

@Composable
fun AddressItem(
    address: Address,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSetDefault: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
            Text(
                text = address.getDetailedDescription(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (address.default) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        Text("Default Address")
                    }
                }
            }

            //     }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = address.fullAddress,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = address.phone,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!address.default) {
                    TextButton(onClick = onSetDefault) {
                        Text("Set as default")
                    }
                }

                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }

                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }
    }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                onDelete()
                showDeleteDialog = false
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAddressDialog(
    address: Address?,
    onDismiss: () -> Unit,
    onConfirm: (Address) -> Unit
) {
    var firstName by remember { mutableStateOf(address?.firstName ?: "") }
    var lastName by remember { mutableStateOf(address?.lastName ?: "") }
    var phone by remember { mutableStateOf(address?.phone ?: "") }
    //var city by remember { mutableStateOf(address?.city ?: "") }
    val egyptianCities = listOf(
        "Cairo", "Giza", "Alexandria", "Qalyubia", "Dakahlia", "Sharqia",
        "Gharbia", "Monufia", "Beheira", "Kafr El Sheikh", "Damietta",
        "Port Said", "Ismailia", "Suez", "North Sinai", "South Sinai",
        "Red Sea", "Fayoum", "Beni Suef", "Minya", "Assiut", "Sohag",
        "Qena", "Luxor", "Aswan", "New Valley (El Wadi El Gedid)", "Matrouh"
    )
    var country = "Egypt" //by remember { mutableStateOf(address?.country ?: "") }
    var fullAddress by remember { mutableStateOf(address?.fullAddress ?: "") }
    var isDefault by remember { mutableStateOf(address?.default ?: false) }

    var showValidationError by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var selectedCity by remember { mutableStateOf(address?.city ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (address == null) "Add New Address" else "Edit Address") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("Recipient First Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Recipient Last Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Recipient Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedCity,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("City") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        egyptianCities.forEach { city ->
                            DropdownMenuItem(
                                text = { Text(city) },
                                onClick = {
                                    selectedCity = city
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = country,
                    onValueChange = {},
                    label = { Text("Country") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = fullAddress,
                    onValueChange = { fullAddress = it },
                    label = { Text("Full Address") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                if (address == null || !address.default) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = isDefault,
                            onCheckedChange = { isDefault = it }
                        )
                        Text(
                            text = "Set as default address",
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (firstName.isBlank() || lastName.isBlank() || phone.isBlank() ||
                        fullAddress.isBlank() || country.isBlank()
                    ) {
                        showValidationError = true
                    } else {
                        val newAddress = Address(
                            id = address?.id ?: 0,
                            firstName = firstName,
                            lastName = lastName,
                            phone = phone,
                            city = selectedCity,
                            country = country,
                            fullAddress = fullAddress,
                            default = isDefault
                        )
                        onConfirm(newAddress)
                    }
                }
            ) {
                Text(if (address == null) "Add" else "Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    if (showValidationError) {
        MissingFieldsDialog { showValidationError = false }
    }
}


//@Composable
//fun AddAddressFab(onClick: () -> Unit) {
//    FloatingActionButton(onClick = onClick) {
//        Icon(Icons.Default.Add, contentDescription = "Add Address")
//    }
//}


@Composable
fun EmptyAddressList() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.LocationOn,
            contentDescription = "No addresses",
            modifier = Modifier.size(64.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No addresses saved",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Gray
        )
        Text(
            text = "Add your first address by tapping the + button",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@Composable
fun ExpandableFab(
    onAddAddressClick: () -> Unit,
    onAddByMapClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.wrapContentSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(horizontalAlignment = Alignment.End) {
            if (expanded) {
                FloatingActionButton(
                    onClick = onAddByMapClick,
                    modifier = Modifier.padding(bottom = 12.dp),
                    containerColor = colorResource(R.color.light_gray)
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Add by Map",
                        tint = colorResource(id = R.color.dark_blue)
                    )
                }

                FloatingActionButton(
                    onClick = onAddAddressClick,
                    modifier = Modifier.padding(bottom = 12.dp),
                    containerColor = Color.LightGray
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Add Address",
                        tint = colorResource(id = R.color.dark_blue)
                    )
                }
            }

            FloatingActionButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = if (expanded) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = "Toggle"
                )
            }
        }
    }
}

@Composable
fun EditConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Address") },
        text = { Text("Are you sure you want to edit this address?") },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
            ) {
                Text("Edit")
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
fun MissingFieldsDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Incomplete Information") },
        text = { Text("Please complete all required fields.") },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@Composable
fun DeleteConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Address") },
        text = { Text("Are you sure you want to delete this address?") },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


