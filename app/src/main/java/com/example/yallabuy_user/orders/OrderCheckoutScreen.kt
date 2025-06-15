package com.example.yallabuy_user.orders

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yallabuy_user.data.models.CreateLineItem
import com.example.yallabuy_user.data.models.CreateShippingAddress
import com.example.yallabuy_user.data.models.cart.LineItem
import com.example.yallabuy_user.data.models.settings.Address
import com.example.yallabuy_user.home.ProgressShow
import com.example.yallabuy_user.utilities.ApiResponse
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

private const val TAG = "OrderCheckoutScreen"

@Composable
fun OrderCheckoutScreen(viewModel: NewOrderViewModel = koinViewModel(), cartId: Long) {

    val context = LocalContext.current
    val uiCartOrderState = viewModel.cartOrder.collectAsState()
    val uiAddressState = viewModel.address.collectAsState()
    //data
    var errorMessage by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        viewModel.getDraftOrder(cartId)
        viewModel.getCustomerAddress(context)
    }

    when (uiCartOrderState.value) {
        is ApiResponse.Failure -> {
            Text((uiCartOrderState.value as ApiResponse.Failure).toString())
        }

        ApiResponse.Loading -> {
            ProgressShow()
        }

        is ApiResponse.Success -> {
            val order = (uiCartOrderState.value as ApiResponse.Success).data
            var createLineItems: List<CreateLineItem> = listOf()
            var couponCode by remember { mutableStateOf("") }
            var shippingAddress =  CreateShippingAddress(id = -1L)
            var totalCost by remember { mutableStateOf("") }
            var isCash by remember { mutableStateOf(true) }
            var getWay by remember { mutableStateOf("") }
            var currency by remember { mutableStateOf("EGP") }
            var paymentStatus by remember { mutableStateOf("pending") }
            var totalPayment by remember { mutableStateOf("") }

            createLineItems = mapLineItemToCreateLineItem(order.lineItems)
            currency = order.currency ?: "EGP"
            totalCost = order.totalPrice ?: "-1"
            getWay = if (isCash) "cash" else "online" // Name of Provider
            paymentStatus = if (isCash) "pending" else "paid"
            totalPayment = if (!isCash) totalCost else "00.01"
            Column(
                modifier = Modifier
                    //.fillMaxWidth()
                    .fillMaxSize()
                    .padding(vertical = 12.dp, horizontal = 6.dp)
                    .verticalScroll(rememberScrollState())
            ) {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.Center,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text("Order Items")
//                }
//                LazyColumn(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(6.dp)
//                ) {
//                    items(order.lineItems.size) { index ->
//                        Log.i(TAG, "OrderCheckoutScreen: ${order.lineItems.size}")
//                        OrderCheckoutItem(lineItem = order.lineItems[index], currency ?: "$")
//                        HorizontalDivider()
//                    }
//                }
                order.lineItems.forEach { lineItem ->
                    OrderCheckoutItem(lineItem = lineItem, currency = currency ?: "$")
                    HorizontalDivider()
                }

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
                        }, modifier = Modifier, shape = RoundedCornerShape(8.dp)

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
                HorizontalDivider()
                Spacer(modifier = Modifier.height(10.dp))
                Text("Payment Options", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isCash, onClick = {
                                isCash = !isCash
                            })
                        Text("Cash")
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = !isCash, onClick = {
                                isCash = !isCash
                            })
                        Text("Online")
                    }

                }
                HorizontalDivider()
                Spacer(modifier = Modifier.height(10.dp))
                Text("Shipping Address", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                when (uiAddressState.value) {
                    is ApiResponse.Failure -> {
                        Text((uiAddressState.value as ApiResponse.Failure).toString())
                        shippingAddress = CreateShippingAddress(id = -1L)
                    }

                    ApiResponse.Loading -> {
                        shippingAddress = CreateShippingAddress(id = -1L)
                    }

                    is ApiResponse.Success -> {
                        val addresses = (uiAddressState.value as ApiResponse.Success).data
                        Log.i(TAG, "OrderCheckoutScreen: $addresses")
                        shippingAddress = CreateShippingAddress(id = addresses.first().id)
                        DropdownField(
                            label = "Select Address",
                            selected = addresses.first().city,
                            options = addresses
                        ) {
                            shippingAddress = CreateShippingAddress(id = it)
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(10F))
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

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.postNewOrder(
                                items = createLineItems,
                                discountCode = couponCode,
                                shippingAddress = shippingAddress ,
                                amount = totalPayment,
                                getWay = getWay,
                                financialStatus = paymentStatus,
                                context = context
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3B9A94), contentColor = Color.White
                    )
                ) {
                    Text(
                        "CheckOut",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
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

@Composable
fun DropdownField(label: String, selected: String, options: List<Address>, onSelected: (Long) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(selected) }

    Column {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    Modifier.clickable { expanded = true })
            },
            modifier = Modifier.fillMaxWidth()
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach {address->
                DropdownMenuItem(onClick = {
                    onSelected(address.id)
                    expanded = false
                    selectedOption = address.fullAddress
                }, text = { Text(address.fullAddress.ifEmpty { "Un Named Address" }) })
            }
        }
    }
}
private fun mapLineItemToCreateLineItem(lineItems: List<LineItem>): List<CreateLineItem> {
    return lineItems.map { lineItem ->
        CreateLineItem(variantId = lineItem.variantID, quantity = lineItem.quantity)
    }
}
