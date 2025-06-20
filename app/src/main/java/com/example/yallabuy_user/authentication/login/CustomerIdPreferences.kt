package com.example.yallabuy_user.authentication.login

import android.content.Context
class CustomerIdPreferences {
    companion object {
        private const val PREF_NAME = "customer_id"
        private const val PREF_NAME_ = "customer_name"

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

        fun saveCustomerName(context: Context , name : String){
            val sharedPreferences = context.getSharedPreferences(PREF_NAME_, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(PREF_NAME_, name)
            editor.apply()
        }
        fun getCustomerName(context: Context): Long {
            val sharedPreferences = context.getSharedPreferences(PREF_NAME_, Context.MODE_PRIVATE)
            return sharedPreferences.getLong(PREF_NAME_, 0L)
        }
    }
}
