package com.imp.data.util

import com.google.gson.annotations.SerializedName

/**
 * Base Response
 */
data class BaseResponse<T> (

        // 결과값
        @JvmField
        @SerializedName("result")
        var result: Boolean = false,

        // 상태 코드 (200: 정상, 422: 오류, 500: 시스템 오류)
        @JvmField
        @SerializedName("status")
        var status: Int? = null,

        // 데이터
        @JvmField
        @SerializedName("data")
        var data: T? = null,

        // 오류 메시지
        @JvmField
        @SerializedName("message")
        var message: String? = null,
)