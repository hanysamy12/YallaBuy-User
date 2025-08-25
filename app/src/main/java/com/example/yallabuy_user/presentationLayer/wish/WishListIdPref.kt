package com.example.yallabuy_user.presentationLayer.wish

import android.content.Context
import androidx.core.content.edit

class WishListIdPref {
    companion object {
        private const val PREF_NAME = "wishList_id"
        fun saveWishListID(context: Context, value: Long) {
            val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            sharedPreferences.edit {
                putLong(PREF_NAME, value)
            }
        }

        fun getWishListId(context: Context): Long {
            val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getLong(PREF_NAME, 0L)
        }
    }
}