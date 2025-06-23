package com.example.yallabuy_user.cart.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.yallabuy_user.R
import com.example.yallabuy_user.authentication.login.CustomerIdPreferences
import com.example.yallabuy_user.cart.viewmodel.CartViewModel
import com.example.yallabuy_user.home.HomeViewModel
import com.example.yallabuy_user.ui.navigation.ScreenRoute
import com.example.yallabuy_user.utilities.ApiResponse
import com.example.yallabuy_user.utilities.Common
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CartScreen(
    navController: NavController,
    cartViewModel: CartViewModel = koinViewModel(), setTopBar: (@Composable () -> Unit) -> Unit
) {
    val draftOrdersState by cartViewModel.draftOrders.collectAsState()
    val showOutOfStockDialog by cartViewModel.showOutOfStockDialog.collectAsState()
    val context = LocalContext.current
    val customerId = CustomerIdPreferences.getData(context)
    val currencySymbol = Common.currencyCode.getCurrencyCode()

    LaunchedEffect(draftOrdersState) {
        val draftOrders = (draftOrdersState as? ApiResponse.Success)?.data?.draftOrderCarts ?: return@LaunchedEffect
        cartViewModel.convertItemPrices(draftOrders)
    }


    LaunchedEffect(Unit) {
        if (customerId != 0L) {
            cartViewModel.getCustomerByIdAndFetchCart(customerId)
        }
        setTopBar {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Cart", color = Color.White,
                        fontFamily = FontFamily(Font(R.font.caprasimo_regular)),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorResource(R.color.teal_80)
                ),
                navigationIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_app),
                        contentDescription = "App Icon",
                        tint = Color.Unspecified, // Optional: set tint if needed
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            )
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        when (val state = draftOrdersState) {
            is ApiResponse.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ApiResponse.Failure -> {
                Text(
                    text = "Failed to load cart: ${state.error}",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }

            is ApiResponse.Success -> {
                val draftOrders = state.data.draftOrderCarts
                LaunchedEffect(Unit) {
                    cartViewModel.convertItemPrices(draftOrders)
                }
                if (draftOrders.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painterResource(R.drawable.empty_cart),
                            contentDescription = "logo",
                            modifier = Modifier.size(250.dp)
                        )
                    }
                } else {
                    val draftOrderId = draftOrders.first().id
                    val allLineItems = draftOrders.flatMap { it.lineItems }
                    val totalPrice = allLineItems.sumOf {
                        val converted = cartViewModel.convertedPrices[it.variantID]
                        converted?.toDoubleOrNull() ?: it.getTotalPrice().toDouble()
                    }
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .height(180.dp)
                            .padding(bottom = 8.dp)
                    ) {
                        items(draftOrders) { draftOrder ->
                            draftOrder.lineItems.forEach { item ->
                                CartItemCard(
                                    draftOrderId = draftOrder.id ?: -1L,
                                    variantId = item.variantID,
                                    title = item.title,
                                    price = cartViewModel.convertedPrices[item.variantID]
                                        ?: item.price,
                                    currencySymbol = Common.currencyCode.getCurrencyCode(),
                                    quantity = item.quantity.toInt(),
                                    imageUrl = item.properties.find {
                                        it.name.lowercase() == "image"
                                    }?.value ?: "",
                                    onIncrease = {
                                        cartViewModel.increaseItemQuantity(
                                            draftOrder.id ?: -1L,
                                            item.variantID
                                        )
                                    },
                                    onDecrease = {
                                        cartViewModel.decreaseItemQuantity(
                                            draftOrder.id ?: -1L,
                                            item.variantID
                                        )
                                    },
                                    onDelete = {
                                        cartViewModel.removeItemFromCart(
                                            draftOrder.id ?: -1L,
                                            item.variantID,
                                            customerId
                                        )
                                    }
                                )
                            }
                        }
                    }

                    CheckoutSection(
                        total = "$currencySymbol ${"%.2f".format(totalPrice)} ",
                        onCheckOutClicked = {
                            if (draftOrderId != null)
                                navController.navigate(
                                    ScreenRoute.OrderCheckOut(
                                        draftOrderId,
                                        totalPrice
                                    )
                                )
                        },
                        itemCount = allLineItems.size
                    )

                }
            }
        }
        if (showOutOfStockDialog) {
            AlertDialog(
                onDismissRequest = { cartViewModel.dismissOutOfStockDialog() },
                title = { Text("Out of Stock") },
                text = { Text("Sorry:(\nThe quantity requested exceeds the available stock.") },
                confirmButton = {
                    TextButton(
                        onClick = { cartViewModel.dismissOutOfStockDialog() }
                    ) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
fun CartItemCard(
    draftOrderId: Long,
    variantId: Long,
    title: String,
    price: String,
    quantity: Int,
    imageUrl: String,
    currencySymbol: String,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onDelete: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Delete Item") },
            text = { Text("Are you sure you want to delete this item?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        onDelete()
                    }
                ) {
                    Text("Yes", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(180.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Product Image",
                modifier = Modifier
                    .size(90.dp)
                    .padding(8.dp)
                    .clip(CircleShape)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Quantity: $quantity",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Price: $currencySymbol $price",
                    fontSize = 12.sp,
                    color = Color.Black
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                QuantitySelector(
                    quantity = quantity,
                    onIncrease = onIncrease,
                    onDecrease = onDecrease
                )
                Spacer(modifier = Modifier.height(8.dp))
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Item",
                    tint = colorResource(R.color.dark_turquoise),
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { showDialog = true }
                        .padding(top = 4.dp)
                )
            }
        }
    }
}


@Composable
fun QuantitySelector(
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(color = Color(0xFF3B9A94), RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "-",
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier
                .clickable(enabled = quantity > 1) { onDecrease() }
                .padding(8.dp)
        )

        Text(
            text = quantity.toString(),
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Text(
            text = "+",
            color = Color.White,
            fontSize = 18.sp,
            modifier = Modifier
                .clickable { onIncrease() }
                .padding(8.dp)
        )
    }
}

@Composable
fun CheckoutSection(total: String, itemCount: Int, onCheckOutClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Total ($itemCount ${if (itemCount == 1) "item" else "items"})", fontSize = 14.sp)
            Text(total, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Button(
            onClick = { onCheckOutClicked() },
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.teal_80)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Checkout", color = Color.White)
        }
    }
}

