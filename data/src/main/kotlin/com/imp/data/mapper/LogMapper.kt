package com.imp.data.mapper

import com.imp.data.local.dao.LogDao
import com.imp.domain.model.LogModel

/**
 * Main - Log Mapper
 */
object LogMapper {

    /**
     * Mapping Log Data
     */
    fun mappingLogData(response: LogDao): LogModel {

        return LogModel(
            screenTime = LogModel.LogValue(
                response.screenTime.max,
                response.screenTime.valueList
            ),
            screenAwake = LogModel.LogValue(
                response.screenAwake.max,
                response.screenAwake.valueList
            ),
            step = LogModel.LogValue(
                response.step.max,
                response.step.valueList
            ),
            light = LogModel.LogValue(
                response.light.max,
                response.light.valueList
            )
        )
    }
}