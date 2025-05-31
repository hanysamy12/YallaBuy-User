package com.example.yallabuy_user.ui.navigation

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.testshopify.ui.navigation.BottomNavigationBar
import com.example.yallabuy_user.R
import com.example.yallabuy_user.collections.CollectionsScreen
import com.example.yallabuy_user.home.HomeScreen


private const val TAG = "MainScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var onFilterClicked: ((String) -> Unit)? by remember { mutableStateOf(null) }

    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {},
        topBar = {
            when (currentRoute) {
                ScreenRoute.Home.route -> CenterAlignedTopAppBar(
                    title = { Text("Home") },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Yellow
                    )
                )

                ScreenRoute.Collections.route -> CenterAlignedTopAppBar(
                    title = { Text("Collections") },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Yellow
                    )
                )
            }
        },
        bottomBar = {
            Box(modifier = Modifier.height(60.dp)) {
                BottomNavigationBar((navController))
            }
            Log.i(TAG, "MainScreen: CurrentRoute  $currentRoute")
        },
        floatingActionButton = {
            if (currentRoute != null) {

                ExpandableFAB(
                    currentRoute = currentRoute,
                    onClothesClick = {
                        onFilterClicked?.invoke("CLOTHES")
                        ///   Log.i(TAG, "MainScreen: CLOTHES")
                    },
                    onShoesClick = {
                        onFilterClicked?.invoke("SHOES")
                        // Log.i(TAG, "MainScreen: SHOES")
                    })
            }


        }


    ) { contentPadding ->
        NavHost(
            navController = navController,
            startDestination = ScreenRoute.Home.route,
            modifier = Modifier.padding(contentPadding)
        ) {
            composable(route = ScreenRoute.Home.route)
            {
                HomeScreen()
            }
            composable(route = ScreenRoute.Collections.route)
            {
                CollectionsScreen(setFilterMeth = {
                    onFilterClicked = it
                })
            }
        }
    }
}

@Composable
fun ExpandableFAB(
    currentRoute: String,
    onClothesClick: () -> Unit,
    onShoesClick: () -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }

    if (currentRoute == ScreenRoute.Collections.route) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomEnd
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                //Clothes (top)
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(300)
                    ) + fadeIn(),
                    exit = slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(300)
                    ) + fadeOut()
                ) {
                    FloatingActionButton(onClick = {
                        onClothesClick()
                        isExpanded = false
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_tshirt),
                            contentDescription = "Clothes",
                            modifier = Modifier.size(32.dp),
                            tint = Color.Unspecified
                        )
                    }
                }

                // Shoes down
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(250)
                    ) + fadeIn(),
                    exit = slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(250)
                    ) + fadeOut()
                ) {
                    FloatingActionButton(onClick = {
                        onShoesClick()
                        isExpanded = false
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_shoes),
                            contentDescription = "Shoes",
                            modifier = Modifier.size(32.dp),
                            tint = Color.Unspecified
                        )
                    }
                }

                // Main FAB (bottom of the stack)
                FloatingActionButton(onClick = {
                    isExpanded = !isExpanded
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_collections),
                        contentDescription = "Choose SubCategory"
                    )
                }
            }
        }
    }
}
