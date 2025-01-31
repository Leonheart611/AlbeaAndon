package com.mika.enterprise.albeaandon.core.util

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import com.google.android.gms.common.util.ClientLibraryUtils.getPackageInfo
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mika.enterprise.albeaandon.R
import com.mika.enterprise.albeaandon.core.util.Constant.SPV_PRODUCTION
import com.mika.enterprise.albeaandon.core.util.Constant.spvUserDeptFilter
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
        e.printStackTrace()
        return ErrorResponseValue(
            success = false,
            messages = listOf("Something went wrong")
        )
    }
}

fun String.toErrorResponseValue(): ErrorResponseValue {
    try {
        val gson = GsonBuilder().setLenient().create()
        val errorResponse = gson.fromJson(this, ErrorResponseValue::class.java)
        return errorResponse
    } catch (e: Exception) {
        e.printStackTrace()
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

fun mappingColors(status: String): Int {
    return when (status) {
        "NEW" -> R.color.new_color
        "ONPROG" -> R.color.in_progress_color
        "ASSIGNED" -> R.color.assigned_color
        "CLOSED" -> R.color.close_color
        else -> R.color.new_color
    }
}

fun mappingAssignFilter(userDept: String): String {
    return when (userDept) {
        SPV_PRODUCTION -> spvUserDeptFilter
        "Mechanic" -> "MECHANIC"
        "OperatorBahan" -> "OPERATOR_BAHAN"
        else -> ""
    }
}

fun Context.getVersionName(): String = try {
    getPackageInfo(this, this.packageName)?.versionName ?: ""
} catch (e: PackageManager.NameNotFoundException) {
    ""
}

fun String.getCodeLanguage(): String {
    return when (this) {
        "English" -> "en"
        "Chinese - 中文" -> "zh"
        else -> "en"
    }
}

