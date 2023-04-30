package com.example.geotrackfamily.utility

sealed class Result<out T> {
    data class Success<T>(val data: T): Result<T>()
    data class Error(val error: String): Result<Nothing>()
    object Empty: Result<Nothing>()
}

data class CompositionObj<T, T1>(val data: T, val message: T1)