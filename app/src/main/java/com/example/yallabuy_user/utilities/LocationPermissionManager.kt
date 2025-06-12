package com.example.yallabuy_user.utilities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LocationPermissionManager(
    private val context: Context,
    private val activity: ComponentActivity
) {

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val _hasLocationPermission = MutableStateFlow(false)
    val hasLocationPermission: StateFlow<Boolean> = _hasLocationPermission

    private val _isLocationEnabled = MutableStateFlow(isLocationEnabled())
    val isLocationEnabled: StateFlow<Boolean> = _isLocationEnabled

    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    //request location permission inside the composable
    //rememberActivityResult
    fun initPermissionLauncher(onPermissionResult: (Boolean) -> Unit) {
        permissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            _hasLocationPermission.value = granted
            onPermissionResult(granted)
        }
    }

    fun checkPermission() {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        _hasLocationPermission.value = granted
    }

    fun requestPermission() {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    fun isLocationEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun updateLocationEnabled() {
        _isLocationEnabled.value = isLocationEnabled()
    }

    fun openLocationSettings() {
        context.startActivity(
            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                .apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
        )
    }

    fun isInternetAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun openInternetSettings() {
        context.startActivity(
            Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }
    @SuppressLint("MissingPermission")
    fun getUserCurrentLocation(
        context: Context,
        onLocationReceived: (LatLng) -> Unit
    ) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                onLocationReceived(LatLng(it.latitude, it.longitude))
            }
        }
    }
}
