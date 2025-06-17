package com.example.yallabuy_user.orders

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.yallabuy_user.data.models.LineItemsItem
import com.example.yallabuy_user.home.ProgressShow
import com.example.yallabuy_user.ui.navigation.ScreenRoute
import com.example.yallabuy_user.utilities.ApiResponse
import org.koin.androidx.compose.koinViewModel

private const val TAG = "OrderItemsScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderItemScreen(
    orderId: Long?,
    navController: NavController,
    viewModel: OrdersViewModel = koinViewModel(),
    setTopBar: (@Composable () -> Unit) -> Unit,
    title: String?
) {

    val uiOrderState by viewModel.orderProducts.collectAsState()

    LaunchedEffect(Unit) {
        setTopBar {
            CenterAlignedTopAppBar(
                title = { Text(title?: "Order Details") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF3B9A94)
                )
            )
        }
        viewModel.getOrderById(orderId)
    }
    Box {
        when (uiOrderState) {
            is ApiResponse.Failure -> {
                val failure = (uiOrderState as ApiResponse.Failure)
                Text(failure.toString())
                Log.i(TAG, "OrderItemScreen: $failure")
            }

            ApiResponse.Loading -> {
                ProgressShow()
            }

            is ApiResponse.Success<*> -> {
                val ordersItem = (uiOrderState as ApiResponse.Success).data

                val products = ordersItem.lineItems
                val currentCode = ordersItem.currency
                Column {

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        products?.size?.let {
                            items(it) { index ->
                                OrderProductItem(
                                    products[index],
                                    currentCode = currentCode,
                                    onOrderClicked = { productId ->
                                        navController.navigate(
                                            ScreenRoute.ProductInfo(productId),
                                        )
                                    }
                                )
                                HorizontalDivider()
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Price")
                        Text("${ordersItem.currency} ${ordersItem.totalPrice ?: "UnKnown"}")
                    }
                    HorizontalDivider()

//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(10.dp),
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        Text("Shipped To:")
//                        Text("${ordersItem.shippingAddress?.country ?: ""}, ${ordersItem.shippingAddress?.city ?: ""}")
//                    }
                }
            }
        }

    }

}

@Composable
private fun OrderProductItem(
    product: LineItemsItem?,
    currentCode: String?,
    onOrderClicked: (Long) -> Unit
) {
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
                .shadow(
                    elevation = 3.dp,
                    shape = RoundedCornerShape(12.dp),
                    clip = true
                )
                .background(shape = RoundedCornerShape(12.dp), color = Color.White),
            model = product?.imgUrl,
            contentDescription = ""
        )
        Spacer(Modifier.width(12.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            Text(
                text = "${product?.name}",
                fontWeight = FontWeight.Medium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Text("${product?.price} $currentCode")
        }
        IconButton(
            modifier = Modifier
                .padding(4.dp),
            onClick = {
                product?.productId?.let { onOrderClicked(it) }
            },
        ) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "show product details"
            )
        }

    }
}


