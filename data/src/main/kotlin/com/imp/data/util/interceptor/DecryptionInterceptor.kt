package com.imp.data.util.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

/**
 * Decryption Interceptor
 */
class DecryptionInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val response = chain.proceed(chain.request())
        if (response.isSuccessful) {

            val str = response.body!!.string()

            val newResponse = response.newBuilder()
            val contentType = "application/json;charset=UTF-8"
            newResponse.body(str.toResponseBody(contentType.toMediaTypeOrNull()))

            Log.d("api", "Response Body: $str")
            return newResponse.build()
        }

        return response
    }
}