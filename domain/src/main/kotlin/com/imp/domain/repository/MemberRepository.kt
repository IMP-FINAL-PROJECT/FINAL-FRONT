package com.imp.domain.repository

import com.imp.domain.model.ErrorCallbackModel
import com.imp.domain.model.MemberModel

/**
 * Member Repository Interface
 */
interface MemberRepository {

    /**
     * Login
     */
    suspend fun login(id: String, password: String, successCallback: (MemberModel) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit)

    /**
     * Register
     */
    suspend fun register(data: MemberModel, successCallback: (MemberModel) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit)

    /**
     * Check Email Validation
     */
    suspend fun checkEmail(id: String, successCallback: (Boolean) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit)
}