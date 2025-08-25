package com.example.yallabuy_user.presentationLayer.authentication.login

import android.content.Context
import androidx.core.content.edit

class CustomerIdPreferences {
    companion object {
        private const val PREF_NAME = "customer_id"
        private const val PREF_NAME_ = "customer_name"

        fun saveCustomerID(context: Context, value: Long) {
            val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            sharedPreferences.edit {
                putLong(PREF_NAME, value)
            }
        }

        fun getData(context: Context): Long {
            val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getLong(PREF_NAME, 0L)
        }

        fun saveCustomerName(context: Context , name : String){
            val sharedPreferences = context.getSharedPreferences(PREF_NAME_, Context.MODE_PRIVATE)
            sharedPreferences.edit {
                putString(PREF_NAME_, name)
            }
        }

        fun getCustomerName(context: Context): String? {
            val sharedPreferences = context.getSharedPreferences(PREF_NAME_, Context.MODE_PRIVATE)
            return sharedPreferences.getString(PREF_NAME_, null)

        }
    }
}
