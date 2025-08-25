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

/*
Clean Architecture usually has 3 main layers:

--Presentation Layer (UI)

-Jetpack Compose screens, ViewModels.

-Uses StateFlow observe UI state.

-Talks only to the Domain layer (not directly to Repository/Retrofit).

--Domain Layer (Business Rules)

-Contains Entities (business models) and Use Cases (Interactors).

-Pure Kotlin (no Android/Retrofit/Room dependencies).

-Exposes simple functions like GetProductsUseCase, AddToCartUseCase.

--Data Layer (Data Sources)

-Handles API (Retrofit), Local DB (Room/DataStore), Firebase, etc.

-Implements repository interfaces defined in Domain layer.
 */