package com.example.yallabuy_user.settings.model.remote


import com.google.gson.annotations.SerializedName

/*
https://v6.exchangerate-api.com/v6/YOUR_API_KEY/latest/USD
 */
data class ExchangeRateResponse(
    @SerializedName("result")
    val result: String,

    @SerializedName("documentation")
    val documentation: String,

    @SerializedName("terms_of_use")
    val termsOfUse: String,

    @SerializedName("time_last_update_unix")
    val timeLastUpdateUnix: Long,

    @SerializedName("time_last_update_utc")
    val timeLastUpdateUtc: String,

    @SerializedName("time_next_update_unix")
    val timeNextUpdateUnix: Long,

    @SerializedName("time_next_update_utc")
    val timeNextUpdateUtc: String,

    @SerializedName("base_code")
    val baseCode: String, //"USD"

    @SerializedName("conversion_rates")
    val conversionRates: Map<String, Double> //{"USD": 1.0, "EUR": 0.92, "EGP": 47.5}
)
