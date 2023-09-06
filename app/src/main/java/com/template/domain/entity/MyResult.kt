package com.template.domain.entity


sealed class MyResult<out T> {
    data class Success<out T>(val data: T) : MyResult<T>()
    data class BadResponseError(val message: String) : MyResult<Nothing>()
    data class NoValueError(val message: String) : MyResult<Nothing>()

}