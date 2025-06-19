package com.example.yallabuy_user


import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import com.example.yallabuy_user.ui.navigation.MainScreen
import com.example.yallabuy_user.ui.navigation.NOInternetScreen
import com.example.yallabuy_user.ui.theme.YallaBuyUserTheme
import com.example.yallabuy_user.utilities.NetworkObserver
import com.example.yallabuy_user.utilities.NetworkStatus

class MainActivity : ComponentActivity() {
    private lateinit var networkObserver: NetworkObserver
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
         networkObserver = NetworkObserver(application)
        setContent {
            val networkStatus = networkObserver.networkStatus.collectAsState()

            YallaBuyUserTheme {
                when (networkStatus.value) {
                    is NetworkStatus.Available -> { MainScreen()}
                    is NetworkStatus.Lost -> NOInternetScreen()
                }

            }
        }
    }
}

