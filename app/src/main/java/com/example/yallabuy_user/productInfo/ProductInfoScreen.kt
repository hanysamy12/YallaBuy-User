package com.example.yallabuy_user.productInfo

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import com.example.yallabuy_user.data.models.productInfo.ProductInfoResponse
import com.example.yallabuy_user.utilities.ApiResponse
import org.koin.androidx.compose.koinViewModel
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.pager.HorizontalPagerIndicator
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.yallabuy_user.R
import com.example.yallabuy_user.authentication.login.CustomerIdPreferences
import com.example.yallabuy_user.authentication.login.LoginScreen
import com.example.yallabuy_user.wish.WishListIdPref

import com.example.yallabuy_user.cart.viewmodel.CartViewModel
import com.example.yallabuy_user.data.models.cart.Customer
import com.example.yallabuy_user.data.models.cart.DraftOrderCart
import com.example.yallabuy_user.data.models.cart.DraftOrderBody
import com.example.yallabuy_user.data.models.cart.LineItem
import com.example.yallabuy_user.data.models.cart.Property
import com.example.yallabuy_user.ui.navigation.ScreenRoute


@Composable
fun ProductInfoScreen(
    productId: Long,
    navController: NavHostController,
    productInfoViewModel: ProductInfoViewModel = koinViewModel(),
    cartViewModel: CartViewModel = koinViewModel()
) {

    val productInfo = productInfoViewModel.productInfo.collectAsState().value
    val showSignUpDialog = cartViewModel.showSignUpDialog.collectAsState()

    val context = LocalContext.current
    LaunchedEffect(productId) {
        Log.i("info", "ProductInfoScreen in the launchedEffect ")
        productInfoViewModel.getProductInfoById(productId)
        productInfoViewModel.isAlreadySaved(WishListIdPref.getWishListId(context), productId)
    }

    when (productInfo) {
        is ApiResponse.Failure -> {
            Log.i("TAG", "getProductInfoById error ")
        }

        ApiResponse.Loading -> {
            Log.i("TAG", "getProductInfoById loading ")
        }

        is ApiResponse.Success -> {
            Column {
                CompositionLocalProvider(LocalProductInfoViewModel provides productInfoViewModel
                    , LocalNavController provides navController) {
                    ProductImage(productInfo.data)
                }
                Spacer(Modifier.height(5.dp))
                // ProductDetail(productInfo.data)

                Column(modifier = Modifier.fillMaxSize()) {
                    //   ProductImage(productInfo.data)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(Modifier.height(5.dp))
                            ProductDetail(
                                productInfo.data,
                                onAddToCartClick = { draftOrder ->


                                    productInfoViewModel.getCustomerById(
                                        customerId = CustomerIdPreferences.getData(context),
                                        data = productInfo.data, isWishlist = false
                                    )
                                }
                            )
                        }
                    }
            }
            if (showSignUpDialog.value) {
                AlertDialog(
                    onDismissRequest = { cartViewModel.dismissSignUpDialog() },
                    title = { Text("Login Required") },
                    text = { Text("You need to sign up or log in to be able to add products to the cart.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                cartViewModel.dismissSignUpDialog()
                                navController.navigate(ScreenRoute.Login.route)
                            }
                        ) {
                            Text("Sign up")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { cartViewModel.dismissSignUpDialog() }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun ProductImage(data: ProductInfoResponse) {
    val images = data?.product?.images ?: emptyList()
    val pagerState = rememberPagerState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                HorizontalPager(
                    count = images.size,
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    AsyncImage(
                        model = images[page].src,
                        contentDescription = "Product Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Fit,
                        placeholder = painterResource(id = R.drawable.broken_image),
                        error = painterResource(id = R.drawable.broken_image)
                    )
                }

                HeartInCircle(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp), data
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalPagerIndicator(
            pagerState = pagerState,
            activeColor = Color.Black,
            inactiveColor = Color.LightGray,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 4.dp)
        )
    }
}

@Composable
fun ProductDetail(
    data: ProductInfoResponse,
    onAddToCartClick: (DraftOrderBody) -> Unit
) {


    var productCounter by remember { mutableIntStateOf(1) }
    val context = LocalContext.current

    var selectedSize by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("") }
    var showMissingSelectionDialog by remember { mutableStateOf(false) }
    Column {
        Text(
            data?.product?.title ?: "No Title Available",
            fontSize = 20.sp, fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(5.dp)
        )
        Text(
            "Description",
            Modifier.padding(5.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Text(
            data.product.body_html ?: "No Description Available",
            color = Color.Black,
            modifier = Modifier.padding(5.dp)
        )

        SizesDropDownMenu(data) { selectedSize = it }
        Spacer(modifier = Modifier.height(3.dp))
        ColorDropDownMenu(data) { selectedColor = it }

        Price(data, count = productCounter)

        Row {
            QuantitySelectorCard(
                productCounter = productCounter,
                onIncrease = { productCounter += 1 },
                onDecrease = { if (productCounter > 1) productCounter -= 1 }
            )
            Button(
                onClick = {

                    if (selectedSize.isEmpty() || selectedColor.isEmpty()) {
                        showMissingSelectionDialog = true
                    } else {
                        val selectedVariant = data.product.variants.find {
                            it.option1 == selectedSize && it.option2 == selectedColor
                        }
                        selectedVariant?.let { variant ->
                            val draftOrderCartObject = DraftOrderBody(
                                draftOrderCart = DraftOrderCart(
                                    id = 0L,
                                    lineItems = mutableListOf(
                                        LineItem(
                                            variantID = variant.id,
                                            productID = variant.product_id,
                                            title = data.product.title,
                                            quantity = productCounter,
                                            price = variant.price,
                                            properties = listOf(
                                                Property("Color", selectedColor),
                                                Property("Size", selectedSize),
                                                Property(
                                                    "Quantity_in_Stock",
                                                    variant.inventory_quantity.toString()
                                                ),
                                                Property("Image", data.product.image.src),
                                                Property("SavedAt", "Cart")
                                            )
                                        )
                                    ),
//                                        totalPrice = (variant.price.toDoubleOrNull()
//                                            ?: 0.0 * productCounter).toString(),


                                    customer = Customer(
                                        CustomerIdPreferences.getData(context)
                                        //  id = 8805732188478
                                    )
                                )
                            )

                            onAddToCartClick(draftOrderCartObject)
                        }
                    }
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(horizontal = 5.dp, vertical = 10.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text("Add To Cart", fontSize = 20.sp)
            }
        }
        if (showMissingSelectionDialog) {
            AlertDialog(
                onDismissRequest = { showMissingSelectionDialog = false },
                title = { Text("Selection Required") },
                text = { Text("Please select both size and color before adding to cart") },
                confirmButton = {
                    Button(
                        onClick = { showMissingSelectionDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text("OK", color = Color.White)
                    }
                }
            )
        }
    }
}

@Composable
fun QuantitySelectorCard(
    productCounter: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .padding(8.dp)
            .wrapContentSize()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = onDecrease,
                enabled = productCounter > 1
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Decrease",
                    tint = Color.Black
                )
            }

            Text(
                text = productCounter.toString(),
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            IconButton(onClick = onIncrease) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase",
                    tint = Color.Black
                )
            }
        }
    }
}

@Composable
fun SizesDropDownMenu(
    data: ProductInfoResponse,
    onSizeSelected: (String) -> Unit
) {
    val isDropDownSelected = remember { mutableStateOf(false) }
    val itemSelectedIndex = remember { mutableIntStateOf(0) }

    val variants = data.product.variants
    val selectedSize =
        variants.getOrNull(itemSelectedIndex.intValue)?.option1 ?: "No Sizes Available"

    LaunchedEffect(Unit) {
        onSizeSelected(selectedSize)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Select Size",
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                    .clickable { isDropDownSelected.value = true }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedSize,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Dropdown Arrow",
                    tint = Color.Black
                )
            }

            DropdownMenu(
                expanded = isDropDownSelected.value,
                onDismissRequest = { isDropDownSelected.value = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                variants.forEachIndexed { index, variant ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = variant.option1,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        onClick = {
                            Log.i("TAG", "SizesDropDownMenu: ${variant.option1}")
                            itemSelectedIndex.intValue = index
                            onSizeSelected(variant.option1)
                            isDropDownSelected.value = false

                        }
                    )
                }
            }
        }
    }

}

@Composable
fun ColorDropDownMenu(
    data: ProductInfoResponse,
    onColorSelected: (String) -> Unit
) {

    val isDropDownSelected = remember { mutableStateOf(false) }
    val itemSelectedIndex = remember { mutableIntStateOf(0) }

    // val variants = data?.product?.variants ?: emptyList() //I edited that

    val variants = data.product.variants
    val selectedColor =
        variants.getOrNull(itemSelectedIndex.intValue)?.option2 ?: "No Colors Available"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Select Color",
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                    .clickable { isDropDownSelected.value = true }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedColor,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Dropdown Arrow",
                    tint = Color.Black
                )
            }

            DropdownMenu(
                expanded = isDropDownSelected.value,
                onDismissRequest = { isDropDownSelected.value = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                variants.forEachIndexed { index, variant ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = variant.option2,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        onClick = {
                            Log.i("TAG", "ColorDropDownMenu: ${variant.option2}")
                            itemSelectedIndex.intValue = index
                            onColorSelected(variant.option2)
                            isDropDownSelected.value = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Price(data: ProductInfoResponse, count: Int) {
    var priceInBound = remember { mutableDoubleStateOf(0.0) }
    priceInBound.doubleValue =
        ((data?.product?.variants?.get(0)?.price?.toDoubleOrNull() ?: 0.0) * 50) * count
    Text(
        text = "  Total Price :  ${priceInBound.doubleValue}  EGP",
        modifier = Modifier.padding(10.dp),
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun HeartInCircle(
    modifier: Modifier = Modifier,
    data: ProductInfoResponse
) {
    val productInfoViewModel = LocalProductInfoViewModel.current
    val isHeartClicked = remember { mutableStateOf(false) }
    var icon = remember { Icons.Outlined.FavoriteBorder }
    val context = LocalContext.current
    val productIsSaved = productInfoViewModel.productIsAlreadySaved
    val isAlreadySaved = remember { mutableStateOf(false) }
    val isFirstProductInWishList =
        productInfoViewModel.isFirstProductInWishList.collectAsState().value
    val showDeleteProductAlert = remember { mutableStateOf(false) }

    val isNotGuest = remember { mutableStateOf(false) }
    val showGuestAlert = remember { mutableStateOf(false) }

    if (CustomerIdPreferences.getData(LocalContext.current) != 0L) {
        isNotGuest.value = true
    }

    LaunchedEffect(productIsSaved) {
        productIsSaved.collect {
            isAlreadySaved.value = it
        }
    }
    if (isAlreadySaved.value || isHeartClicked.value) {
        Log.i("saved", "isSaved ${isAlreadySaved.value} and isHeartClicked ${isHeartClicked.value} ")
        icon = Icons.Default.Favorite
    }
    Box(
        modifier = modifier
            .size(32.dp)
            .border(BorderStroke(1.dp, Color(0xFF3B9A94)), shape = CircleShape)
            .background(Color.White, shape = CircleShape)
            .clickable {
                if (isNotGuest.value) {
                    if (isAlreadySaved.value) {
                        showDeleteProductAlert.value = true
                    } else {
                        isHeartClicked.value = true
                        productInfoViewModel.getCustomerById(
                            CustomerIdPreferences.getData(context),
                            data, isWishlist = true
                        )
                    }
                } else {
                    showGuestAlert.value = true
                }
            },
        contentAlignment = Alignment.Center

    ) {

        Icon(
            imageVector = icon,
            contentDescription = "Heart Icon",
            modifier = Modifier.size(18.dp) ,
        )
    }

    if (showDeleteProductAlert.value) {
        DeleteProductAlertInProductInfo(
            onConfirmation = {
                showDeleteProductAlert.value = false
            },
            onDismissRequest = {
                showDeleteProductAlert.value = false
            }, data.product.title
        )
    }
    if (isFirstProductInWishList) {
        WishListIdPref.saveWishListID(
            LocalContext.current,
            productInfoViewModel.getWishListDraftOrderId()
        )
    }

    if (showGuestAlert.value) {
        GuestAlert(
            onConfirmation = {
                showGuestAlert.value = false
            },
            onDismissRequest = {
                showGuestAlert.value = false
            }
        )
    }
}

@Composable
fun DeleteProductAlertInProductInfo(
    onConfirmation: () -> Unit,
    onDismissRequest: () -> Unit,
    title: String?,

    ) {

    val productInfoViewModel = LocalProductInfoViewModel.current
    val failComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.questionmark))
    val context = LocalContext.current
    val customerId = CustomerIdPreferences.getData(context)
    val wishListId = WishListIdPref.getWishListId(context)
    val failProgress by animateLottieCompositionAsState(
        composition = failComposition,
        iterations = LottieConstants.IterateForever
    )
    val icon: @Composable () -> Unit = {
        LottieAnimation(
            composition = failComposition,
            progress = { failProgress },
            modifier = Modifier.size(100.dp)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Alert Dialog Box
        AlertDialog(
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismissRequest()
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Cancel")
                }
            },
            // set dismiss request
            onDismissRequest = {
                onDismissRequest()
            },
            // configure confirm button
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirmation()
                        productInfoViewModel.deleteProductFromWishList(
                            customerId,
                            title ?: "No title found" ,
                            wishListId
                        )
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Red
                    )
                ) {
                    Text("Delete")
                }
            },
            // set icon
            icon = icon,
            // set title text
            title = {
                Text(text = "Delete $title", color = Color.Black)
            },
            // set description text
            text = {
                Text(
                    text = "Are u sure u want to delete $title",
                    color = Color.DarkGray
                )
            },
            // set padding for contents inside the box
            modifier = Modifier.padding(16.dp),
            // define box shape
            shape = RoundedCornerShape(16.dp),
            // set box background color
            containerColor = Color.White,
            // set icon color
            iconContentColor = Color.Red,
            // set title text color
            titleContentColor = Color.Black,
            // set text color
            textContentColor = Color.DarkGray,
            // set elevation
            tonalElevation = 8.dp,
            // set properties
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            )
        )
    }
}

@Composable
fun GuestAlert(
    onConfirmation: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val failComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.warning_animation))
    val failProgress by animateLottieCompositionAsState(
        composition = failComposition,
        iterations = LottieConstants.IterateForever
    )
    val navController = LocalNavController.current

    val icon: @Composable () -> Unit = {
        LottieAnimation(
            composition = failComposition,
            progress = { failProgress },
            modifier = Modifier.size(100.dp)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Alert Dialog Box
        AlertDialog(
            // set dismiss request
            onDismissRequest = {
                onDismissRequest()
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismissRequest()
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Red
                    )
                ) {
                    Text("Close")
                }
            },
            // configure confirm button
            confirmButton = {
                TextButton(
                    onClick = {
                        navController.navigate(ScreenRoute.Login.route)
                        onConfirmation()
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Green
                    )
                ) {
                    Text("Login")
                }
            },
            // set icon
            icon = icon,
            // set title text
            title = {
                Text(text = "SignIn Required", color = Color.Black)
            },
            // set description text
            text = {
                Text(
                    text = "SignIn or SignUp to add Product to favorite",
                    color = Color.DarkGray
                )
            },
            // set padding for contents inside the box
            modifier = Modifier.padding(16.dp),
            // define box shape
            shape = RoundedCornerShape(16.dp),
            // set box background color
            containerColor = Color.White,
            // set icon color
            iconContentColor = Color.Red,
            // set title text color
            titleContentColor = Color.Black,
            // set text color
            textContentColor = Color.DarkGray,
            // set elevation
            tonalElevation = 8.dp,
            // set properties
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            )
        )
    }
}


