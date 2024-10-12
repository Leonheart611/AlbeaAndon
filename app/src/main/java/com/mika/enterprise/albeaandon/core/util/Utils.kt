package com.mika.enterprise.albeaandon.core.util

import android.content.Context
import android.widget.Toast
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import okhttp3.ResponseBody
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun String?.orEmptyDash() = this ?: "-"

fun ResponseBody?.toErrorResponseValue(): ErrorResponseValue {
    try {
        val errorResponse = Gson().fromJson(this?.string(), ErrorResponseValue::class.java)
        return errorResponse
    } catch (e: Exception) {
        return ErrorResponseValue(
            success = false,
            messages = listOf("Something went wrong")
        )
    }
}

fun String.convertDateIntoLocalDateTime(): String {
    try {
        val instant = Instant.parse(this)
        val zonedDateTime = instant.atZone(ZoneId.systemDefault())
        val formattedDate = zonedDateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"))
        return formattedDate
    } catch (e: Exception) {
        FirebaseCrashlytics.getInstance().recordException(e)
        return "cannot format this Date Value"
    }
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}