package com.example.yallabuy_user.orders

import android.util.Log
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.yallabuy_user.R
import com.example.yallabuy_user.home.ProgressShow
import com.example.yallabuy_user.utilities.ApiResponse
import org.koin.androidx.compose.koinViewModel

private const val TAG = "PreviousOrdersScreen"

@Preview
@Composable
fun PreviousOrdersScreen(orderViewModel: OrdersViewModel = koinViewModel()) {
    // Number of orders to display
    // Number of Items in each order
    // Order date

    val uiOrdersState by orderViewModel.orders.collectAsState()

    LaunchedEffect(Unit) {
        orderViewModel.getPreviousOrders(8792449548606)
    }
    Box {

        Column(modifier = Modifier.fillMaxSize().padding(6.dp)) {
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
                    Text(
                        "${orders[0].customer?.firstName}${orders[0].customer?.lastName} "
                    )
                    LazyColumn (modifier = Modifier.fillMaxSize()){
                        items(orders.size) { _ ->
                            OrderItem()
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun OrderItem() {
    Row(
        modifier = Modifier
            //.fillMaxWidth()
            .height(100.dp)
            .padding(4.dp), horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(modifier = Modifier.size(150.dp).background(shape = RoundedCornerShape(12.dp), color = Color.White), model =  R.drawable.dummy_product, contentDescription = "")
        Column {
            Text("Order Date", fontWeight = FontWeight.Bold)
            Text("Total 200.00 EGP")
        }
        IconButton(modifier = Modifier.width(100.dp)
            .padding(4.dp),
            onClick = { Log.i(TAG, "OrderItem: Clicked") },
        ) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "")
        }

        LazyColumn {
            items(3) {
               // ProductOrderItem()
            }
        }

    }
}

@Preview
@Composable
private fun ProductOrderItem() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(4.dp), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AsyncImage(model = R.drawable.dummy_product, contentDescription = "")
        Text("Product Name")
    }
}