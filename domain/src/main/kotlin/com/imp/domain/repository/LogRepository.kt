package com.imp.domain.repository

import com.imp.domain.model.LogModel

/**
 * Main - Log Repository Interface
 */
interface LogRepository {

    /**
     * Load Log Data
     */
    suspend fun loadLogData(): LogModel
}