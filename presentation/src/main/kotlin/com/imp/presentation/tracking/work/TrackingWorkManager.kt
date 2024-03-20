package com.imp.presentation.tracking.work

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.imp.presentation.tracking.util.TrackingDataUtil
import com.imp.presentation.widget.utils.DateUtil
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * Tracking WorkManager
 */
class TrackingWorkManager(private val context: Context, params: WorkerParameters) : Worker(context, params) {

    companion object {
        private const val TAG = "work"
    }

    private var mDisposable: CompositeDisposable? = null
    private var dataJob: Job? = null

    init { mDisposable = CompositeDisposable() }

    override fun doWork(): Result {

        dataJob?.cancel()
        dataJob = CoroutineScope(Dispatchers.IO).launch {

            // get data
            val illuminance = async { TrackingDataUtil.getIlluminance() }.await()
            var pedometer = async { TrackingDataUtil.getPedometer() }.await()
            val gps = async { TrackingDataUtil.getGps() }.await()
            var screen = async { TrackingDataUtil.getScreenTime(context) }.await()

            val minTimestamp = minOf(illuminance.first, pedometer.first, gps.first)
            val hour = DateUtil.getHour(minTimestamp)
            Log.d(TAG, "hour: $hour")

            // 최소 시간대 보다 구한 시간대가 이후일 경우, 데이터 초기화
            if (DateUtil.getHour(illuminance.first) > hour) {

                Log.d(TAG, "illuminance first: ${illuminance.first}, ${DateUtil.getHour(illuminance.first)}")
                Log.d(TAG, "illuminance clear, ${DateUtil.timestampToDataMin(illuminance.first)}")

                illuminance.second.clear()
            }
            if (DateUtil.getHour(pedometer.first) > hour) {

                Log.d(TAG, "pedometer first: ${pedometer.first}, ${DateUtil.getHour(pedometer.first)}")
                Log.d(TAG, "pedometer clear, ${DateUtil.timestampToDataMin(pedometer.first)}")

                pedometer = Pair(minTimestamp, 0)
            }
            if (DateUtil.getHour(gps.first) > hour) {

                Log.d(TAG, "gps first: ${gps.first}, ${DateUtil.getHour(gps.first)}")
                Log.d(TAG, "gps clear, ${DateUtil.timestampToDataMin(gps.first)}")

                gps.second.clear()
            }
            if (screen.first != -1L && DateUtil.getHour(screen.first) > hour) {

                Log.d(TAG, "screen first: ${screen.first}, ${DateUtil.getHour(screen.first)}")
                Log.d(TAG, "screen clear, ${DateUtil.timestampToDataMin(screen.first)}")

                screen = Triple(minTimestamp, 0, 0)
            }

            // save tracking data
            requestSaveData(
                id = "test1@naver.com",
                illuminance = illuminance.second,
                pedometer = pedometer.second,
                screenDuration = screen.second,
                screenFrequency = screen.third,
                gps = gps.second,
                timestamp = DateUtil.timestampToData(minTimestamp),
                hour = DateUtil.getHour(minTimestamp),
                minTimestamp = minTimestamp
            )
        }

        return Result.success()
    }

    /**
     * Request Save Data
     *
     * @param id 사용자 이메일
     * @param illuminance 조도 센서 값
     * @param pedometer 만보기 걸음 수
     * @param screenFrequency 화면 on/off 빈도 수
     * @param screenDuration 화면 사용 시간
     * @param gps 사용자 현재 위치 및 머무른 시간 (위도, 경도, 머무른 시간(min))
     * @param timestamp 수집 일자 (yyyy-MM-dd)
     * @param hour 수집 시간대 (24시간)
     * @param minTimestamp 저장된 데이터 중 첫 번째 timestamp
     */
    private fun requestSaveData(
        id: String,
        illuminance: ArrayList<Int>,
        pedometer: Int,
        screenFrequency: Int,
        screenDuration: Int,
        gps: ArrayList<Array<Double>>,
        timestamp: String,
        hour: Int,
        minTimestamp: Long
    ) {


    }

    /**
     * Remove Data And Reset Tracking Start Time
     *
     * @param timestamp
     */
    private fun removeData(timestamp: Long) {

        CoroutineScope(Dispatchers.IO).launch {

            TrackingDataUtil.removeData(timestamp)
        }
    }
}