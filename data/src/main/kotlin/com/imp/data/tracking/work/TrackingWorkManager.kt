package com.imp.data.tracking.work

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.imp.data.remote.api.ApiTracking
import com.imp.data.tracking.util.DateUtil
import com.imp.data.tracking.util.TrackingDataUtil
import com.imp.data.tracking.util.resetCalendarTime
import com.imp.data.util.ApiClient
import com.imp.data.util.BaseResponse
import com.imp.data.tracking.data.SensorDataStore
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * Tracking WorkManager
 */
@SuppressLint("CheckResult")
class TrackingWorkManager(private val context: Context, params: WorkerParameters) : Worker(context, params) {

    companion object {
        private const val TAG = "tracking"
    }

    private var mDisposable: CompositeDisposable? = null
    private var dataJob: Job? = null

    init { mDisposable = CompositeDisposable() }

    override fun doWork(): Result {

        readTrackingData()

        return Result.success()
    }

    /**
     * Read Tracking Data
     */
    private fun readTrackingData() {

        dataJob?.cancel()
        dataJob = CoroutineScope(Dispatchers.IO).launch {

            // get data
            val illuminance = async { TrackingDataUtil.getIlluminance(context) }.await()
            var pedometer = async { TrackingDataUtil.getPedometer(context) }.await()
            val gps = async { TrackingDataUtil.getGps(context) }.await()
            var screen = async { TrackingDataUtil.getScreenTime(context) }.await()
            var call = async { TrackingDataUtil.getPhoneCall(context) }.await()

            val minTimestamp = minOf(illuminance.first, pedometer.first, gps.first, screen.first, call.first)
            val hour = DateUtil.getHour(minTimestamp)
            Log.d(TAG, "hour: $hour")

            // 최소 시간대 보다 구한 시간 대가 이후일 경우, 데이터 초기화
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

            } else {

                if (gps.second.isNotEmpty()) {

                    // 마지막 위치 저장 (최근 위치)
                    val dao = gps.second.last()
                    if (dao.size >= 2) {
                        SensorDataStore.saveRecentGps(context, dao[0], dao[1])
                    }
                }
            }
            if (screen.first != -1L && DateUtil.getHour(screen.first) > hour) {

                Log.d(TAG, "screen first: ${screen.first}, ${DateUtil.getHour(screen.first)}")
                Log.d(TAG, "screen clear, ${DateUtil.timestampToDataMin(screen.first)}")

                screen = Triple(minTimestamp, 0, 0)
            }
            if (call.first != -1L && DateUtil.getHour(call.first) > hour) {

                Log.d(TAG, "call first: ${call.first}, ${DateUtil.getHour(call.first)}")
                Log.d(TAG, "call clear, ${DateUtil.timestampToDataMin(call.first)}")

                call = Triple(minTimestamp, 0, 0)
            }

            // save tracking data
            requestSaveData(
                illuminance = illuminance.second,
                pedometer = pedometer.second,
                screenDuration = screen.second,
                screenFrequency = screen.third,
                callDuration = call.second,
                callFrequency = call.third,
                gps = gps.second,
                timestamp = DateUtil.timestampToData(minTimestamp),
                hour = DateUtil.getHour(minTimestamp),
                minTimestamp = minTimestamp
            )
        }
    }

    /**
     * Request Save Data
     *
     * @param illuminance 조도 센서 값
     * @param pedometer 만보기 걸음 수
     * @param screenFrequency 화면 on/off 빈도 수
     * @param screenDuration 화면 사용 시간
     * @param callDuration 통화 시간
     * @param callFrequency 통화 빈도 수
     * @param gps 사용자 현재 위치 및 머무른 시간 (위도, 경도, 머무른 시간(min))
     * @param timestamp 수집 일자 (yyyy-MM-dd)
     * @param hour 수집 시간대 (24시간)
     * @param minTimestamp 저장된 데이터 중 첫 번째 timestamp
     */
    private fun requestSaveData(
        illuminance: ArrayList<Int>,
        pedometer: Int,
        screenFrequency: Int,
        screenDuration: Int,
        callDuration: Int,
        callFrequency: Int,
        gps: ArrayList<Array<Double>>,
        timestamp: String,
        hour: Int,
        minTimestamp: Long
    ) {

        val params: MutableMap<String, Any> = HashMap()

        params["id"] = getId(context)
        params["illuminance"] = illuminance
        params["pedometer"] = pedometer
        params["screen_frequency"] = screenFrequency
        params["screen_duration"] = screenDuration
        params["call_duration"] = callDuration
        params["call_frequency"] = callFrequency
        params["gps"] = gps
        params["timestamp"] = timestamp
        params["hour"] = hour

        mDisposable?.add(
            ApiClient.getClient().create(ApiTracking::class.java)
                .sensorData(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<BaseResponse<Any>>() {
                    override fun onComplete() {}
                    override fun onNext(t: BaseResponse<Any>) {

                        if (t.result) {

                            Log.d(TAG, "remove data")
                            removeData(minTimestamp)
                        }

                        dataJob?.cancel()
                    }
                    override fun onError(e: Throwable) { dataJob?.cancel() }
                })
        )
    }

    /**
     * Remove Data And Reset Tracking Start Time
     *
     * @param timestamp
     */
    private fun removeData(timestamp: Long) {

        CoroutineScope(Dispatchers.IO).launch {

            val remove = async { TrackingDataUtil.removeData(context, timestamp) }.await()
            if (remove) {

                SensorDataStore.getTrackingStartTime(context).firstOrNull()?.let { startTime ->

                    val startCalendar = Calendar.getInstance().apply {
                        timeInMillis = startTime
                        resetCalendarTime()
                    }

                    val currentCalendar = Calendar.getInstance().apply { resetCalendarTime() }
                    if (currentCalendar.timeInMillis > startCalendar.timeInMillis) {
                        Log.d(TAG, "reSave tracking data")

                        // (분, 초 제외) 현재 시간 > 시작 시간 -> 현재 시간 전 즉, 남아 있는 데이터 저장
                        readTrackingData()

                    } else return@launch
                }
            }
        }
    }

    /**
     * Get Id
     *
     * @param context
     * @return String
     */
    private fun getId(context: Context): String {
        return context.getSharedPreferences("com.imp.fluffymood", 0).getString("AUTO_LOGIN_ID_KEY", "") ?: ""
    }
}