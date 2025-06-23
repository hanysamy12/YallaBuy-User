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
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.yallabuy_user.R
import com.example.yallabuy_user.authentication.login.CustomerIdPreferences
import com.example.yallabuy_user.cart.viewmodel.CartSharedPreference
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

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderCheckoutScreen(
    viewModel: NewOrderViewModel = koinViewModel(),
    cartId: Long, passedTotalPrice: Double,
    setTopBar: (@Composable () -> Unit) -> Unit,
    navController: NavController
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
    //  val newOrdersViewModel = LocalOrdersViewModel.current
    val customerId = CustomerIdPreferences.getData(context)


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
                title = {
                    Text(
                        "Checkout", color = Color.White,
                        fontFamily = FontFamily(Font(R.font.caprasimo_regular)),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorResource(R.color.teal_80)
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

            val cartTotal = passedTotalPrice

            val discount = couponResult?.takeIf { it.isValid }?.discountValue ?: 0.0
            val discountedTotal = (cartTotal - discount).coerceAtLeast(0.0)

            LaunchedEffect(discountedTotal) {
                viewModel.convertTotalAmount(discountedTotal)
            }

            val displayedTotal = convertedTotal ?: discountedTotal
            totalCost = "%.2f".format(displayedTotal)
            createLineItems = mapLineItemToCreateLineItem(order.lineItems)

            CompositionLocalProvider(LocalOrdersViewModel provides viewModel) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 12.dp, horizontal = 12.dp)
                        .verticalScroll(rememberScrollState())
                ) {

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
                            Text("Validate")
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
                            val totalValue = convertedTotal ?: discountedTotal

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
                                        if (activity != null) {
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
                        Log.i(
                            "newOrder",
                            "OrderSuccessDialog cart id ${CartSharedPreference.getCartId(context)} "
                        )
                        viewModel.removeCartDraftOrder(
                            CartSharedPreference.getCartId(
                                context
                            ), customerId
                        )
                        OrderSuccessDialog(
                            onDismissRequest = { showSuccessDialog = false },
                            onConfirmation = {
                                showSuccessDialog = false
                                navController.popBackStack()
                            })
                    }
                }
            }
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

@Composable
fun OrderSuccessDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
) {
    val successComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.successanimation))
    val successProgress by animateLottieCompositionAsState(
        composition = successComposition,
        iterations = LottieConstants.IterateForever
    )
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                onClick = {
                    onConfirmation()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B9A94),
                    contentColor = Color.White
                )
            ) {
                Text("Continue Shopping")
            }
        },
        icon = {
            LottieAnimation(
                composition = successComposition,
                progress = { successProgress },
                modifier = Modifier.size(100.dp)
            )
        },
        title = {
            Text("Order Placed!", color = Color.Black)
        },
        text = {
            Text(
                text = "Your order has been placed successfully.",
                color = Color.DarkGray
            )
        },
        modifier = Modifier.padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White,
        iconContentColor = Color(0xFF4CAF50), // green
        titleContentColor = Color.Black,
        textContentColor = Color.DarkGray,
        tonalElevation = 8.dp,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    )
}


private fun mapLineItemToCreateLineItem(lineItems: List<LineItem>): List<CreateLineItem> {
    return lineItems.map { lineItem ->
        CreateLineItem(variantId = lineItem.variantID, quantity = lineItem.quantity)
    }
}
