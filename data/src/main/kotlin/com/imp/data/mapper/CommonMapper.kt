package com.imp.data.mapper

import com.imp.data.util.BaseResponse
import com.imp.domain.model.ErrorCallbackModel

/**
 * Common Mapper
 */
object CommonMapper {

    /**
     * Mapping ErrorCallback
     */
    fun <T> mappingErrorCallbackData(response: BaseResponse<T>): ErrorCallbackModel {
        return ErrorCallbackModel(response.status, response.message)
    }

    /**
     * Mapping Error
     */
    fun mappingErrorData(response: Throwable): ErrorCallbackModel {
        return ErrorCallbackModel(null, response.message)
    }
}