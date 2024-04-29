package com.imp.presentation.view.main.fragment

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.imp.presentation.R
import com.imp.presentation.base.BaseFragment
import com.imp.presentation.constants.BaseConstants
import com.imp.presentation.databinding.FrgAnalysisBinding
import com.imp.presentation.view.adapter.AnalysisListAdapter
import com.imp.presentation.view.main.activity.ActMain
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.shape.DotPoints
import com.kakao.vectormap.shape.Polygon
import com.kakao.vectormap.shape.PolygonOptions


/**
 * Main - Analysis Fragment
 */
class FrgAnalysis: BaseFragment<FrgAnalysisBinding>() {

    /** Analysis List Adapter */
    private lateinit var analysisAdapter: AnalysisListAdapter

    /** 지도 관련 변수 */
    private var kakaoMap: KakaoMap? = null
    private var areaPolygon: Polygon? = null

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) = FrgAnalysisBinding.inflate(inflater, container, false)

    override fun initData() {

    }

    override fun initView() {

        // set status bar color
        activity?.let { if (it is ActMain) it.setCurrentStatusBarColor(BaseConstants.MAIN_NAV_LABEL_LOG) }

        initObserver()
        initDisplay()
        initViewPager()
        initMapView()
    }

    /**
     * Initialize Observer
     */
    private fun initObserver() {

    }

    /**
     * Initialize Display
     */
    private fun initDisplay() {

        with(mBinding) {

            // Header
            incHeader.tvTitle.text = getString(R.string.navigation_analysis)
            incHeader.ivAddChat.visibility = View.GONE
        }
    }

    /**
     * Initialize ViewPager
     */
    private fun initViewPager() {

        context?.let { ctx ->

            with(mBinding) {

                viewPager.apply {

                    val tempList = arrayListOf("스크린 타임", "화면 깨우기", "걸음", "통화 시간", "통화 횟수")

                    analysisAdapter = AnalysisListAdapter(ctx, tempList)
                    adapter = analysisAdapter
                    orientation = ViewPager2.ORIENTATION_HORIZONTAL
                    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)
                        }
                        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                        }
                    })

                    // indicator 적용
                    indicator.attachTo(this)
                }
            }
        }
    }

    /**
     * Initialize Map View
     */
    private fun initMapView() {

        with(mBinding) {

            // 이동 경로 map
            mapView.start(object : MapLifeCycleCallback() {
                override fun onMapDestroy() {}
                override fun onMapError(p0: Exception?) {}
            }, object : KakaoMapReadyCallback() {
                override fun onMapReady(map: KakaoMap) {

                    kakaoMap = map

                    // todo test
                    setAreaPolygon(LatLng.from(37.394660, 127.111182))
                }
            })
        }
    }

    /**
     * Set Area Polygon
     *
     * @param point
     */
    private fun setAreaPolygon(point: LatLng) {

        kakaoMap?.let { map ->

            areaPolygon = map.shapeManager?.layer?.addPolygon(getCircleOptions(point, 200))
            kakaoMap!!.moveCamera(
                CameraUpdateFactory.newCenterPosition(point, 15),
                CameraAnimation.from(300)
            )
        }
    }

    /**
     * Get Circle Polygon Option
     *
     * @param center
     * @param radius
     * @return
     */
    private fun getCircleOptions(center: LatLng, radius: Int): PolygonOptions {

        return PolygonOptions.from(
            DotPoints.fromCircle(center, radius.toFloat()),
            Color.parseColor("#078c03")
        )
    }
}