package com.example.yallabuy_user.utilities

import android.app.Application
import android.net.ConnectivityManager
import android.net.Network
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private const val TAG = "NetworkObserver"
class NetworkObserver(context: Application) {
    private val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
    private val mutableNetworkStatus = MutableStateFlow<NetworkStatus>(NetworkStatus.Available)
    val networkStatus: StateFlow<NetworkStatus> = mutableNetworkStatus
    init {
        val currentNetwork = connectivityManager.activeNetwork
        if (currentNetwork == null) {
            Log.i(TAG, "onCreate: No Internet ONStart")
            mutableNetworkStatus.value = NetworkStatus.Lost

        }
        connectivityManager.registerDefaultNetworkCallback(object :
            ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Log.i("TAG", "The default network is now: $network")
                mutableNetworkStatus.value = NetworkStatus.Available
            }
            override fun onLost(network: Network) {
                Log.i(TAG, "onCreate: No Internet Lost")
                mutableNetworkStatus.value = NetworkStatus.Lost
            }
        })
    }
}

sealed class NetworkStatus {
    data object Available : NetworkStatus()
    data object Lost : NetworkStatus()
}