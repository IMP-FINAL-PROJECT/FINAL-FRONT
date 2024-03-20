package com.imp.domain.model

/**
 * Member Model
 */
data class MemberModel (

    // user id
    var id: String? = null,

    // brith (yyyy-MM-dd)
    var screenAwake: String? = null,

    // name
    var name: String? = null,

    // gender (남 : M, 여 : F, 선택 안 함 : N)
    var gender: String? = null
)