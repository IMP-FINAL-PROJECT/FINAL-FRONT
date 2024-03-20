package com.imp.data.repository

import android.annotation.SuppressLint
import com.imp.data.remote.api.ApiLogin
import com.imp.data.util.ApiClient
import com.imp.data.util.extension.isSuccess
import com.imp.domain.model.MemberModel
import com.imp.domain.repository.MemberRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Member Repository Implementation
 */
class MemberRepositoryImpl @Inject constructor() : MemberRepository {

    @SuppressLint("CheckResult")
    override suspend fun login(id: String, password: String, successCallback: (MemberModel) -> Unit, errorCallback: (String?) -> Unit) {

        val params: MutableMap<String, Any> = HashMap()

        params["id"] = id
        params["password"] = password

        ApiClient.getClient().create(ApiLogin::class.java).login(params)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->

                if (response.isSuccess()) {
                    response.data?.let { successCallback.invoke(it) }
                } else {
                    errorCallback.invoke(response.message)
                }

            }, { error ->

                errorCallback.invoke(error.message)
            })
    }
}