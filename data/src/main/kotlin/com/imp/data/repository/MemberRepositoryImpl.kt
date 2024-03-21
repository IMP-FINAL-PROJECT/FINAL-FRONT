package com.imp.data.repository

import android.annotation.SuppressLint
import com.imp.data.mapper.CommonMapper
import com.imp.data.remote.api.ApiMember
import com.imp.data.util.ApiClient
import com.imp.data.util.extension.isSuccess
import com.imp.domain.model.ErrorCallbackModel
import com.imp.domain.model.MemberModel
import com.imp.domain.repository.MemberRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Member Repository Implementation
 */
class MemberRepositoryImpl @Inject constructor() : MemberRepository {

    /**
     * Login
     */
    @SuppressLint("CheckResult")
    override suspend fun login(id: String, password: String, successCallback: (MemberModel) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit) {

        val params: MutableMap<String, Any> = HashMap()

        params["id"] = id
        params["password"] = password

        ApiClient.getClient().create(ApiMember::class.java).login(params)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->

                if (response.isSuccess()) {
                    response.data?.let { successCallback.invoke(it) }
                } else {
                    errorCallback.invoke(CommonMapper.mappingErrorCallbackData(response))
                }

            }, { error ->
                errorCallback.invoke(CommonMapper.mappingErrorData(error))
            })
    }

    /**
     * Register
     */
    @SuppressLint("CheckResult")
    override suspend fun register(data: MemberModel, successCallback: (MemberModel) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit) {

        val params: MutableMap<String, Any> = HashMap()

        params["id"] = data.id ?: ""
        params["password"] = data.password ?: ""
        params["birth"] = data.birth ?: ""
        params["name"] = data.name ?: ""
        params["gender"] = data.gender ?: "N"

        ApiClient.getClient().create(ApiMember::class.java).register(params)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->

                if (response.isSuccess()) {
                    response.data?.let { successCallback.invoke(it) }
                } else {
                    errorCallback.invoke(CommonMapper.mappingErrorCallbackData(response))
                }

            }, { error ->
                errorCallback.invoke(CommonMapper.mappingErrorData(error))
            })
    }

    /**
     * Check Email Validation
     */
    @SuppressLint("CheckResult")
    override suspend fun checkEmail(id: String, successCallback: (Boolean) -> Unit, errorCallback: (ErrorCallbackModel?) -> Unit) {

        ApiClient.getClient().create(ApiMember::class.java).checkEmail(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->

                successCallback.invoke(response.isSuccess())

            }, { error ->
                errorCallback.invoke(CommonMapper.mappingErrorData(error))
            })
    }
}