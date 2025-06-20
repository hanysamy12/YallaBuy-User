package com.example.yallabuy_user.cart.viewmodel

import android.content.Context

object CartSharedPreference {
    private const val PREF_NAME = "cart_id"

    fun saveCartID(context: Context, value: Long) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong(PREF_NAME, value)
        editor.apply()
    }

    fun getCartId(context: Context): Long {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getLong(PREF_NAME, 0L)
    }
}