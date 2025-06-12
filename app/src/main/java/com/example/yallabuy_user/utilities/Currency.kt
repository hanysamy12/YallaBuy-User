package com.example.yallabuy_user.utilities

enum class Currency {
    USD,
    EUR,
    SAR,
    EGP;

    fun getCurrencyCode(): String {
        return when (this) {
            USD -> "$"
            EUR -> "€"
            SAR -> "SAR"
            EGP -> "EGP"
        }
    }
}