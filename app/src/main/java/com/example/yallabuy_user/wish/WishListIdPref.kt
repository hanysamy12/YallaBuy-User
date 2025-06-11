package com.example.yallabuy_user.wish

import android.content.Context

class WishListIdPref {
    companion object {
        private const val PREF_NAME = "wishList_id"
        fun saveWishListID(context: Context, value: Long) {
            val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putLong(PREF_NAME, value)
            editor.apply()
        }

        fun getWishListId(context: Context): Long {
            val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getLong(PREF_NAME, 0L)
        }
    }
}