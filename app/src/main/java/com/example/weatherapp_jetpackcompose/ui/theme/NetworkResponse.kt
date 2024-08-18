package com.example.weatherapp_jetpackcompose.ui.theme

import com.example.weatherapp_jetpackcompose.R

sealed class NetworkResponse<out T>{
    data class Success<out T>(val data: T): NetworkResponse<T>()
    data class Error(val message: String): NetworkResponse<Nothing>()
    object Loading: NetworkResponse<Nothing>()
}