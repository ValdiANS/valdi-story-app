package com.myapplication.valdistoryapp.data

sealed class ResultState<out R> private constructor() {
    data class Success<out T>(val data: T) : ResultState<T>()
    data class Error(val errorCode: Int) : ResultState<Nothing>()
    object Loading : ResultState<Nothing>()
}
