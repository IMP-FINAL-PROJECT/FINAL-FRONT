package com.imp.data.util.extension

import com.imp.data.util.BaseResponse

/**
 * API 성공 여부 판단
 *
 * @return  성공 여부
 */
fun BaseResponse<*>.isSuccess(): Boolean {
    return 200 == status
}

/**
 * 토큰 만료 여부
 */
fun BaseResponse<*>.tokenExpired(): Boolean {
    return ( 400 == status || 401 == status )
}