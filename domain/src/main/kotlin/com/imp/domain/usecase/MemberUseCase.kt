package com.imp.domain.usecase

import com.imp.domain.model.ErrorCallbackModel
import com.imp.domain.model.MemberModel
import com.imp.domain.repository.MemberRepository
import javax.inject.Inject

/**
 * Member UseCase
 */
class MemberUseCase @Inject constructor(private val repository: MemberRepository) {

    /**
     * Login
     */
    suspend fun login(id: String, password: String, successCallback: (MemberModel) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit) {
        repository.login(id, password, successCallback, errorCallback)
    }
}