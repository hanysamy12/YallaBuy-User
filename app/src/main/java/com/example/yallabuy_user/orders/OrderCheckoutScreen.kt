package com.example.yallabuy_user.orders

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yallabuy_user.data.models.cart.LineItem
import com.example.yallabuy_user.home.ProgressShow
import com.example.yallabuy_user.utilities.ApiResponse
import org.koin.androidx.compose.koinViewModel

private const val TAG = "OrderCheckoutScreen"

@Composable
fun OrderCheckoutScreen(viewModel: NewOrderViewModel = koinViewModel(), cartId: Long) {

    val uiCartOrderState = viewModel.cartOrder.collectAsState()
    var couponCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        viewModel.getDraftOrder(cartId)
    }

    when (uiCartOrderState.value) {
        is ApiResponse.Failure -> {
            Text((uiCartOrderState.value as ApiResponse.Failure).toString())
        }

        ApiResponse.Loading -> {
            ProgressShow()
        }

        is ApiResponse.Success<*> -> {
            val order = (uiCartOrderState.value as ApiResponse.Success).data
            val currency = order.currency
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Order Items")
                }
                Column {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp)
                    ) {
                        items(order.lineItems.size) { index ->
                            Log.i(TAG, "OrderCheckoutScreen: ${order.lineItems.size}")
                            OrderCheckoutItem(lineItem = order.lineItems[index], currency)
                            HorizontalDivider()
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Price", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "${order.totalPrice} $currency",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    HorizontalDivider()

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Coupons", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CouponTextField(
                            value = couponCode,
                            onValueChange = { couponCode = it },
                            errorMessage = errorMessage
                        )

                        Button(
                            onClick = {
                                if (couponCode.isBlank()) {
                                    errorMessage = "Coupon code cannot be empty"
                                } else {
                                    errorMessage = ""
                                    Log.i(TAG, "OrderCheckoutScreen: $couponCode")
                                    // call verification
                                }
                            },
                            modifier = Modifier, shape = RoundedCornerShape(8.dp)

                        ) {
                            Text("Verify")
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Cost", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "${order.totalPrice} $currency",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun OrderCheckoutItem(lineItem: LineItem, currency: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(lineItem.title, fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Quantity")
            Spacer(modifier = Modifier.width(8.dp))
            Text(lineItem.quantity.toString(), fontSize = 22.sp)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Price")
            Spacer(modifier = Modifier.width(8.dp))
            Text("${lineItem.price} $currency")
        }
    }
}

@Composable
fun CouponTextField(
    value: String,
    onValueChange: (String) -> Unit,
    errorMessage: String = "",
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Coupon Code") },
        placeholder = { Text("Enter your coupon") },
        singleLine = true,
        isError = errorMessage.isNotEmpty(),
        modifier = Modifier
            .width(200.dp)
            .background(Color.White, shape = RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
    )

}
