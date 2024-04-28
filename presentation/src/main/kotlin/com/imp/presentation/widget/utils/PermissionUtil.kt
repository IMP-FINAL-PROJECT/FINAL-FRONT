package com.imp.presentation.widget.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

/**
 * Permission Util
 */
object PermissionUtil {

    /**
     * 필요한 모든 권한 허용 여부 확인
     *
     * @param context
     * @return 모든 권한 허용 여부 반환 (필수 - 위치, 활동, 전화)
     */
    fun checkPermissions(context: Context): Boolean {
        return checkPermissionLocation(context) && checkPermissionBackgroundLocation(context) && checkPermissionActivity(context) && checkPermissionCall(context)
    }

    /**
     * 알림 권한 허용 여부 확인
     *
     * @param context
     * @return 권한 허용 여부 반환
     */
    fun checkPermissionNotification(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    /**
     * 위치 권한 허용 여부 확인
     *
     * @param context
     * @return 권한 허용 여부 반환
     */
    fun checkPermissionLocation(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 백그라운드 위치 권한 허용 여부 확인
     *
     * @param context
     * @return 권한 허용 여부 반환
     */
    fun checkPermissionBackgroundLocation(context: Context): Boolean {

        // SDK 31부터 위치 권한을 요청한 이후에 Background Location 권한을 요청 해야 함
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 활동 권한 허용 여부 확인
     *
     * @param context
     * @return 권한 허용 여부 반환
     */
    fun checkPermissionActivity(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 전화 권한 허용 여부 확인
     *
     * @param context
     * @return 권한 허용 여부 반환
     */
    fun checkPermissionCall(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 외부 저장소 접근 권한 허용 여부 확인
     *
     * @param context
     * @return 권한 허용 여부 반환
     */
    fun checkPermissionStorage(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * 알림 권한 요청
     *
     * @param activity
     * @param launcher 결과를 처리할 ActivityResultLauncher
     * @param deniedLauncher 권한이 거부된 경우를 처리할 ActivityResultLauncher
     */
    fun requestPermissionNotification(activity: Activity, launcher: ActivityResultLauncher<String>, deniedLauncher: ActivityResultLauncher<Intent>, callback: () -> Unit = {}) {

        // OS 13 이전일 경우, 알림 권한 없음
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        // 권한이 허용된 경우 return
        if (checkPermissionNotification(activity)) return

        if (!activity.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {

            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)

        } else {

            callback.invoke()

            // 권한을 거부한 경우, 설정으로 이동
//            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//            intent.data = Uri.parse(String.format("package:%s", activity.packageName))
//            deniedLauncher.launch(intent)
        }
    }

    /**
     * 위치 권한 요청
     *
     * @param activity
     * @param multipleLauncher 결과를 처리할 ActivityResultLauncher
     * @param deniedLauncher 권한이 거부된 경우를 처리할 ActivityResultLauncher
     */
    fun requestPermissionLocation(activity: Activity, multipleLauncher: ActivityResultLauncher<Array<String>>, deniedLauncher: ActivityResultLauncher<Intent>, callback: (() -> Unit) -> Unit) {

        // 권한이 허용된 경우 return
        if (checkPermissionLocation(activity)) return

        if (!activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
            && !activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {

            val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            multipleLauncher.launch(permissions)

        } else {

            callback.invoke {

                CommonUtil.log("ACCESS_FINE_LOCATION: ${activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)}")
                CommonUtil.log("ACCESS_COARSE_LOCATION: ${activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)}")

                // 권한을 거부한 경우, 설정으로 이동
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse(String.format("package:%s", activity.packageName))
                deniedLauncher.launch(intent)
            }
        }
    }

    /**
     * 백그라운드 위치 권한 요청
     *
     * @param activity
     * @param launcher 결과를 처리할 ActivityResultLauncher
     * @param deniedLauncher 권한이 거부된 경우를 처리할 ActivityResultLauncher
     */
    fun requestPermissionBackgroundLocation(activity: Activity, launcher: ActivityResultLauncher<String>, deniedLauncher: ActivityResultLauncher<Intent>) {

        // 권한이 허용된 경우 return
        if (checkPermissionBackgroundLocation(activity)) return

        if (!activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {

            launcher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)

        } else {

            // 권한을 거부한 경우, 설정으로 이동
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse(String.format("package:%s", activity.packageName))
            deniedLauncher.launch(intent)
        }
    }

    /**
     * 활동 권한 요청
     *
     * @param activity
     * @param launcher 결과를 처리할 ActivityResultLauncher
     * @param deniedLauncher 권한이 거부된 경우를 처리할 ActivityResultLauncher
     */
    fun requestPermissionActivity(activity: Activity, launcher: ActivityResultLauncher<String>, deniedLauncher: ActivityResultLauncher<Intent>, callback: (() -> Unit) -> Unit) {

        // 권한이 허용된 경우 return
        if (checkPermissionActivity(activity)) return

        if (!activity.shouldShowRequestPermissionRationale(Manifest.permission.ACTIVITY_RECOGNITION)) {

            launcher.launch(Manifest.permission.ACTIVITY_RECOGNITION)

        } else {

            callback.invoke {

                // 권한을 거부한 경우, 설정으로 이동
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse(String.format("package:%s", activity.packageName))
                deniedLauncher.launch(intent)
            }
        }
    }

    /**
     * 전화 권한 요청
     *
     * @param activity
     * @param launcher 결과를 처리할 ActivityResultLauncher
     * @param deniedLauncher 권한이 거부된 경우를 처리할 ActivityResultLauncher
     */
    fun requestPermissionCall(activity: Activity, launcher: ActivityResultLauncher<String>, deniedLauncher: ActivityResultLauncher<Intent>, callback: (() -> Unit) -> Unit) {

        // 권한이 허용된 경우 return
        if (checkPermissionCall(activity)) return

        if (!activity.shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {

            launcher.launch(Manifest.permission.READ_PHONE_STATE)

        } else {

            callback.invoke {

                // 권한을 거부한 경우, 설정으로 이동
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse(String.format("package:%s", activity.packageName))
                deniedLauncher.launch(intent)
            }
        }
    }

    /**
     * 외부 저장소 접근 권한 요청
     *
     * @param activity
     * @param launcher 결과를 처리할 ActivityResultLauncher
     * @param multipleLauncher 권한이 거부된 경우를 처리할 ActivityResultLauncher
     */
    fun requestPermissionStorage(activity: Activity, launcher: ActivityResultLauncher<Intent>, multipleLauncher: ActivityResultLauncher<Array<String>>) {

        // 권한이 허용된 경우 return
        if (checkPermissionStorage(activity)) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            try {

                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(String.format("package:%s", activity.packageName))
                launcher.launch(intent)

            } catch (e: Exception) {

                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                launcher.launch(intent)
            }

        } else {

            multipleLauncher.launch(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }
    }
}