package com.example.yallabuy_user.orders

interface OrdersViewModelInterface {
    suspend fun getPreviousOrders(userID : Long)
}