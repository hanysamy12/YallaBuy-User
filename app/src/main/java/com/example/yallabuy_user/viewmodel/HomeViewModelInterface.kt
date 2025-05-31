package com.example.yallabuy_user.viewmodel

interface HomeViewModelInterface {
    suspend fun getAllCategories()
    suspend fun getAllBrands()
}