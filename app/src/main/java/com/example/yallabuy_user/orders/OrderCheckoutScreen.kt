package com.example.yallabuy_user.orders

import android.app.Activity
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yallabuy_user.data.models.CreateLineItem
import com.example.yallabuy_user.data.models.CreateShippingAddress
import com.example.yallabuy_user.data.models.cart.LineItem
import com.example.yallabuy_user.data.models.settings.Address
import com.example.yallabuy_user.home.ProgressShow
import com.example.yallabuy_user.utilities.ApiResponse
import com.example.yallabuy_user.utilities.Common
import com.example.yallabuy_user.utilities.PaymentsUtil
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

private const val TAG = "OrderCheckoutScreen"
//cash show him a dialog if exceeded the amount
//pass total amount from cart screen to the OrderCheckoutScreen
//checkout?? dialog
//address >>hany

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderCheckoutScreen(
    viewModel: NewOrderViewModel = koinViewModel(), cartId: Long, passedTotalPrice: Double,

    setTopBar: (@Composable () -> Unit) -> Unit
) {

    val context = LocalContext.current
    val activity = context as? Activity
    val uiCartOrderState = viewModel.cartOrder.collectAsState()
    val uiAddressState = viewModel.address.collectAsState()
    val convertedTotal by viewModel.cartTotalInPreferredCurrency.collectAsState()
    val currencySymbol = Common.currencyCode.getCurrencyCode()
    var showCashLimitDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }


    //data
    var errorMessage by remember { mutableStateOf("") }
    val couponResult by viewModel.couponValidationResult.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var createLineItems: List<CreateLineItem> = listOf()
    var couponCode by remember { mutableStateOf("") }
    var shippingAddress = CreateShippingAddress(id = -1L)
    var totalCost by remember { mutableStateOf("") }
    var isCash by remember { mutableStateOf(true) }

    var getWay by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("EGP") }
    var paymentStatus by remember { mutableStateOf("pending") }
    var totalPayment by remember { mutableStateOf("") }

//    LaunchedEffect(passedTotalPrice) {
//        viewModel.convertTotalAmount(passedTotalPrice)
//    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val paymentData = PaymentData.getFromIntent(result.data!!)
            val paymentInfo = paymentData?.toJson()
            Log.d("OrderCheckoutScreen", "Payment successful: $paymentInfo")

            coroutineScope.launch {
                viewModel.postNewOrder(
                    items = createLineItems,
                    discountCode = couponCode,
                    shippingAddress = shippingAddress,
                    amount = totalCost,
                    getWay = "online",
                    financialStatus = "paid",
                    context = context
                )

                showSuccessDialog = true
            }
        } else {
            Toast.makeText(context, "Payment canceled or failed", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(passedTotalPrice) {
        viewModel.convertTotalAmount(passedTotalPrice)
    }

    LaunchedEffect(Unit) {
        setTopBar {
            CenterAlignedTopAppBar(
                title = { Text("Checkout") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF3B9A94)
                )
            )
        }
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

            LaunchedEffect(Unit) {
                viewModel.convertItemPrices(order)
            }
//            val cartTotal = order.lineItems.sumOf {
//                (it.price.toDoubleOrNull() ?: 0.0) * it.quantity
//            }

            val cartTotal = passedTotalPrice
            val CASH_LIMIT_EGP = 5000.0

            //createLineItems = mapLineItemToCreateLineItem(order.lineItems)
            //currency = order.currency ?: "EGP"
            val discount = couponResult?.takeIf { it.isValid }?.discountValue ?: 0.0
            val discountedTotal = (cartTotal - discount).coerceAtLeast(0.0)

            LaunchedEffect(discountedTotal) {
                viewModel.convertTotalAmount(discountedTotal)
            }

            val displayedTotal = convertedTotal ?: discountedTotal
            totalCost = "%.2f".format(displayedTotal)
            createLineItems = mapLineItemToCreateLineItem(order.lineItems)

            // getWay = if (isCash) "cash" else "online" // Name of Provider
            // paymentStatus = if (isCash) "pending" else "paid"
            // totalPayment = if (!isCash) totalCost else "00.01"
            Column(
                modifier = Modifier
                    //.fillMaxWidth()
                    .fillMaxSize()
                    .padding(vertical = 12.dp, horizontal = 6.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                order.lineItems.forEach { lineItem ->
                    OrderCheckoutItem(lineItem = lineItem, currency = currency ?: "$")
                    HorizontalDivider()
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
                                viewModel.validateCoupon(
                                    code = couponCode,
                                    cartTotal = cartTotal
                                )
                            }
                        }, modifier = Modifier, shape = RoundedCornerShape(8.dp)

                    ) {
                        Text("Validate Coupon")
                    }
                }
                couponResult?.let { result ->
                    Text(
                        text = result.message,
                        color = if (result.isValid) Color(0xFF2E7D32) else Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Cost", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    if (couponResult?.isValid == true) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text( //cartTotal
                                text = "$currencySymbol ${"%.2f".format(cartTotal)}",
                                fontSize = 18.sp,
                                color = Color.Gray,
                                textDecoration = TextDecoration.LineThrough
                            )
                            Text(
                                "$currencySymbol $totalCost",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    } else {
                        Text(
                            "${"%.2f".format(cartTotal)} $currencySymbol",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
//                    Text(
//                        "${order.totalPrice} $currency",
//                        fontSize = 22.sp,
//                        fontWeight = FontWeight.Bold
//                    )
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
                                //isCash = !isCash
                                isCash = true
                                //
                            })
                        Text("Cash")
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = !isCash, onClick = {
                                isCash = false

                                // val formattedPrice = totalCost //"%.2f".format(cartTotal)
                            }
                        )
                        Text("Online")
                    }

                }

                HorizontalDivider()
                Spacer(modifier = Modifier.height(10.dp))

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        //check if cash exceeded 5000 show him a dialog you have

                        val totalValue = convertedTotal ?: discountedTotal
                   //     val totalValue = displayedTotal

                  //      val egpToPreferredRate = viewModel.getConversionRate("EGP") ?: 1.0
                    //    val cashLimit = 5000.0 * egpToPreferredRate

                        if (isCash && totalValue > 5000.0) {
                            showCashLimitDialog = true
                        } else {
                            if (isCash) {
                                coroutineScope.launch {
                                    viewModel.postNewOrder(
                                        items = createLineItems,
                                        discountCode = couponCode,
                                        shippingAddress = shippingAddress,
                                        amount = totalCost,
                                        getWay = "cash",
                                        financialStatus = "pending",
                                        context = context
                                    )
                                    showSuccessDialog = true
                                }
                            } else {
                                try {
                                    val paymentRequestJson =
                                        PaymentsUtil.getGooglePayRequest(totalCost)
                                    val request =
                                        PaymentDataRequest.fromJson(paymentRequestJson.toString())
                                    if (request != null && activity != null) {
                                        val paymentsClient =
                                            PaymentsUtil.PaymentsClientFactory.getPaymentsClient(
                                                context
                                            )
                                        val task = paymentsClient.loadPaymentData(request)

                                        task.addOnCompleteListener { completedTask ->
                                            try {
                                                val exception = completedTask.exception
                                                if (exception is ResolvableApiException) {
                                                    val intentSenderRequest =
                                                        IntentSenderRequest.Builder(exception.resolution)
                                                            .build()
                                                    launcher.launch(intentSenderRequest)
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "Google Pay failed",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            } catch (e: Exception) {
                                                Toast.makeText(
                                                    context,
                                                    "Error with Google Pay",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        context,
                                        "Payment setup failed",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            }
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

                if (showCashLimitDialog) {
                    AlertDialog(
                        onDismissRequest = { showCashLimitDialog = false },
                        confirmButton = {
                            TextButton(onClick = { showCashLimitDialog = false }) {
                                Text("OK")
                            }
                        },
                        title = { Text("Cash Payment Limit") },
                        text = {
                            Text(
                                "You cannot pay this amount using cash. " +
                                        "Please choose an online payment method.\n\nThank you for your understanding."
                            )
                        }
                    )
                }

                if (showSuccessDialog) {
                    AlertDialog(
                        onDismissRequest = { showSuccessDialog = false },
                        confirmButton = {
                            TextButton(onClick = {
                                showSuccessDialog = false
                                //We need to delete that from draft order
                            }) {
                                Text("OK")
                            }
                        },
                        title = { Text("Order Placed") },
                        text = { Text("You have successfully placed your order.\nThank you for shopping with us!") }
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
fun DropdownField(
    label: String,
    selected: String,
    options: List<Address>,
    onSelected: (Long) -> Unit
) {
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
            options.forEach { address ->
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
