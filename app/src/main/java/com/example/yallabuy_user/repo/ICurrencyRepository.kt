package com.example.yallabuy_user.repo

interface ICurrencyRepository {

    suspend fun getPreferredCurrency(): String

    suspend fun setPreferredCurrency(currencyCode: String)

  //  val preferredCurrencyFlow: Flow<String>

    suspend fun getCurrencyRate(baseCurrency: String, targetCurrency: String): Double

    suspend fun getRatesForBase(baseCurrency: String): Map<String, Double>


}