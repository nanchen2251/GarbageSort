package com.hongmei.garbagesort.map

import android.os.Bundle
import android.view.animation.LinearInterpolator
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.AMap.OnMarkerClickListener
import com.amap.api.maps.CameraUpdate
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.amap.api.maps.model.animation.AlphaAnimation
import com.amap.api.maps.model.animation.Animation
import com.amap.api.maps.model.animation.AnimationSet
import com.amap.api.maps.model.animation.ScaleAnimation
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

    private var breatheMarker: MarkerOptions? = null
    private var breatheMarkerCenter: MarkerOptions? = null

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
        val typeList = listOf("展示所有垃圾桶", "仅展示可回收垃圾桶", "仅展示有害垃圾桶", "仅展示厨余垃圾桶", "仅展示其他垃圾桶")
        mapSpinner.attachDataSource(typeList)
        mapSpinner.onSpinnerItemSelectedListener = OnSpinnerItemSelectedListener { _, _, position, _ ->
            addMarkers(position)
        }
        mapView.map.setOnMarkerClickListener(this)
        mapView.map.addOnInfoWindowClickListener(this)
    }

    private fun addMarkers(position: Int) {
        mapView.map.clear()
        addCurrentPointMarker()
        when (position) {
            0 -> mapView.map.addMarkers(allMarkerList, false)
            1 -> mapView.map.addMarkers(blueMarkerList, false)
            2 -> mapView.map.addMarkers(redMarkerList, false)
            3 -> mapView.map.addMarkers(greenMarkerList, false)
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

    private fun addCurrentPointMarker() {
        GlobalData.currentLocation?.run {
            // 呼吸动画
            if (breatheMarker == null) {
                val latLng = LatLng(latitude, longitude)
                breatheMarker = MarkerOptions()
                    .position(latLng)
                    .zIndex(1f)
                    .anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_circle_64))
                // 中心的marker
                breatheMarkerCenter = MarkerOptions()
                    .position(latLng)
                    .zIndex(2f)
                    .anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_circle_64))

            }

            // 动画执行完成后，默认会保持到最后一帧的状态
            val animationSet = AnimationSet(true)
            val alphaAnimation = AlphaAnimation(0.5f, 0f)
            alphaAnimation.setDuration(2000)
            // 设置不断重复
            alphaAnimation.repeatCount = Animation.INFINITE
            val scaleAnimation = ScaleAnimation(1F, 3.5f, 1F, 3.5f)
            scaleAnimation.setDuration(2000)
            // 设置不断重复
            scaleAnimation.repeatCount = Animation.INFINITE
            animationSet.addAnimation(alphaAnimation)
            animationSet.addAnimation(scaleAnimation)
            animationSet.setInterpolator(LinearInterpolator())
            mapView.map.addMarker(breatheMarkerCenter)
            mapView.map.addMarker(breatheMarker).run {
                setAnimation(animationSet)
                startAnimation()
            }
        }
    }


    override fun onLocationChanged(aMapLocation: AMapLocation?) {
        if (aMapLocation == null || aMapLocation.errorCode != 0) {
            toastError("定位失败，请确认定位权限已经开启")
            return
        }
        // 切换到定位中心点
        val center = LatLng(aMapLocation.latitude, aMapLocation.longitude)
        val newCameraPosition = CameraUpdateFactory.newCameraPosition(CameraPosition(center, 17F, 30F, 0F))
        changeCamera(newCameraPosition)
        if (GlobalData.currentLocation == null) {
            // 垃圾桶数据
            initMarkerListFromCenter(center)
        }

        // 定位成功，获取信息
        GlobalData.currentLocation = aMapLocation
        addCurrentPointMarker()


    }

    private fun initMarkerListFromCenter(center: LatLng) {
        blueMarkerList.clear()
        allMarkerList.clear()
        greyMarkerList.clear()
        blueMarkerList.clear()
        redMarkerList.clear()

        // 生成随机数
        val latNums = ArrayList<Int>()
        val lngNums = ArrayList<Int>()
        for (i in 0 until 100) {
            val randomLat = Random.nextInt(-100, 100)
            val randomLng = Random.nextInt(-100, 100)
            if (!latNums.contains(randomLat) && !lngNums.contains(randomLng)) {
                latNums.add(randomLat)
                lngNums.add(randomLng)
            }
        }

        // 生成随机点
        val pointList = ArrayList<LatLng>()
        for (i in 0 until latNums.size) {
            val lat = center.latitude + 0.0001 * latNums[i]
            val lng = center.longitude + 0.0001 * lngNums[i]
            pointList.add(LatLng(lat, lng))
        }

        // 随机垃圾桶
        for (i in 0 until pointList.size) {
            // 双数为四色垃圾桶
            // 单数只有双色垃圾桶
            var newPoint = pointList[i]
            val marker = MarkerOptions()
                .position(newPoint)
                .draggable(true)
                .title("到这去")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_trash_blue))
            blueMarkerList.add(marker)

            if (i % 2 == 0) {
                newPoint = LatLng(newPoint.latitude, newPoint.longitude - 0.0002)
                redMarkerList.add(
                    MarkerOptions()
                        .position(newPoint)
                        .draggable(true)
                        .title("到这去")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_trash_red))
                )

                newPoint = LatLng(newPoint.latitude, newPoint.longitude - 0.0002)
                greenMarkerList.add(
                    MarkerOptions()
                        .position(newPoint)
                        .draggable(true)
                        .title("到这去")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_trash_green))
                )
            }

            newPoint = LatLng(newPoint.latitude, newPoint.longitude - 0.0002)
            greyMarkerList.add(
                MarkerOptions()
                    .position(newPoint)
                    .draggable(true)
                    .title("到这去")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_trash_grey))
            )
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