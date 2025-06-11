package com.example.yallabuy_user.settings.view

import android.location.Geocoder
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yallabuy_user.R
import com.example.yallabuy_user.settings.viewmodel.AddressViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable

//Permissions
//check location enabled or not and do it as we did in climate app
//move the camera to user location
//I need a text above when the user scroll to write the location in the text as I made the pin fixed in the center of the map
//I want the address line of the lat and long so the user can understand it


fun MapLocationScreen(
    viewModel: AddressViewModel= koinViewModel(),
    onNavigateBack: () -> Unit,
   // onLocationConfirmed: (Address) -> Unit
) {
    var selectedLocation by remember { mutableStateOf(LatLng(30.0444, 31.2357)) } // Default to Cairo
    var currentAddress by remember { mutableStateOf("Searching for address...") }

    val context = LocalContext.current

    val geocoder = remember { Geocoder(context, Locale.getDefault()) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(selectedLocation, 15f)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select delivery location", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.dark_blue)
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
                .padding(paddingValues)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Current location",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Gray
                    )
                    Text(
                        text = currentAddress,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = true,
                    mapType = MapType.NORMAL
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = true,
                    mapToolbarEnabled = false
                )
            ) {
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
//                    createAddressFromLocation(
//                        geocoder = geocoder,
//                        location = cameraPositionState.position.target,
//                        addressText = currentAddress
//                    ) { address ->
//                        onLocationConfirmed(address)
//                    }
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
