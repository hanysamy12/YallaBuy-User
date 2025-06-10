package com.example.yallabuy_user.settings.model.remote

import com.example.yallabuy_user.utilities.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeRateApiService {

    //url: https://v6.exchangerate-api.com/v6/
    // Example full URL: https://v6.exchangerate-api.com/v6/YOUR_API_KEY/latest/USD

    @GET("{apiKey}/latest/EGP")
    suspend fun getLatestRates(
        @Path("apiKey") apiKey: String,
//        @Path("baseCurrency") baseCurrency: String = "EGP"
    ): ExchangeRateResponse

}
