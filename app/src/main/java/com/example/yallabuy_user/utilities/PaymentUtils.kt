package com.example.yallabuy_user.utilities

import android.content.Context
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.WalletConstants
import org.json.JSONArray
import org.json.JSONObject

object PaymentsUtil {

    fun getGooglePayRequest(totalPrice: String): JSONObject {
        val paymentDataRequestJson = JSONObject()
            .put("apiVersion", 2)
            .put("apiVersionMinor", 0)
            .put("allowedPaymentMethods", JSONArray().put(getCardPaymentMethod()))
            .put("transactionInfo", getTransactionInfo(totalPrice))
            .put("merchantInfo", getMerchantInfo())
        return paymentDataRequestJson
    }

    private fun getCardPaymentMethod(): JSONObject {
        return JSONObject()
            .put("type", "CARD")
            .put("parameters", JSONObject()
                .put("allowedAuthMethods", JSONArray().put("PAN_ONLY").put("CRYPTOGRAM_3DS"))
                .put("allowedCardNetworks", JSONArray().put("VISA").put("MASTERCARD"))
            )
            .put("tokenizationSpecification", JSONObject()
                .put("type", "PAYMENT_GATEWAY")
                .put("parameters", JSONObject()
                    .put("gateway", "example") // Fake Gateway
                    .put("gatewayMerchantId", "exampleMerchantId")
                )
            )
    }

    private fun getTransactionInfo(totalPrice: String): JSONObject {
        return JSONObject()
            .put("totalPrice", totalPrice)
            .put("totalPriceStatus", "FINAL")
            .put("currencyCode", "USD")
    }

    private fun getMerchantInfo(): JSONObject {
        return JSONObject()
            .put("merchantName", "Test Merchant")
    }

    object PaymentsClientFactory {

        fun getPaymentsClient(context: Context): PaymentsClient {
            val walletOptions = Wallet.WalletOptions.Builder()
                .setEnvironment(WalletConstants.ENVIRONMENT_TEST) // أو PRODUCTION
                .build()

            return Wallet.getPaymentsClient(context, walletOptions)
        }
    }
}
