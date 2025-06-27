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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import coil.compose.AsyncImage
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
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
@Composable
fun MainScreen() {
    var onFilterClicked: ((String) -> Unit)? by remember { mutableStateOf(null) }
    val startDestination = remember { mutableStateOf(ScreenRoute.Registration.route) }
    val context = LocalContext.current

    val customerId = CustomerIdPreferences.getData(context)
    if (customerId != 0L) {
        startDestination.value = ScreenRoute.Home.route
    }


    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val topBarContent = remember { mutableStateOf<@Composable () -> Unit>({}) }
    val snackBar = remember { SnackbarHostState() }

    val bottomNavRoutes = listOf(
        ScreenRoute.Home.route,
        ScreenRoute.Collections.route,
        ScreenRoute.WishList.route,
        ScreenRoute.Cart.route,
        ScreenRoute.Profile.route
    )
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackBar)
        }, topBar = {
            Log.i(TAG, "MainScreen: CurrentRoute  $currentRoute")
            topBarContent.value()

        }, bottomBar = {
            if (currentRoute in bottomNavRoutes) {
                Box {
                    BottomNavigationBar((navController))
                }
            }
        }, floatingActionButton = {
            if (currentRoute != null) {
                ExpandableFAB(currentRoute = currentRoute, onClothesClick = {
                    onFilterClicked?.invoke("T-SHIRTS")
                }, onShoesClick = {
                    onFilterClicked?.invoke("SHOES")
                }, onAccessoryClick = {
                    onFilterClicked?.invoke("ACCESSORIES")
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
                RegistrationScreen(navController, setTopBar = { topBarContent.value = it })
            }
            composable(route = ScreenRoute.Login.route) {
                LoginScreen(navController, setTopBar = { topBarContent.value = it })
            }
            composable(route = ScreenRoute.Home.route) {
                HomeScreen(navController, setTopBar = { topBarContent.value = it })
            }
            composable(route = ScreenRoute.WishList.route) {
                WishScreen(navController, setTopBar = { topBarContent.value = it })
            }
            composable(route = ScreenRoute.Collections.route) {
                CollectionsScreen(navController, setFilterMeth = {
                    onFilterClicked = it
                }, setTopBar = { topBarContent.value = it })
            }
            composable(route = ScreenRoute.Cart.route) {
                CartScreen(navController, setTopBar = { topBarContent.value = it })
            }
            composable(route = ScreenRoute.Profile.route) {
                ProfileScreen(navController, setTopBar = { topBarContent.value = it })
            }

            composable(ScreenRoute.Settings.route) {
                SettingsScreen(navController, setTopBar = { topBarContent.value = it })
            }
            composable(ScreenRoute.AboutUs.route) {
                AboutUsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    setTopBar = { topBarContent.value = it })
            }
            composable(ScreenRoute.ContactUs.route) {
                ContactUsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    setTopBar = { topBarContent.value = it })
            }
            composable(ScreenRoute.Currency.route) {
                CurrencyScreen(
                    onNavigateBack = { navController.popBackStack() },
                    setTopBar = { topBarContent.value = it })
            }
            composable(ScreenRoute.Address.route) {
                val context = LocalContext.current

                AddressScreen(//viewModel = viewModel
                    customerId = CustomerIdPreferences.getData(context), //8805732188478,
                    onNavigateBack = { navController.popBackStack() }, onNavigateToMap = {
                        navController.navigate(ScreenRoute.Map.route)
                    }, setTopBar = { topBarContent.value = it })
            }

            composable(ScreenRoute.Map.route) {
                val context = LocalContext.current
                val activity = LocalActivity.current as ComponentActivity
                val locationPermissionManager =
                    remember { LocationPermissionManager(context, activity) }

                MapLocationScreen(
                    locationPermissionManager = locationPermissionManager,
                    navController = navController,
                    onNavigateBack = { navController.popBackStack() },
                    setTopBar = { topBarContent.value = it })
            }

            //question about that
            composable(
                route = "address_form?addressId={addressId}&fullAddress={fullAddress}&city={city}&country={country}",
                arguments = listOf(navArgument("addressId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }, navArgument("fullAddress") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }, navArgument("city") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }, navArgument("country") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
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



            composable<ScreenRoute.ProductsScreen> {
                val args = it.toRoute<ScreenRoute.ProductsScreen>()
                ProductsScreen(
                    navController,
                    vendorName = args.vendorName,
                    categoryID = args.categoryID,
                    setTopBar = { topBarContent.value = it },
                    title = args.title,
                )
            }
            composable<ScreenRoute.ProductInfo> {
                val args = it.toRoute<ScreenRoute.ProductInfo>()
                ProductInfoScreen(
                    args.productId,
                    navController,
                    setTopBar = { topBarContent.value = it }, snackbarHostState = snackBar)
            }
            composable(ScreenRoute.PreviousOrders.route) {
                PreviousOrdersScreen(
                    navController,
                    setTopBar = { topBarContent.value = it })
            }
            composable<ScreenRoute.PreviousOrderDetails> {
                val args = it.toRoute<ScreenRoute.PreviousOrderDetails>()
                OrderItemScreen(
                    orderId = args.orderId,
                    navController = navController,
                    setTopBar = { topBarContent.value = it },
                    title = null
                )
            }

            composable<ScreenRoute.OrderCheckOut> {
                val args = it.toRoute<ScreenRoute.OrderCheckOut>()
                OrderCheckoutScreen(
                    cartId = args.orderId,
                    passedTotalPrice = args.totalAmount,
                    setTopBar = { topBarContent.value = it },
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun ExpandableFAB(
    currentRoute: String,
    onClothesClick: () -> Unit,
    onShoesClick: () -> Unit,
    onAccessoryClick: () -> Unit
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
                //Accessory (top)
                AnimatedVisibility(
                    visible = isExpanded, enter = slideInVertically(
                        initialOffsetY = { it }, animationSpec = tween(350)
                    ) + fadeIn(), exit = slideOutVertically(
                        targetOffsetY = { it }, animationSpec = tween(350)
                    ) + fadeOut()
                ) {
                    FloatingActionButton(onClick = {
                        onAccessoryClick()
                        isExpanded = false
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_ring),
                            contentDescription = "Accessories",
                            modifier = Modifier.size(32.dp),
                            tint = Color.Unspecified
                        )
                    }
                }
                //Clothes (middle)
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

//@Preview
@Composable
fun NOInternetScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val successComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.no_internet))
        val successProgress by animateLottieCompositionAsState(
            composition = successComposition,
            iterations = LottieConstants.IterateForever
        )
        val tealColor = Color(0xFF26A69A)
        val dynamicProperties = rememberLottieDynamicProperties(
            rememberLottieDynamicProperty(
                property = LottieProperty.COLOR,
                value = tealColor.toArgb(), // important: use ARGB format
                keyPath = arrayOf("**", "Fill 1") // adjust this to match your JSON structure
            )
        )
        AsyncImage(
            model = R.drawable.ic_app,
            contentDescription = "App Icon",
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            LottieAnimation(
                composition = successComposition,
                progress = { successProgress },
                dynamicProperties = dynamicProperties,
                modifier = Modifier.size(100.dp)
            )
            Text(text = "No Internet", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Please Check Your Connection", fontSize = 20.sp, color = Color.Gray)
        }
    }
}