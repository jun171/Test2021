package tw.tcnr22.m1706

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.location.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

abstract class M1706 : AppCompatActivity(), LocationListener {
    private var mSpnLocation: Spinner? = null

    // 自建的html檔名
    private var webView: WebView? = null
    private var Lat: String? = null
    private var Lon: String? = null
    private var jcontent // 地名變數
            : String? = null

    /*** GPS */
    private var locationMgr: LocationManager? = null
//    private var provider // 提供資料
//            : String? = null
    private var provider = ""
    private var txtOutput: TextView? = null
    var iSelect = 0
    private val TAG = "oldpa=>"

    /*** Navigation  */
    private var bNav: Button? = null
//    var sLocation: Array<String>
var sLocation: Array<String> = emptyArray()

    var Navon = "off"
    var Navstart = "24.172127,120.610313" // 起始點
    var Navend = "24.179051,120.600610" // 結束點
    var aton: String? = null
    private val permissionsList: MutableList<String> = ArrayList()

    //-----------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.m1706)
        checkRequiredPermission(this) //  檢查SDK版本, 確認是否獲得權限.
        setupViewComponent()
    }

    private fun checkRequiredPermission(activity: Activity) {
        for (permission in permissionsArray) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsList.add(permission)
            }
        }
        if (permissionsList.size != 0) {
            ActivityCompat.requestPermissions(
                activity,
                permissionsList.toTypedArray(),
                REQUEST_CODE_ASK_PERMISSIONS
            )
        }
    }

    private fun setupViewComponent() {
        mSpnLocation = findViewById<View>(R.id.spnLocation) as Spinner
        // ----Location-----------
        val adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item
        )
        for (i in locations.indices) adapter.add(locations[i][0])
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mSpnLocation!!.adapter = adapter
        mSpnLocation!!.onItemSelectedListener = mSpnLocationOnItemSelLis
        // ---------------------------------
        webView = findViewById<View>(R.id.webView) as WebView
        txtOutput = findViewById<View>(R.id.txtOutput) as TextView
        //		--導航監聽--
        bNav = findViewById<View>(R.id.Navigation) as Button
        bNav!!.setOnClickListener(bNavselectOn)
    }

    //	--導航監聽--
    private val bNavselectOn = View.OnClickListener {
        if (Navon === "off") {
            bNav!!.setTextColor(resources.getColor(R.color.Blue))
            Navon = "on"
            bNav!!.text = "關閉路徑規劃"
            setMapLocation()
        } else {
            bNav!!.setTextColor(resources.getColor(R.color.Red))
            Navon = "off"
            bNav!!.text = "開啟路徑規劃"
            setMapLocation()
        }
    }
    private val mSpnLocationOnItemSelLis: AdapterView.OnItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, v: View, position: Int, id: Long) {
                Navend = locations[position][1]
                setMapLocation()
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }

    private fun setMapLocation() {
        iSelect = mSpnLocation!!.selectedItemPosition
        sLocation = locations[iSelect][1].split(",").toTypedArray()
        Lat = sLocation[0] // 南北緯
        Lon = sLocation[1] // 東西經
        jcontent = locations[iSelect][0] // 地名
        //---------增加判斷是否規畫路徑------------------
        if (Navon === "on" && iSelect != 0) {
            Navstart = locations[0][1]
            //            Navend = locations[iSelect][1];
            val RoutePlanningOverlays = "javascript: RoutePlanning()"
            webView!!.loadUrl(RoutePlanningOverlays)
        } else {
            webView!!.settings.javaScriptEnabled = true
            webView!!.addJavascriptInterface(this@M1706, "AndroidFunction")
            webView!!.loadUrl(MAP_URL)
        }

//-------------------------------------------------------------
    }

    // -------------------------------
    /* 開啟時先檢查是否有啟動GPS精緻定位 */
    override fun onStart() {
        super.onStart()
        if (initLocationProvider()) {
            nowaddress()
        } else {
            txtOutput!!.text = "GPS未開啟,請先開啟定位！"
        }
    }

    override fun onStop() {
        locationMgr!!.removeUpdates(this)
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
    }

    /************************************************
     * GPS部份
     */
    /* 檢查GPS 設定GPS服務 */
    private fun initLocationProvider(): Boolean {
        locationMgr = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationMgr!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER
            return true
        }
        return false
    }

    /* 建立位置改變偵聽器 預先顯示上次的已知位置 */
    private fun nowaddress() {
        // 取得上次已知的位置
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(application, "No Permission", Toast.LENGTH_LONG).show()
            return
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(application, "No Permission", Toast.LENGTH_LONG).show()
            return
        }
        val location = locationMgr!!.getLastKnownLocation(provider)
        updateWithNewLocation(location)

        // 監聽 GPS Listener
        locationMgr!!.addGpsStatusListener(gpsListener)

        // Location Listener
        val minTime: Long = 5000 // ms
        val minDist = 5.0f // meter
        locationMgr!!.requestLocationUpdates(
            provider, minTime, minDist,
            this
        )
    }

    var gpsListener =
        GpsStatus.Listener { event ->

            /* 監聽GPS 狀態 */
            when (event) {
                GpsStatus.GPS_EVENT_STARTED -> Log.d(TAG, "GPS_EVENT_STARTED")
                GpsStatus.GPS_EVENT_STOPPED -> Log.d(TAG, "GPS_EVENT_STOPPED")
                GpsStatus.GPS_EVENT_FIRST_FIX -> Log.d(TAG, "GPS_EVENT_FIRST_FIX")
                GpsStatus.GPS_EVENT_SATELLITE_STATUS -> Log.d(TAG, "GPS_EVENT_SATELLITE_STATUS")
            }
        }

    private fun updateWithNewLocation(location: Location?) {
        var where = ""
        if (location != null) {
            val lng = location.longitude // 經度
            val lat = location.latitude // 緯度
            //----------------------------
            Lat = lat.toString() + ""
            Lon = lng.toString() + ""
            val speed = location.speed // 速度
            val time = location.time // 時間
            val timeString = getTimeString(time)
            where = """
                經度: $lng
                緯度: $lat
                速度: $speed
                時間: $timeString
                Provider: $provider
                """.trimIndent()
            // 標記"我的位置"
            locations[0][1] = "$lat,$lng" // 用GPS找到的位置更換 陣列的目前位置
        } else {
            where = "*位置訊號消失*"
        }
        // 位置改變顯示
        txtOutput!!.text = where
    }

    private fun getTimeString(timeInMilliseconds: Long): String {
        var format: SimpleDateFormat? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        }
        return format!!.format(timeInMilliseconds)
    }

    /* 位置變更狀態監視 */
    override fun onLocationChanged(location: Location) {
//        //定位改變時
//        updateWithNewLocation(location);
//        //將畫面移至定位點的位置
//        aton = location.getLatitude() + "," + location.getLongitude();
//        final String centerURL = "javascript:centerAt(" +
//                location.getLatitude() + "," + location.getLongitude() + ")";
//        webView.loadUrl(centerURL);
//
//        final String deleteOverlays = "javascript:deleteOverlays()";
//        webView.loadUrl(deleteOverlays);
        //定位改變時
        updateWithNewLocation(location)
        //將畫面移至定位點的位置
        aton = location.latitude.toString() + "," + location.longitude
        val centerURL = "javascript:centerAt(" +
                location.latitude + "," + location.longitude + ")"
        webView!!.loadUrl(centerURL)
        val deleteOverlays = "javascript:deleteOverlays()"
        webView!!.loadUrl(deleteOverlays)
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        when (status) {
            LocationProvider.OUT_OF_SERVICE -> Log.v(TAG, "Status Changed: Out of Service")
            LocationProvider.TEMPORARILY_UNAVAILABLE -> Log.v(
                TAG,
                "Status Changed: Temporarily Unavailable"
            )
            LocationProvider.AVAILABLE -> Log.v(TAG, "Status Changed: Available")
        }
    }

    override fun onProviderEnabled(provider: String) {
        Log.d(TAG, "onProviderEnabled")
    }

    override fun onProviderDisabled(provider: String) {
        updateWithNewLocation(null)
        Log.d(TAG, "onProviderDisabled")
    }

    // ===========================================================
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_ASK_PERMISSIONS -> {
                var i = 0
                while (i < permissions.size) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(
                            applicationContext,
                            permissions[i] + "權限申請成功!",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "權限被拒絕： " + permissions[i],
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    i++
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val it = Intent()
        when (item.itemId) {
            R.id.action_settings -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    //---------------------
    // -----------------------------
    @JavascriptInterface
    fun GetLat(): String? {
        return Lat
    }

    @JavascriptInterface
    fun GetLon(): String? {
        return Lon
    }

    @JavascriptInterface
    fun Getjcontent(): String? {
        return jcontent
    }

    //-----傳送導航資訊-------------------------------
    @JavascriptInterface
    fun Navon(): String {
        return Navon
    }

    @JavascriptInterface
    fun Getstart(): String? {
        return aton
    }

    @JavascriptInterface
    fun Getend(): String {
        return Navend
    } //---------------------

    companion object {
        private val locations = arrayOf(
            arrayOf("現在位置", "0,0"),
            arrayOf("中區職訓", "24.172127,120.610313"),
            arrayOf("東海大學路思義教堂", "24.179051,120.600610"),
            arrayOf("台中公園湖心亭", "24.144671,120.683981"),
            arrayOf("秋紅谷", "24.1674900,120.6398902"),
            arrayOf("台中火車站", "24.136829,120.685011"),
            arrayOf("國立科學博物館", "24.1579361,120.6659828")
        )
        private const val MAP_URL = "file:///android_asset/GoogleMap_a.html"

        //-----------------所需要申請的權限數組---------------
        private val permissionsArray = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        //申請權限後的返回碼
        private const val REQUEST_CODE_ASK_PERMISSIONS = 1
    }
}