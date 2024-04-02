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

    /**
     * Register
     */
    suspend fun register(data: MemberModel, successCallback: (MemberModel) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit) {
        repository.register(data, successCallback, errorCallback)
    }

    /**
     * Check Email Validation
     */
    suspend fun checkEmail(id: String, successCallback: (Boolean) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit) {
        repository.checkEmail(id, successCallback, errorCallback)
    }

    /**
     * Get Member Data
     */
    suspend fun getMemberData(successCallback: (MemberModel) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit) {
        repository.getMemberData(successCallback, errorCallback)
    }

    /**
     * Edit Profile
     */
    suspend fun editProfile(data: MemberModel, successCallback: (MemberModel) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit) {
        repository.editProfile(data, successCallback, errorCallback)
    }
}