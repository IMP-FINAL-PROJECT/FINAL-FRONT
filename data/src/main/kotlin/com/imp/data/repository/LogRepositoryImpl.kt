package com.imp.data.repository

import com.imp.data.local.LogSampleData
import com.imp.data.mapper.LogMapper
import com.imp.domain.model.LogModel
import com.imp.domain.repository.LogRepository
import javax.inject.Inject

/**
 * Main - Log Repository Implementation
 */
class LogRepositoryImpl @Inject constructor() : LogRepository {

    override suspend fun loadLogData(): LogModel {
        return LogMapper.mappingLogData(LogSampleData.getLogData())
    }
}