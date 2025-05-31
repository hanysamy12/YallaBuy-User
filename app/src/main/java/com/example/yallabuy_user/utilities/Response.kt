package com.example.yallabuy_user.utilities


sealed class ApiResponse<out T> {
    data object Loading : ApiResponse<Nothing>()
    data class Success<T>(val data: T) : ApiResponse<T>()
    data class Failure(val error: Throwable) : ApiResponse<Nothing>()
}