package com.example.yallabuy_user.presentationLayer.cart.viewmodel

import android.content.Context
import androidx.core.content.edit

object CartSharedPreference {
    private const val PREF_NAME = "cart_id"

    fun saveCartID(context: Context, value: Long) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit {
            putLong(PREF_NAME, value)
        }
    }

    fun getCartId(context: Context): Long {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getLong(PREF_NAME, 0L)
    }
}