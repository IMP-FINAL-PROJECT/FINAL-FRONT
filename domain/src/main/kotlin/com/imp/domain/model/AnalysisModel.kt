package com.imp.domain.model

/**
 * Main - Analysis Model
 */
data class AnalysisModel(

    var id: String? = null,

    var date: String? = null,

    // 생활의 규칙성 점수
    var circadian_rhythm_score: Double = 0.0,

    // 핸드폰 사용 시간 점수
    var phone_usage_score: Double = 0.0,

    // 활동 점수
    var activity_score: Double = 0.0,

    // 장소의 다양성 점수
    var location_diversity_score: Double = 0.0,

    // 빛 노출량 점수
    var illuminance_exposure_score: Double = 0.0,

    // 장소의 다양성
    var place_diversity: ArrayList<ArrayList<Double>> = ArrayList(),

    // 집에 머무는 시간 (%)
    var home_stay_percentage: Double = 0.0,

    // 생활의 규칙성
    var life_routine_consistency: Double = 0.0,

    // 낮 시간 휴대폰 사용 빈도
    var day_phone_use_frequency: Double = 0.0,

    // 밤 시간 휴대폰 사용 빈도
    var night_phone_use_frequency: Double = 0.0,

    // 낮 시간 휴대폰 사용 시간
    var day_phone_use_duration: Double = 0.0,

    // 밤 시간 휴대폰 사용 시간
    var night_phone_use_duration: Double = 0.0,

    // 낮 시간 통화 빈도
    var day_call_use_frequency: Double = 0.0,

    // 밤 시간 통화 빈도
    var night_call_use_frequency: Double = 0.0,

    // 낮 시간 통화 시간
    var day_call_use_duration: Double = 0.0,

    // 밤 시간 통화 시간
    var night_call_use_duration: Double = 0.0,

    // 낮 빛 노출량
    var day_light_exposure: Double = 0.0,

    // 밤 빛 노출량
    var night_light_exposure: Double = 0.0,

    // 낮 걸음 수
    var day_step_count: Double = 0.0,

    // 밤 걸음 수
    var night_step_count: Double = 0.0
)