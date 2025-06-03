package com.example.yallabuy_user.home

interface HomeViewModelInterface {
    suspend fun getAllCategories()
    suspend fun getAllBrands()
}