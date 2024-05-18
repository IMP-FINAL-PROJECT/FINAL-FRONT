package com.imp.domain.model

/**
 * Home - Log Model
 */
data class LogModel (

    // id
    var id: String? = null,

    // date (yyyy-MM-dd)
    var date: String? = null,

    // day data
    var day: DayData = DayData(),

    // week data
    var week: WeekData = WeekData(),

    // gps
    var gps: ArrayList<ArrayList<Double>> = ArrayList()

) {

    data class DayData (

        // 조도 리스트
        var illuminance: ArrayList<Int> = ArrayList(),

        // 총 걸음 수
        var pedometer: Int = 0,

        // 걸음 수 리스트
        var pedometer_list: ArrayList<Int> = ArrayList(),

        // 총 화면 깨우기 수
        var screen_frequency: Int = 0,

        // 화면 깨우기 수 리스트
        var screen_frequency_list: ArrayList<Int> = ArrayList(),

        // 총 스크린 타임
        var screen_duration: Int = 0,

        // 스크린 타임 리스트
        var screen_duration_list: ArrayList<Int> = ArrayList(),

        // 총 통화 빈도 수
        var call_frequency: Int = 0,

        // 통화 빈도 수 리스트
        var call_frequency_list: ArrayList<Int> = ArrayList(),

        // 총 통화 시간
        var call_duration: Int = 0,

        // 통화 시간 리스트
        var call_duration_list: ArrayList<Int> = ArrayList()
    )

    data class WeekData (

        // 조도 리스트
        var illuminance: ArrayList<Int> = ArrayList(),

        // 총 걸음 수
        var pedometer: Int = 0,

        // 걸음 수 리스트
        var pedometer_list: ArrayList<Int> = ArrayList(),

        // 총 화면 깨우기 수
        var screen_frequency: Int = 0,

        // 화면 깨우기 수 리스트
        var screen_frequency_list: ArrayList<Int> = ArrayList(),

        // 총 스크린 타임
        var screen_duration: Int = 0,

        // 스크린 타임 리스트
        var screen_duration_list: ArrayList<Int> = ArrayList(),

        // 총 통화 빈도 수
        var call_frequency: Int = 0,

        // 통화 빈도 수 리스트
        var call_frequency_list: ArrayList<Int> = ArrayList(),

        // 총 통화 시간
        var call_duration: Int = 0,

        // 통화 시간 리스트
        var call_duration_list: ArrayList<Int> = ArrayList()
    )
}