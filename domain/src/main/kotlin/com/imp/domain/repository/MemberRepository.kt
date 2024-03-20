package com.imp.domain.repository

import com.imp.domain.model.MemberModel

/**
 * Member Repository Interface
 */
interface MemberRepository {

    /**
     * Login
     */
    suspend fun login(id: String, password: String, successCallback: (MemberModel) -> Unit, errorCallback: (String?) -> Unit)
}