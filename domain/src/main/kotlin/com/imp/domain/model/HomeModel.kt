package com.imp.domain.model

/**
 * Main - Home Model
 */
data class HomeModel (

    // id
    var id: String? = null,

    // 행복 점수
    var point: Int = 0,

    // 행복 점수
    var point_list: ArrayList<Int> = ArrayList()
)