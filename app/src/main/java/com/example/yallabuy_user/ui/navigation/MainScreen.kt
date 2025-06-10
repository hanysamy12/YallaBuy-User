package com.example.yallabuy_user.ui.navigation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import com.example.yallabuy_user.R
import com.example.yallabuy_user.authentication.login.CustomerIdPreferences
import com.example.yallabuy_user.authentication.login.LoginScreen
import com.example.yallabuy_user.authentication.registration.RegistrationScreen
import com.example.yallabuy_user.cart.CartScreen
import com.example.yallabuy_user.collections.CollectionsScreen
import com.example.yallabuy_user.home.HomeScreen
import com.example.yallabuy_user.productInfo.ProductInfoScreen
import com.example.yallabuy_user.products.ProductsScreen
import com.example.yallabuy_user.profile.ProfileScreen
import com.example.yallabuy_user.wish.WishScreen


private const val TAG = "MainScreen"

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var onFilterClicked: ((String) -> Unit)? by remember { mutableStateOf(null) }
    var startDestination = remember { mutableStateOf(ScreenRoute.Registration.route) }
    val context = LocalContext.current

    val customerId = CustomerIdPreferences.getData(context)
    if (customerId != 0L) {
        startDestination.value = ScreenRoute.Home.route
    }


    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    var isShowFilterBarProductsScreen by remember { mutableStateOf(false) }

    val bottomNavRoutes = listOf(
        ScreenRoute.Home.route,
        ScreenRoute.Collections.route,
        ScreenRoute.WishList.route,
        ScreenRoute.Cart.route,
        ScreenRoute.Profile.route
    )
    Scaffold(modifier = Modifier.fillMaxSize(), snackbarHost = {}, topBar = {
        Log.i(TAG, "MainScreen: CurrentRoute  $currentRoute")

        when {
            currentRoute == ScreenRoute.Home.route -> CenterAlignedTopAppBar(
                title = { Text("Home") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFFFC107)
                )
            )

            currentRoute == ScreenRoute.WishList.route -> CenterAlignedTopAppBar(
                title = { Text("Wish List") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFFFC107)
                )
            )


            currentRoute == ScreenRoute.Collections.route -> CenterAlignedTopAppBar(
                title = { Text("Collections") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFFFC107)
                )
            )

            currentRoute == ScreenRoute.Cart.route -> CenterAlignedTopAppBar(
                title = { Text("Cart") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFFFC107)
                )
            )

            currentRoute == ScreenRoute.Profile.route -> CenterAlignedTopAppBar(
                title = { Text("My Account") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFFFC107)
                )
            )

            currentRoute?.startsWith(ScreenRoute.ProductsScreen.BASE_ROUTE) == true -> {
                CenterAlignedTopAppBar(
                    title = { Text("Products") },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFFFFC107)
                    ),
                    actions = {
                        IconButton(onClick = {
                            isShowFilterBarProductsScreen = !isShowFilterBarProductsScreen
                        }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Toggle Filter"
                            )
                        }
                    }
                )
            }

        }

    }, bottomBar = {
        if (currentRoute in bottomNavRoutes) {
            Box {
                BottomNavigationBar((navController))
            }
        }
    }, floatingActionButton = {
        if (currentRoute != null) {
            ExpandableFAB(currentRoute = currentRoute, onClothesClick = {
                onFilterClicked?.invoke("CLOTHES")
            }, onShoesClick = {
                onFilterClicked?.invoke("SHOES")
            })
        }


    }


    ) { contentPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination.value,
            modifier = Modifier.padding(contentPadding)
        ) {
            composable(route = ScreenRoute.Registration.route) {
                RegistrationScreen(navController)
            }
            composable(route = ScreenRoute.Login.route) {
                LoginScreen(navController)
            }
            composable(route = ScreenRoute.Home.route) {
                HomeScreen(navController)
            }
            composable(route = ScreenRoute.WishList.route) {
                WishScreen(navController)
            }
            composable(route = ScreenRoute.Collections.route) {
                CollectionsScreen(navController, setFilterMeth = {
                    onFilterClicked = it
                })
            }
            composable(route = ScreenRoute.Cart.route) {
                CartScreen(navController)
            }
            composable(route = ScreenRoute.Profile.route) {
                ProfileScreen(navController)
            }
            //with null
            composable(ScreenRoute.ProductsScreen.BASE_ROUTE) {
                ProductsScreen(
                    navController,
                    isFilterBarShown = isShowFilterBarProductsScreen,
                    collectionId = null
                )
            }
            //with value
            composable(
                route = ScreenRoute.ProductsScreen.FULL_ROUTE,
                arguments = listOf(
                    navArgument("collectionId") {
                        type = NavType.StringType  // Changed to StringType
                        nullable = true
                    }
                )
            ) { backStackEntry ->
                val collectionIdStr = backStackEntry.arguments?.getString("collectionId")
                val collectionId = collectionIdStr?.toLongOrNull()

                ProductsScreen(
                    navController,
                    isFilterBarShown = isShowFilterBarProductsScreen,
                    collectionId = collectionId,

                    )
            }
            composable<ScreenRoute.ProductInfo> {
                val args = it.toRoute<ScreenRoute.ProductInfo>()
                ProductInfoScreen(args.productId)
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
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                //Clothes (top)
                AnimatedVisibility(
                    visible = isExpanded, enter = slideInVertically(
                        initialOffsetY = { it }, animationSpec = tween(300)
                    ) + fadeIn(), exit = slideOutVertically(
                        targetOffsetY = { it }, animationSpec = tween(300)
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
                    visible = isExpanded, enter = slideInVertically(
                        initialOffsetY = { it }, animationSpec = tween(250)
                    ) + fadeIn(), exit = slideOutVertically(
                        targetOffsetY = { it }, animationSpec = tween(250)
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
