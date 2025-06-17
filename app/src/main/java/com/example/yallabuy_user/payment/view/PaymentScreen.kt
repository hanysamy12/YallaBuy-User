package com.example.yallabuy_user.payment.view

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.yallabuy_user.R
import com.example.yallabuy_user.utilities.PaymentsUtil
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.PaymentDataRequest

@Composable
fun PaymentScreen(
    navController: NavController,
    totalPrice: Double
) {
    val context = LocalContext.current
    val activity = context as? Activity
    var selectedPayment by remember { mutableStateOf("CASH") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Toast.makeText(context, "Payment Successful!", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        } else {
            Toast.makeText(context, "Payment Failed or Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Billing Address", fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = "123 Main St, Cairo",
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            enabled = false
        )

        Text("Payment Method", fontWeight = FontWeight.Bold)
        Row {
            RadioButton(
                selected = selectedPayment == "CASH",
                onClick = { selectedPayment = "CASH" }
            )
            Text("Cash")

            Spacer(modifier = Modifier.width(16.dp))

            RadioButton(
                selected = selectedPayment == "ONLINE",
                onClick = { selectedPayment = "ONLINE" }
            )
            Text("Google Pay")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (selectedPayment == "CASH") {
                    Toast.makeText(context, "Order placed with Cash!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                } else {
                    val paymentRequest = try {
                        val json = PaymentsUtil.getGooglePayRequest("%.2f".format(totalPrice))
                        PaymentDataRequest.fromJson(json.toString())
                    } catch (e: Exception) {
                        null
                    }

                    if (paymentRequest != null && activity != null) {
                        val task = PaymentsUtil.PaymentsClientFactory.getPaymentsClient(context)
                            .loadPaymentData(paymentRequest)
                        AutoResolveHelper.resolveTask(task, activity, 999)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.dark_blue))
        ) {
            Text("Place Order", color = Color.White)
        }
    }
}
