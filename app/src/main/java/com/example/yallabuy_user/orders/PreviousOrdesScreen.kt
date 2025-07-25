package com.example.yallabuy_user.orders

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.yallabuy_user.R
import com.example.yallabuy_user.data.models.OrdersItem
import com.example.yallabuy_user.home.ProgressShow
import com.example.yallabuy_user.ui.navigation.ScreenRoute
import com.example.yallabuy_user.utilities.ApiResponse
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviousOrdersScreen(
    navController: NavController, orderViewModel: OrdersViewModel = koinViewModel(),
    setTopBar: (@Composable () -> Unit) -> Unit
) {

    val uiOrdersState by orderViewModel.orders.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        setTopBar {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Latest Orders", color = Color.White,
                        fontFamily = FontFamily(Font(R.font.caprasimo_regular)),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF3B9A94)
                ),
                navigationIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Back"

                            )
                        }
                        Icon(
                            painter = painterResource(id = R.drawable.ic_app),
                            contentDescription = "App Icon",
                            tint = Color.Unspecified,
                            //modifier = Modifier.padding(start = 5.dp)
                        )
                    }
                }
            )
        }
        orderViewModel.getPreviousOrders(context)
    }
    Box {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
        ) {
            Spacer(Modifier.height(12.dp))
            when (uiOrdersState) {
                is ApiResponse.Failure -> {
                    Text((uiOrdersState as ApiResponse.Failure).toString())
                }

                ApiResponse.Loading -> {
                    ProgressShow()
                }

                is ApiResponse.Success<*> -> {
                    val orders = (uiOrdersState as ApiResponse.Success).data
                    if (orders.isNotEmpty()) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(orders.size) { index ->
                                OrderItem(
                                    orders[index],
                                    onOrderClicked = {
                                        navController.navigate(
                                            ScreenRoute.PreviousOrderDetails(orderId = it)
                                        )
                                    },
                                )
                            }
                        }
                    } else {
                        Column(
                            Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AsyncImage(
                                modifier = Modifier
                                    .size(150.dp)
                                    .background(shape = RoundedCornerShape(12.dp), color = Color.White),
                                model = R.drawable.empty_cart,
                                contentDescription = ""
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderItem(order: OrdersItem, onOrderClicked: (Long) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            modifier = Modifier
                .size(150.dp)
                .background(shape = RoundedCornerShape(12.dp), color = Color.White),
            model = R.drawable.ic_app,
            contentDescription = ""
        )
        Spacer(Modifier.width(12.dp))
        Column {
            val date = order.createdAt?.split("T")?.get(0)
            Text("Date $date", fontWeight = FontWeight.Bold)
            Text("Total ${order.currentTotalPrice} ${order.currency}")
        }
        IconButton(
            modifier = Modifier
                .width(100.dp)
                .padding(4.dp),
            onClick = {
                order.id?.let { onOrderClicked(it) }
            },
        ) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "")
        }

    }
}

