package ru.practicum.android.diploma.data.dto

sealed class Response

data class ResponseSuccess<T>(val data: T) : Response()
data class ResponseError(val exception: Throwable? = null) : Response()
