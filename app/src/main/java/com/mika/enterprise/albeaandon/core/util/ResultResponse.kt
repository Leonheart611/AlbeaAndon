package com.mika.enterprise.albeaandon.core.util

sealed class ResultResponse<out T> {
    data class Success<out T>(val data: T) : ResultResponse<T>()
    data class Error(
        val exception: Exception?,
        val errorResponse: ErrorResponse = ErrorResponse(),
    ) : ResultResponse<Nothing>()

    data class UnAuthorized(val errorResponse: ErrorResponse) : ResultResponse<Nothing>()

}

data class ErrorResponseValue(
    val success: Boolean? = null,
    val messages: List<String>? = null
)

data class ErrorResponse(
    val code: Int? = null,
    val message: String? = null
)