package com.mika.enterprise.albeaandon.core.util

import android.content.SharedPreferences
import com.mika.enterprise.albeaandon.core.util.Constant.KEY_LANGUAGE
import com.mika.enterprise.albeaandon.core.util.Constant.PROD_URL_ID
import com.mika.enterprise.albeaandon.core.util.Constant.USER_TOKEN
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.text.split

@Singleton
class AuthInterceptor @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : Interceptor {

    var baseUrl = PROD_URL_ID
    fun setUpdatedUrl(newUrl: String) {
        baseUrl = newUrl
    }


    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = sharedPreferences.getString(USER_TOKEN, "") ?: ""
        val newUrl = request.url.newBuilder()
            .scheme(baseUrl.split("://")[0])
            .host(baseUrl.split("://")[1].split(":")[0])
            .port(baseUrl.split("://")[1].split(":")[1].split("/")[0].toInt())
            .build()
        if (token.isNotEmpty()) {
            val newRequest = request.newBuilder()
                .url(newUrl)
                .addHeader("Authorization", "Bearer $token")
                .build()
            return chain.proceed(newRequest)
        }
        val newRequest = request.newBuilder().url(newUrl).build()
        return chain.proceed(newRequest)
    }
}