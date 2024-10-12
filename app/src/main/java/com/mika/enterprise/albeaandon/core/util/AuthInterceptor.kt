package com.mika.enterprise.albeaandon.core.util

import android.content.SharedPreferences
import com.mika.enterprise.albeaandon.core.util.Constant.USER_TOKEN
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = sharedPreferences.getString(USER_TOKEN, "") ?: ""
        if (token.isNotEmpty()) {
            val newRequest = request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            return chain.proceed(newRequest)
        }
        return chain.proceed(request)
    }
}