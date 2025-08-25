package com.example.yallabuy_user.presentationLayer.home

interface HomeViewModelInterface {
    suspend fun getAllCategories()
    suspend fun getAllBrands()
}