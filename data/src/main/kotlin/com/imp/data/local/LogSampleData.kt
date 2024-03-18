package com.imp.data.local

import com.imp.data.local.dao.LogDao
import kotlin.random.Random

object LogSampleData {

    fun getLogData(): LogDao {

        return LogDao(
            screenTime = LogDao.LogValue(
                max = 60f,
                valueList = getRandomData(60)
            ),
            screenAwake = LogDao.LogValue(
                max = 10f,
                valueList = getRandomData(10)
            ),
            step = LogDao.LogValue(
                max = 1000f,
                valueList = getRandomData(1000)
            ),
            light = LogDao.LogValue(
                max = 100f,
                valueList = getRandomData(100)
            ),
            location = LogDao.LogPointValue(
                valueList = getDummyPointList()
            )
        )
    }

    private fun getRandomData(max: Int): ArrayList<Float> {

        val list = ArrayList<Float>()

        for (i in 0 until 24) {
            list.add(Random.Default.nextInt(max).toFloat())
        }

        return list
    }

    private fun getDummyPointList(): ArrayList<ArrayList<Double>> {

        return arrayListOf(
            arrayListOf(37.537229, 127.005515),
            arrayListOf(37.545024, 127.03923),
            arrayListOf(37.527896, 127.036245),
            arrayListOf(37.541889, 127.095388)
        )
    }
}