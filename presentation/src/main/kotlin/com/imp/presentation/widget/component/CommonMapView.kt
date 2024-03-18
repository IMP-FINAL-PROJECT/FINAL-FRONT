package com.imp.presentation.widget.component

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.imp.presentation.R
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapPolyline
import net.daum.mf.map.api.MapView

/**
 * Common MapView
 */
class CommonMapView: MapView {

    constructor(context: Context?) : super(context) {
        initDisplay()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initDisplay()
    }
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initDisplay()
    }

    private fun initDisplay() {

        setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.537906, 127.046245), false)
        setZoomLevel(7, false)

        // 현재 위치로 이동
        //currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
    }

    /**
     * Add Polyline
     *
     * @param pointList
     */
    fun addPolyline(pointList: ArrayList<MapPoint>) {

        MapPolyline().apply {

            lineColor = ContextCompat.getColor(context, R.color.color_3377ff)
            pointList.forEach { addPoint(it) }

            addPolyline(this)
        }
    }

    /**
     * Add Markers
     *
     * @param pointList
     */
    fun addMarker(pointList: ArrayList<MapPoint>) {
        pointList.forEach { addPOIItem(getDefaultMarker(it)) }
    }

    /**
     * Get Default Marker
     *
     * @param point
     * @return
     */
    private fun getDefaultMarker(point: MapPoint): MapPOIItem {

        return MapPOIItem().apply {

            itemName = "Custom Marker"
            mapPoint = point
            markerType = MapPOIItem.MarkerType.BluePin
            selectedMarkerType = MapPOIItem.MarkerType.RedPin
            setCustomImageAnchor(
                0.5f,
                1.0f
            )
        }
    }
}