package com.hongmei.garbagesort.map

import android.os.Bundle
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.AMap.OnMarkerClickListener
import com.amap.api.maps.CameraUpdate
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.amap.api.navi.AmapNaviPage
import com.amap.api.navi.AmapNaviParams
import com.amap.api.navi.AmapNaviType
import com.amap.api.navi.AmapPageType
import com.hongmei.garbagesort.GlobalData
import com.hongmei.garbagesort.R
import com.hongmei.garbagesort.base.BaseFragment
import com.hongmei.garbagesort.ext.appContext
import com.hongmei.garbagesort.ext.toastError
import kotlinx.android.synthetic.main.map_fragment.*
import org.angmarch.views.OnSpinnerItemSelectedListener
import kotlin.random.Random


/**
 * Date: 2021-02-02
 * Desc: 地图页面
 */
class MapFragment : BaseFragment<MapViewModel>(), AMapLocationListener, OnMarkerClickListener, AMap.OnInfoWindowClickListener {
    // 测试数据 蓝垃圾桶位置
    private val blueMarkerList = ArrayList<MarkerOptions>()

    // 测试数据 绿垃圾桶位置
    private val greenMarkerList = ArrayList<MarkerOptions>()

    // 测试数据 红垃圾桶位置
    private val redMarkerList = ArrayList<MarkerOptions>()

    // 测试数据 灰垃圾桶位置
    private val greyMarkerList = ArrayList<MarkerOptions>()

    private val allMarkerList = ArrayList<MarkerOptions>()

    /**
     * 声明AMapLocationClient类对象
     */
    private val locationClient by lazy {
        AMapLocationClient(appContext)
            .apply {
                setLocationListener(this@MapFragment)
            }
    }

    private val locationOption by lazy {
        AMapLocationClientOption()
            .apply {
                // 设置高精度定位模式
                locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
                // 设置只定位一次
                isOnceLocation = true
                // 设置返回地址信息
                isNeedAddress = true
            }
    }

    override fun layoutId(): Int {
        return R.layout.map_fragment
    }

    override fun initView(savedInstanceState: Bundle?) {
        mapView.onCreate(savedInstanceState)
        locationClient.setLocationOption(locationOption)
        val typeList = listOf("展示所有垃圾桶", "仅展示可回收垃圾桶", "仅展示湿垃圾桶", "仅展示有害垃圾桶", "仅展示干垃圾桶")
        mapSpinner.attachDataSource(typeList)
        mapSpinner.onSpinnerItemSelectedListener = OnSpinnerItemSelectedListener { _, _, position, _ ->
            addMarkers(position)
        }
        mapView.map.setOnMarkerClickListener(this)
        mapView.map.addOnInfoWindowClickListener(this)
    }

    private fun addMarkers(position: Int) {
        mapView.map.clear()
        when (position) {
            0 -> mapView.map.addMarkers(allMarkerList, false)
            1 -> mapView.map.addMarkers(blueMarkerList, false)
            2 -> mapView.map.addMarkers(greenMarkerList, false)
            3 -> mapView.map.addMarkers(redMarkerList, false)
            4 -> mapView.map.addMarkers(greyMarkerList, false)
        }
    }

    override fun onResume() {
        super.onResume()
        locationClient.startLocation()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        locationClient.stopLocation()
        mapView?.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
        locationClient.onDestroy()
    }

    override fun onLocationChanged(aMapLocation: AMapLocation?) {
        if (aMapLocation == null || aMapLocation.errorCode != 0) {
            toastError("定位失败，请确认定位权限已经开启")
            return
        }
        // 定位成功，获取信息
        GlobalData.currentLocation = aMapLocation
        // 切换到定位中心点
        val center = LatLng(aMapLocation.latitude, aMapLocation.longitude)
        val newCameraPosition = CameraUpdateFactory.newCameraPosition(CameraPosition(center, 16F, 30F, 0F))
        changeCamera(newCameraPosition)
        // 垃圾桶数据
        initMarkerListFromCenter(center)
    }

    private fun initMarkerListFromCenter(center: LatLng) {
        for (i in 0 until 50) {
            val newPoint = LatLng(center.latitude + 0.001 * Random.nextInt(-30, 30), center.longitude + 0.001 * Random.nextInt(-30, 30))
            val marker = MarkerOptions()
                .position(newPoint)
                .draggable(true)
                .title("到这去")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_trash_blue))
            blueMarkerList.add(marker)
        }
        for (i in 0 until 50) {
            val newPoint = LatLng(center.latitude + 0.001 * Random.nextInt(-30, 30), center.longitude + 0.001 * Random.nextInt(-30, 30))
            val marker = MarkerOptions()
                .position(newPoint)
                .draggable(true)
                .title("到这去")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_trash_green))
            greenMarkerList.add(marker)
        }
        for (i in 0 until 50) {
            val newPoint = LatLng(center.latitude + 0.001 * Random.nextInt(-30, 30), center.longitude + 0.001 * Random.nextInt(-30, 30))
            val marker = MarkerOptions()
                .position(newPoint)
                .draggable(true)
                .title("到这去")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_trash_red))
            redMarkerList.add(marker)
        }
        for (i in 0 until 50) {
            val newPoint = LatLng(center.latitude + 0.001 * Random.nextInt(-30, 30), center.longitude + 0.001 * Random.nextInt(-30, 30))
            val marker = MarkerOptions()
                .position(newPoint)
                .draggable(true)
                .title("到这去")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_trash_grey))
            greyMarkerList.add(marker)
        }
        allMarkerList.addAll(blueMarkerList)
        allMarkerList.addAll(greenMarkerList)
        allMarkerList.addAll(redMarkerList)
        allMarkerList.addAll(greyMarkerList)
        addMarkers(mapSpinner.selectedIndex)
    }


    /**
     * 根据动画按钮状态，调用函数animateCamera或moveCamera来改变可视区域
     */
    private fun changeCamera(update: CameraUpdate) {
        mapView.map.animateCamera(update)
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        // 点击垃圾桶 marker 的回调
        if (marker == null) {
            return false
        }
        marker.showInfoWindow()
        return true
    }

    override fun onInfoWindowClick(marker: Marker?) {
        if (marker == null) {
            return
        }
        // 终点
        val end = Poi("目的地", marker.position, null)
        // 组件参数配置
        val params = AmapNaviParams(null, null, end, AmapNaviType.DRIVER, AmapPageType.ROUTE)
        // 启动组件
        AmapNaviPage.getInstance().showRouteActivity(appContext, params, null)
    }

}