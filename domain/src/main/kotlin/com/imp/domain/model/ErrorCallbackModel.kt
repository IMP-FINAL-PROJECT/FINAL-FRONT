package com.imp.domain.model

/**
 * Error Callback Model
 */
data class ErrorCallbackModel (

    // 상태 코드
    var status: Int? = null,

    // 오류 메시지
    var message: String? = null
)