package com.gokanaz.kanazplayer.core.common.result

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

typealias ResultState<T> = Result<T>