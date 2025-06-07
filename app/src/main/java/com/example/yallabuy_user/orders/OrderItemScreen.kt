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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.yallabuy_user.R
import com.example.yallabuy_user.data.models.LineItemsItem
import com.example.yallabuy_user.data.models.OrdersItem

private const val TAG = "OrderItemsScreen"
@Composable
fun OrderItemScreen(orderId: Long) {

//
//    Box {
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(6.dp)
//        ) {
//            Spacer(Modifier.height(12.dp))
//
//                    val products = ordersItem[0].lineItems
//                    Text(
//                        " "
//                    )
//                    LazyColumn(modifier = Modifier.fillMaxSize()) {
//                        products?.size?.let {
//                            items(it) { index ->
//                                OrderProductItem(
//                                    products[index],
//                                    onOrderClicked = { /*navController.navigate(ScreenRoute.PreviousOrders.route)*/}
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//
//@Composable
//private fun OrderProductItem(product :LineItemsItem?, onOrderClicked: (Long) -> Unit) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(100.dp)
//            .padding(4.dp),
//        horizontalArrangement = Arrangement.SpaceBetween,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        AsyncImage(
//            modifier = Modifier
//                .size(150.dp)
//                .background(shape = RoundedCornerShape(12.dp), color = Color.White),
//            model = R.drawable.dummy_product,
//            contentDescription = ""
//        )
//        Spacer(Modifier.width(12.dp))
//        Column {
//            Text("${product?.name}", fontWeight = FontWeight.Bold)
//            Text("${product?.price}")
//        }
//        IconButton(
//            modifier = Modifier
//                .width(100.dp)
//                .padding(4.dp),
//            onClick = {
//                product?.id?.let { onOrderClicked(it) }
//                Log.i(TAG, "OrderItem: Clicked") },
//        ) {
//            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "")
//        }
//
//    }
}

