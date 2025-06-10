package com.example.yallabuy_user.authentication.login

import android.content.Context
class CustomerIdPreferences {
    companion object {
        private const val PREF_NAME = "customer_id"

        fun saveCustomerID(context: Context, value: Long) {
            val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putLong(PREF_NAME, value)
            editor.apply()
        }

        fun getData(context: Context): Long {
            val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getLong(PREF_NAME, 0L)
        }
    }
}
