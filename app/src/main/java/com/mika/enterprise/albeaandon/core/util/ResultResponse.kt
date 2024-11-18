package com.mika.enterprise.albeaandon.core.util

import com.google.firebase.crashlytics.FirebaseCrashlytics
import retrofit2.Response

sealed class ResultResponse<out T> {
    data class Success<out T>(val data: T) : ResultResponse<T>()
    data class EmptyOrNotFound(val message: String? = null, val isSuccess: Boolean) :
        ResultResponse<Nothing>()

    data class Error(val errorResponse: ErrorResponse = ErrorResponse()) : ResultResponse<Nothing>()

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

fun <T> handleUnauthorizedError(response: Response<T>): ResultResponse<T> {
    return ResultResponse.UnAuthorized(
        ErrorResponse(
            code = response.code(),
            message = response.errorBody().toErrorResponseValue().messages?.firstOrNull()
                .orEmpty()
        )
    )
}

fun <T> handleGenericError(response: Response<T>): ResultResponse<T> {
    val message = response.errorBody()?.string()?.toErrorResponseValue()?.messages?.firstOrNull()
    FirebaseCrashlytics.getInstance()
        .recordException(
            Exception(
                "Error -- ${response.code()} -- ${
                    response.errorBody()?.string()
                }"
            )
        )
    return ResultResponse.Error(
        ErrorResponse(
            code = response.code(),
            message =  message
        )
    )
}

fun <T> handleGenericError(error: Exception): ResultResponse<T> {
    FirebaseCrashlytics.getInstance().recordException(error)
    return ResultResponse.Error(
        ErrorResponse(code = 404, message = error.message.orEmpty())
    )
}

fun <T> handleNotFoundError(response: Response<T>): ResultResponse<T> {
    return response.errorBody().toErrorResponseValue().let {
        ResultResponse.EmptyOrNotFound(
            message = it.messages?.firstOrNull().orEmpty(),
            isSuccess = it.success ?: false,
        )
    }
}