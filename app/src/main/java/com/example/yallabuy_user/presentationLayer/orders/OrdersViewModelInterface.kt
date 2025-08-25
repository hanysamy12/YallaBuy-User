package com.example.yallabuy_user.presentationLayer.orders

import android.content.Context

interface OrdersViewModelInterface {
    suspend fun getPreviousOrders(context: Context)
    suspend fun getOrderById(orderID : Long?)
}