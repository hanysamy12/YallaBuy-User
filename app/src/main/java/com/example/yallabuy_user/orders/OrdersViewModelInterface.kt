package com.example.yallabuy_user.orders

import android.content.Context

interface OrdersViewModelInterface {
    suspend fun getPreviousOrders(context: Context)
    suspend fun getOrderById(orderID : Long?)
}