package com.example.yallabuy_user.settings.model.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeRateApiService {

    //url: https://v6.exchangerate-api.com/v6/
    // Example full URL: https://v6.exchangerate-api.com/v6/YOUR_API_KEY/latest/USD

    @GET("{apiKey}/latest/{baseCurrency}")
    suspend fun getLatestRates(
        @Path("apiKey") apiKey: String,
        @Path("baseCurrency") baseCurrency: String = "USD"
    ): Response<ExchangeRateResponse>

}
