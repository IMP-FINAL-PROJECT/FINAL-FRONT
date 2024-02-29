package com.imp.data.util.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import java.io.IOException

/**
 * Header Interceptor
 */
class HeaderInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val original = chain.request()
        val newBuilder = original.newBuilder()

        // 헤더에 인증토큰 추가
//        val accessToken:String? = PreferencesUtil.getPreferencesString(BaseConstants.ACCESS_TOKEN)
//        accessToken?.let {
//            if (it.isNotEmpty()) {
//                newBuilder.addHeader("Authorization", "Bearer $it")
//                Log.d("api", "AccessToken : $it")
//            }
//        }

        val request = newBuilder.build()

        Log.d("api", "Request URL : " + original.url)
        Log.d("api", "Request Body : " + toString(original.body))

        return chain.proceed(request)
    }

    fun toString(request: RequestBody?): String? {
        return try {
            val buffer = Buffer()
            if (request != null) {
                request.writeTo(buffer)
            } else {
                return ""
            }
            buffer.readUtf8()
        } catch (e: IOException) {
            ""
        }
    }
}