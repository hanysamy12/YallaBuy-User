package com.example.yallabuy_user.settings.view

import android.Manifest
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.yallabuy_user.R
import com.example.yallabuy_user.settings.viewmodel.AddressViewModel
import com.example.yallabuy_user.utilities.LocationPermissionManager
import com.example.yallabuy_user.utilities.PermissionUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import org.koin.androidx.compose.koinViewModel
import java.util.Locale


//Permissions
//check location enabled or not and do it as we did in climate app
//move the camera to user location
//I need a text above when the user scroll to write the location in the text as I made the pin fixed in the center of the map
//I want the address line of the lat and long so the user can understand it

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapLocationScreen(
    viewModel: AddressViewModel = koinViewModel(),
    locationPermissionManager: LocationPermissionManager,
    onNavigateBack: () -> Unit,
    navController: NavController,
    setTopBar: ((@Composable () -> Unit)) -> Unit,
    // onLocationConfirmed: (Address) -> Unit
) {
    LaunchedEffect(Unit) {
        setTopBar {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Map", color = Color.White,
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
    val context = LocalContext.current
    val activity = LocalActivity.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var selectedLocation by remember { mutableStateOf(LatLng(30.0444, 31.2357)) }
    var currentAddress by remember { mutableStateOf("Searching for address...") }
    var city by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(selectedLocation, 15f)
    }


    var showRationaleDialog by remember { mutableStateOf(false) }
    var showPermissionDeniedDialog by remember { mutableStateOf(false) }

    val hasPermission by locationPermissionManager.hasLocationPermission.collectAsState()
    val isLocationEnabled by locationPermissionManager.isLocationEnabled.collectAsState()

    var showLocationSettingsDialog by remember { mutableStateOf(false) }
    var showInternetDialog by remember { mutableStateOf(false) }

    val geocoder = remember { Geocoder(context, Locale.getDefault()) }

    var detectedAddress by remember { mutableStateOf("Tap on the map to select an address") }
    var showEditDialog by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result ->
        PermissionUtils.onPermissionResult(
            permission = Manifest.permission.ACCESS_FINE_LOCATION,
            isPermissionGranted = result,
            activity = activity,
            onGranted = {
                locationPermissionManager.getUserCurrentLocation(context) { latLng ->
                    selectedLocation = latLng
                    cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                }
            },
            onShowRational = {
                //user refused the permission once
                showRationaleDialog = true

            },
            onPermanentlyRefused = {

                //user refused the permission twice
                showPermissionDeniedDialog = true

            }
        )
    }

//    LaunchedEffect(cameraPositionState.position) {
//        delay(500)
//        updateAddressFromLocation(
//            geocoder = geocoder,
//            location = cameraPositionState.position.target,
//            onAddressUpdated = { address ->
//                currentAddress = address
//            }
//        )
//    }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            selectedLocation = cameraPositionState.position.target
            Geocoder(context, Locale.getDefault()).getFromLocation(
                selectedLocation.latitude,
                selectedLocation.longitude,
                1,
                object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<android.location.Address>) {
                        if (addresses.isNotEmpty()) {
                            val addr = addresses[0]
                            currentAddress = addr.getAddressLine(0) ?: "Address not found"
                            city = addr.locality ?: ""
                            country = addr.countryName ?: ""
                        } else {
                            currentAddress = "Address not found"
                            city = ""
                            country = ""
                        }
                    }

                    override fun onError(errorMessage: String?) {
                        currentAddress = "Address not found"
                        city = ""
                        country = ""
                    }
                }
            )
        }
    }


    LaunchedEffect(Unit) {
        locationPermissionManager.checkPermission() //internet
        locationPermissionManager.updateLocationEnabled()
        showInternetDialog = !locationPermissionManager.isInternetAvailable()
    }

    LaunchedEffect(Unit) {
        if (!PermissionUtils.checkPermission(context)) {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

//    LaunchedEffect(hasPermission) {
//        if (!hasPermission) {
//            locationPermissionManager.requestPermission()
//        }
//    }

    LaunchedEffect(isLocationEnabled, hasPermission) {
        showLocationSettingsDialog = hasPermission && !isLocationEnabled
    }

    LaunchedEffect(hasPermission, isLocationEnabled) {
//        if (hasPermission && isLocationEnabled) {
//            locationPermissionManager.getUserCurrentLocation(context) { latLng ->
//                selectedLocation = latLng
//                cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
//            }
//        }
    }

    if (showInternetDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("No Internet Connection") },
            text = { Text("We need an internet connection to detect your address. Please enable Wi-Fi or mobile data.") },
            confirmButton = {
                TextButton(onClick = {
                    locationPermissionManager.openInternetSettings()
                    showInternetDialog = false
                }) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showInternetDialog = false
                    onNavigateBack()
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showLocationSettingsDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Enable Location") },
            text = { Text("Location services are disabled. Please enable them in settings.") },
            confirmButton = {
                TextButton(onClick = {
                    locationPermissionManager.openLocationSettings()
                    showLocationSettingsDialog = false
                }) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showLocationSettingsDialog = false
                    onNavigateBack()
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showRationaleDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Why we need your location") },
            text = {
                Text("We use your location to detect where you want your order delivered. Please grant permission.")
            },
            confirmButton = {
                TextButton(onClick = {
                    showRationaleDialog = false
                    launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }) {
                    Text("Grant Permission")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showRationaleDialog = false
                    onNavigateBack()
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showPermissionDeniedDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Permission Denied") },
            text = {
                Text("You've permanently denied location access. Please go to settings and enable it manually.")
            },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionDeniedDialog = false
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                }) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPermissionDeniedDialog = false
                    onNavigateBack()
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(

    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {


            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = false,
                    mapType = MapType.NORMAL
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = false,
                    mapToolbarEnabled = false
                )
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp)
                    .align(Alignment.TopCenter),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Current location",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Gray
                    )
                    Text(
                        text = currentAddress,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location Marker",
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center)
                    .offset(y = (-24).dp),
                tint = colorResource(id = R.color.dark_blue)
            )

            FloatingActionButton(
                onClick = {
//                    getCurrentLocation(context) { location ->
//                        selectedLocation = location
//                        cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 15f)
                    //}
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "My Location",
                    tint = colorResource(id = R.color.dark_blue)
                )
            }

            Button(
                onClick = {
                    navController.navigate(
                        "address_form" +
                                "?fullAddress=${Uri.encode(currentAddress)}" +
                                "&city=${Uri.encode(city)}" +
                                "&country=${Uri.encode(country)}"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.dark_blue)
                )
            ) {
                Text(
                    "Confirm location",
                    modifier = Modifier.padding(vertical = 8.dp),
                    fontSize = 18.sp
                )
            }


        }
    }
}

@Composable
fun EditAddressDialog(
    detectedAddress: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var address by remember { mutableStateOf(detectedAddress) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Address") },
        text = {
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(address)
                onDismiss()
            }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
