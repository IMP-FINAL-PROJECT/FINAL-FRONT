package com.imp.presentation.widget.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Spannable
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.TypefaceSpan
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.imp.data.tracking.service.TrackingForegroundService
import com.imp.presentation.widget.component.CustomTypefaceSpan
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File
import java.util.Locale

/**
 * Method Storage Util
 */
class MethodStorageUtil {

    companion object {

        /**
         * 이미지 출력 (rxJava)
         *
         * @param imageView
         * @param bitmap
         * @param completeCallback
         */
        @SuppressLint("CheckResult")
        fun rxJavaSetImage(imageView: ImageView, bitmap: Bitmap?, completeCallback: (() -> Unit)) {

            if (imageView.context == null) {
                CommonUtil.log("imageView context is null")
                return
            }

            if (bitmap == null) {
                CommonUtil.log("Bitmap is null")
                return
            }

            Observable.create { emitter: ObservableEmitter<Drawable> ->
                try {
                    val drawable = Glide.with(imageView.context)
                        .load(bitmap)
                        .override(imageView.width, imageView.height)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontAnimate()
                        .submit()
                        .get()

                    emitter.onNext(drawable)
                    emitter.onComplete()
                } catch (e: Exception) {
                    emitter.onError(e)
                }
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { drawable ->
                        imageView.setImageDrawable(drawable)
                        imageView.visibility = View.VISIBLE
                        completeCallback.invoke()
                    },
                    { error ->
                        error.printStackTrace()
                    }
                )
        }

        /**
         * 이미지 출력 (Glide)
         *
         * @param imageView
         * @param url
         */
        fun setImageUrl(imageView: ImageView, url: String) {

            if (imageView.context == null) return

            try {

                Glide.with(imageView.context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .override(imageView.width, imageView.height)
                    .skipMemoryCache(true)
                    .dontAnimate()
                    .into(object : SimpleTarget<Drawable>() {
                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable>?
                        ) {

                            imageView.setImageDrawable(resource)
                        }
                    })

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * 이미지 출력 (Glide)
         *
         * @param imageView
         * @param file
         */
        fun setImageFile(imageView: ImageView, file: File) {

            if (imageView.context == null) return

            try {

                Glide.with(imageView.context)
                    .load(file)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .override(imageView.width, imageView.height)
                    .skipMemoryCache(true)
                    .dontAnimate()
                    .into(object : SimpleTarget<Drawable>() {
                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable>?
                        ) {

                            imageView.setImageDrawable(resource)
                        }
                    })

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * 이미지 출력 (Glide)
         *
         * @param imageView
         * @param bitmap
         * @param glide
         */
        fun setImageBitmap(imageView: AppCompatImageView, bitmap: Bitmap?, glide: RequestManager) {

            val placeholderDrawable = BitmapDrawable(imageView.resources, bitmap)

            glide.asBitmap()
                .load(bitmap)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .skipMemoryCache(true)
                .placeholder(placeholderDrawable)
                .override(imageView.width, imageView.height)
                .into(imageView)
        }

        /**
         * GIF 출력 (Glide)
         *
         * @param context
         * @param glide
         * @param imageFile
         * @param preview
         * @param gifView
         */
        fun setGIF(context: Context?, glide: RequestManager, imageFile: File?, preview: ImageView, gifView: ImageView) {

            if (context == null) return

            imageFile?.let { file ->

                val options = RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .override(gifView.width, gifView.height)
                    .skipMemoryCache(true)

                glide
                    .asGif()
                    .load(file)
                    .apply(options)
                    .listener(object : RequestListener<GifDrawable> {

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<GifDrawable>,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: GifDrawable,
                            model: Any,
                            target: Target<GifDrawable>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {

                            preview.visibility = View.VISIBLE
                            return false
                        }
                    })
                    .into(object : CustomViewTarget<ImageView, GifDrawable>(gifView) {

                        override fun onResourceReady(
                            resource: GifDrawable,
                            transition: Transition<in GifDrawable>?
                        ) {

                            preview.visibility = View.GONE
                            resource.start()
                            gifView.setImageDrawable(resource)
                        }

                        override fun onLoadFailed(errorDrawable: Drawable?) {}
                        override fun onResourceCleared(placeholder: Drawable?) {}
                    })
            }
        }

        /**
         * 디바이스 언어 설정 가져오기
         */
        fun getSystemLanguage(): String = Locale.getDefault().language

        /**
         * 디바이스 국가 설정 가져오기
         */
        fun getSystemCountry(): String = Locale.getDefault().country

        /**
         * 앱 버전
         */
        fun getPackageVersion(context: Context): String? {

            return try {
                val pInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                pInfo.versionName
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                null
            }
        }

        /**
         * 앱 설치 여부 확인
         *
         * @param context
         * @param packageName
         */
        fun isAppInstalled(context: Context, packageName: String): Boolean {

            val packageManager = context.packageManager
            return try {
                packageManager.getApplicationInfo(packageName, 0)
                true
            } catch (e: Exception) {
                false
            }
        }

        /**
         * Tracking Service 실행 여부 확인
         *
         * @param context
         */
        fun isServiceRunning(context: Context): Boolean {

            try {

                val activityManager = context.getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager
                val serviceList = activityManager.getRunningServices(Int.MAX_VALUE)
                for (service in serviceList) {

                    if (service.service.className == TrackingForegroundService::class.java.name) {
                        return true
                    }
                }

                return false

            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }

        /**
         * 클릭 시 해당 앱 이동
         *
         * @param context
         * @param pkg
         */
        fun moveToApp(context: Context, pkg: String) {

            val options = Bundle()

            try {
                context.startActivity(context.packageManager.getLaunchIntentForPackage(pkg), options)
            } catch (e: NullPointerException) {
                if (pkg.isEmpty()) {
                    Toast.makeText(context, "패키지명이 없습니다", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "not installed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        /**
         * Touch Vibrate
         *
         * @param context
         * @param view
         */
        fun createVibRate(context: Context?, view: View) {

            context?.let {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                    val vibrator = it.getSystemService(Vibrator::class.java)
                    vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                } else {

                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                }
            }
        }

        /**
         * Set Spannable
         * @param textView 텍스트 뷰
         * @param start 시작 index
         * @param end 끝 index
         * @param size 폰트 사이즈
         */
        fun setSpannable(textView: TextView, start: Int, end: Int, size: Int) {

            val spannable = textView.text as Spannable
            spannable.setSpan(AbsoluteSizeSpan(size), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        /**
         * Set Spannable
         * @param textView 텍스트 뷰
         * @param start 시작 index
         * @param end 끝 index
         * @param font 폰트
         */
        fun setSpannable(textView: TextView, start: Int, end: Int, size: Int, font: Int) {

            val spannable = textView.text as Spannable
            spannable.setSpan(AbsoluteSizeSpan(size), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            val font = Typeface.create(ResourcesCompat.getFont(textView.context, font), Typeface.NORMAL)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                spannable.setSpan(TypefaceSpan(font), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            } else {
                spannable.setSpan(CustomTypefaceSpan(font), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        /**
         * Get StatusBar Height
         *
         * @param context
         * @return Int
         */
        @SuppressLint("DiscouragedApi", "InternalInsetResource")
        fun getStatusBarHeight(context: Context): Int {

            var statusBarHeight = 0
            val resourceId: Int = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) statusBarHeight = context.resources.getDimensionPixelSize(resourceId)

            return statusBarHeight
        }
    }
}