package com.example.yallabuy_user.ui.navigation

import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
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
import com.example.yallabuy_user.cart.view.CartScreen
import com.example.yallabuy_user.collections.CollectionsScreen
import com.example.yallabuy_user.home.HomeScreen
import com.example.yallabuy_user.orders.OrderCheckoutScreen
import com.example.yallabuy_user.orders.OrderItemScreen
import com.example.yallabuy_user.orders.PreviousOrdersScreen
import com.example.yallabuy_user.payment.view.PaymentScreen
import com.example.yallabuy_user.productInfo.ProductInfoScreen
import com.example.yallabuy_user.products.ProductsScreen
import com.example.yallabuy_user.profile.ProfileScreen
import com.example.yallabuy_user.settings.view.AddressFormScreen
import com.example.yallabuy_user.settings.view.AddressScreen
import com.example.yallabuy_user.settings.view.CurrencyScreen
import com.example.yallabuy_user.settings.view.MapLocationScreen
import com.example.yallabuy_user.utilities.LocationPermissionManager
import com.example.yallabuy_user.wish.WishScreen
import com.mariammuhammad.yallabuy.View.Settings.AboutUsScreen
import com.mariammuhammad.yallabuy.View.Settings.ContactUsScreen
import com.mariammuhammad.yallabuy.View.Settings.SettingsScreen


private const val TAG = "MainScreen"

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
                    containerColor = Color(0xFF3B9A94)
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
                    containerColor = Color(0xFF3B9A94)
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

            currentRoute == ScreenRoute.PreviousOrders.route -> CenterAlignedTopAppBar(
                title = { Text("Previous Orders") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFFFC107)
                )
            )
            ////change order ID !!!
            currentRoute == ScreenRoute.PreviousOrderDetails(22).route -> CenterAlignedTopAppBar(
                title = { Text("Order Details") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFFFC107)
                )
            )

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

            composable(ScreenRoute.Settings.route){
                SettingsScreen(navController)
            }
            composable(ScreenRoute.AboutUs.route) {
                AboutUsScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable(ScreenRoute.ContactUs.route) {
                ContactUsScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable(ScreenRoute.Currency.route) {
                CurrencyScreen(
                    onNavigateBack = { navController.popBackStack() })
            }
            composable(ScreenRoute.Address.route) {
                val context = LocalContext.current

                AddressScreen(//viewModel = viewModel
                    customerId = CustomerIdPreferences.getData(context), //8805732188478,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToMap = {
                        navController.navigate(ScreenRoute.Map.route)
                    }
                )
            }

            composable(ScreenRoute.Map.route){
                val context= LocalContext.current
                val activity = LocalActivity.current as ComponentActivity
                    val locationPermissionManager = remember { LocationPermissionManager(context, activity) }

                    MapLocationScreen(
                        locationPermissionManager = locationPermissionManager,
                        navController = navController,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

            //question about that
            composable(
                route = "address_form?addressId={addressId}&fullAddress={fullAddress}&city={city}&country={country}",
                arguments = listOf(
                    navArgument("addressId") {
                        type = NavType.LongType
                        defaultValue = 0L
                    },
                    navArgument("fullAddress") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument("city") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument("country") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                AddressFormScreen(
                    navController = navController,
                    addressId = backStackEntry.arguments?.getLong("addressId") ?: 0L,
                    fullAddress = backStackEntry.arguments?.getString("fullAddress"),
                    city = backStackEntry.arguments?.getString("city"),
                    country = backStackEntry.arguments?.getString("country")
                )
            }

            composable<ScreenRoute.Payment> { backStackEntry ->
                val args = backStackEntry.toRoute<ScreenRoute.Payment>()
                PaymentScreen(
                    navController = navController,
                    totalPrice = args.total
                )
            }

            //with null
            composable(ScreenRoute.ProductsScreen.BASE_ROUTE) {
                ProductsScreen(
                    navController,
                    isFilterBarShown = isShowFilterBarProductsScreen,
                    vendorName = null,
                    categoryID = null
                )
            }

            //with value
            composable(
                route = ScreenRoute.ProductsScreen.FULL_ROUTE
            ) { backStackEntry ->
                val vendorName = backStackEntry.arguments?.getString("vendorName")
                val categoryIDString = backStackEntry.arguments?.getString("categoryID")
                val categoryID = categoryIDString?.toLongOrNull()
                ProductsScreen(
                    navController,
                    isFilterBarShown = isShowFilterBarProductsScreen,
                    vendorName = vendorName,
                    categoryID = categoryID
                )
            }
            composable<ScreenRoute.ProductInfo> {
                val args = it.toRoute<ScreenRoute.ProductInfo>()
                ProductInfoScreen(args.productId, navController)
            }
            composable(ScreenRoute.PreviousOrders.route) {
                PreviousOrdersScreen(navController)
            }
            composable(route = ScreenRoute.PreviousOrderDetails.FULL_ROUTE) { navBackStackEntry ->
                val orderId = navBackStackEntry.arguments?.getString("orderId")?.toLongOrNull()
                OrderItemScreen(orderId, navController)
            }
            composable<ScreenRoute.OrderCheckOut>{
                val args = it.toRoute<ScreenRoute.OrderCheckOut>()
                OrderCheckoutScreen(cartId = args.orderId ?: 0L)
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
