package com.imp.domain.model

/**
 * Main - Analysis Model
 */
data class AnalysisModel(

    var id: String? = null,

    var date: String? = null,

    var place_diversity: ArrayList<ArrayList<Double>> = ArrayList(),

    var home_stay_percentage: Double = 0.0,

    var life_routine_consistency: Double = 0.0,

    var day_phone_use_frequency: Double = 0.0,

    var night_phone_use_frequency: Double = 0.0,

    var day_phone_use_duration: Double = 0.0,

    var night_phone_use_duration: Double = 0.0,

    var day_call_use_frequency: Double = 0.0,

    var night_call_use_frequency: Double = 0.0,

    var day_call_use_duration: Double = 0.0,

    var night_call_use_duration: Double = 0.0,

    var day_light_exposure: Double = 0.0,

    var night_light_exposure: Double = 0.0,

    var day_step_count: Double = 0.0,

    var night_step_count: Double = 0.0
)