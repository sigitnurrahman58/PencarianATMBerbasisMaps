package com.cicisigit.projekpmob2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import org.osmdroid.config.Configuration
import android.icu.text.SimpleDateFormat
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.database.database
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.Date
import java.util.Locale

data class Atm(
    val name: String,
    val address: String,
    val lat: Double,
    val lon: Double
)
class MainActivity : AppCompatActivity(){
    private lateinit var map: MapView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<CoordinatorLayout>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userLocation: Location? = null

    // Firebase Realtime Database - URL project kamu
    private val database = Firebase.database("https://project-pmob-3d780-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val searchRef = database.getReference("search_history")

    // 50 ATM real di Yogyakarta
    private val atms = listOf(
        Atm("ATM BNI Pasarhewahan", "Jl. K.H. Agung Pasarhewahan", -7.7975, 110.3710),
        Atm("ATM BCA Imogiri Timur", "Jl. Imogiri Timur", -7.8100, 110.3820),
        Atm("ATM BRI Mangga Mulyo", "Jl. Mangga Mulyo", -7.8050, 110.3750),
        Atm("ATM Mandiri Kotagede", "Jl. Mondorakan Kotagede", -7.8167, 110.3958),
        Atm("ATM BCA Malioboro", "Jl. Malioboro No.60", -7.7930, 110.3660),
        Atm("ATM BNI UGM", "Bulaksumur UGM", -7.7680, 110.3780),
        Atm("ATM BRI Gejayan", "Jl. Gejayan", -7.7800, 110.3850),
        Atm("ATM BCA Hartono Mall", "Ring Road Utara Condongcatur", -7.7480, 110.3640),
        Atm("ATM Mandiri Adisucipto", "Bandara Adisucipto", -7.7870, 110.4310),
        Atm("ATM BCA Pringgondani Babarsari", "Jl. Babarsari", -7.7765, 110.4118),
        Atm("ATM BNI Kotagede", "Jl. Tegalgendu Kotagede", -7.8167, 110.3958),
        Atm("ATM BCA Akademi Manajemen", "Jl. Jend. Sudirman 49-51", -7.7850, 110.3680),
        Atm("ATM BRI Taman Siswa", "Jl. Taman Siswa", -7.8138, 110.3763),
        Atm("ATM Mandiri Malioboro Mall", "Jl. Malioboro No.52-58", -7.7982, 110.3658),
        Atm("ATM BCA Jogja Expo Center", "Jl. Raya Janti Banguntapan", -7.7980, 110.4050),
        Atm("ATM BNI Adisucipto Airport", "Bandara Adisucipto", -7.7870, 110.4310),
        Atm("ATM BRI UGM", "Jl. Kaliurang Km.4", -7.7685, 110.3775),
        Atm("ATM BCA Godean", "Jl. Godean Km.4", -7.7830, 110.3350),
        Atm("ATM Mandiri Gejayan", "Jl. Gejayan No.20", -7.7700, 110.3880),
        Atm("ATM BNI Tugu Jogja", "Depan Stasiun Tugu", -7.7890, 110.3630),
        Atm("ATM BCA Galeria Mall", "Jl. Jembatan Merah", -7.7825, 110.3705),
        Atm("ATM BRI Kraton", "Jl. Rotowijayan", -7.8080, 110.3620),
        Atm("ATM Mandiri Ambarrukmo Plaza", "Plaza Ambarrukmo", -7.7820, 110.4000),
        Atm("ATM BCA Sahid Jaya Hotel", "Jl. Babarsari", -7.7760, 110.4120),
        Atm("ATM BNI Sleman City Hall", "Jl. Magelang Km.10", -7.7050, 110.3560),
        Atm("ATM BRI Pakem", "Jl. Kaliurang Km.17", -7.6500, 110.4200),
        Atm("ATM BCA Carrefour Yogya", "Jl. Solo Km 8", -7.7834, 110.4123),
        Atm("ATM Mandiri Lippo Plaza", "Jl. Laksda Adisucipto", -7.7810, 110.3970),
        Atm("ATM BRI Condongcatur", "Terminal Condongcatur", -7.7550, 110.4000),
        Atm("ATM BCA Ngasem", "Jl. Ngasem Kraton", -7.8051, 110.3605),
        Atm("ATM BNI Bantul", "Jl. Wates Km 2 Kasihan", -7.8008, 110.3471),
        Atm("ATM Mandiri JEC", "Jl. Raya Janti Wonocatur", -7.7988, 110.4061),
        Atm("ATM BCA Smartfren Timoho", "Jl. Kenari No.62 Umbulharjo", -7.8004, 110.3932),
        Atm("ATM BRI Indomaret Taman Siswa", "Jl. Taman Siswa 150", -7.8138, 110.3763),
        Atm("ATM Mandiri POP Mart Malioboro", "Jl. Malioboro 91", -7.7935, 110.3656),
        Atm("ATM BCA Indomaret Ngasem", "Jl. Ngasem No.15 Kraton", -7.8051, 110.3605),
        Atm("ATM BNI MSC Timoho", "Jl. Kenari Umbulharjo", -7.8004, 110.3932),
        Atm("ATM BRI Mini Soccer JEC", "Jl. Raya Janti Banguntapan", -7.7988, 110.4061),
        Atm("ATM BCA UNY Colombo", "Jl. Colombo No.1 Karangmalang", -7.7708, 110.3783),
        Atm("ATM Mandiri Sahid Yogya", "Jl. Babarsari", -7.7765, 110.4118),
        Atm("ATM BNI Hartono Mall", "Ring Road Utara Sleman", -7.7480, 110.3640),
        Atm("ATM BCA Kotagede", "Jl. Tegalgendu Kotagede", -7.8167, 110.3958),
        Atm("ATM BRI Prambanan", "Jl. Raya Solo-Yogya Km.16", -7.7550, 110.4940),
        Atm("ATM Mandiri Godean", "Jl. Godean Km.5", -7.7820, 110.3300),
        Atm("ATM BCA Pakem", "Jl. Kaliurang Km.17", -7.6500, 110.4200),
        Atm("ATM BNI Gejayan", "Jl. Gejayan", -7.7800, 110.3850),
        Atm("ATM BRI Malioboro", "Jl. Malioboro", -7.7930, 110.3660),
        Atm("ATM Mandiri Tugu", "Depan Stasiun Tugu", -7.7890, 110.3630),
        Atm("ATM BCA Kraton", "Jl. Rotowijayan Kraton", -7.8080, 110.3620),
        Atm("ATM BNI Ambarrukmo", "Plaza Ambarrukmo", -7.7820, 110.4000)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        searchRef.child("test").setValue("halo firebase")
        // Cara lama yang pasti tidak merah dan jalan 100%
        Configuration.getInstance().userAgentValue = packageName

        setContentView(R.layout.activity_main)

        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Bottom Sheet
        val bottomSheetView = findViewById<CoordinatorLayout>(R.id.bottom_sheet_container)
        if (bottomSheetView != null) {
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        // My Location overlay
        val myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), map)
        myLocationOverlay.enableMyLocation()
        myLocationOverlay.enableFollowLocation()
        map.overlays.add(myLocationOverlay)

        getUserLocationAndSetupMap()

        // Search filter + simpan ke Firebase
        val etSearch = findViewById<EditText>(R.id.et_search)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (query.length >= 7) {
                    val timestamp = SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        Locale.getDefault()
                    ).format(Date())

                    val searchData = mapOf(
                        "query" to query,
                        "timestamp" to timestamp
                    )

                    searchRef.push().setValue(searchData)
                }
                filterAndAddMarkers(query.lowercase())
            }
        })
    }

    private fun getUserLocationAndSetupMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    userLocation = location
                    val userPoint = GeoPoint(location.latitude, location.longitude)
                    map.controller.setZoom(15.0)
                    map.controller.setCenter(userPoint)
                } else {
                    map.controller.setZoom(15.0)
                    map.controller.setCenter(GeoPoint(-7.797068, 110.370529))
                }
                filterAndAddMarkers("")
            }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            map.controller.setZoom(15.0)
            map.controller.setCenter(GeoPoint(-7.797068, 110.370529))
            filterAndAddMarkers("")
        }
    }

    private fun filterAndAddMarkers(query: String) {
        map.overlays.clear()

        val myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), map)
        myLocationOverlay.enableMyLocation()
        map.overlays.add(myLocationOverlay)

        val filteredAtms = if (query.isEmpty()) {
            atms
        } else {
            atms.filter {
                it.name.lowercase().contains(query) || it.address.lowercase().contains(query)
            }
        }

        filteredAtms.forEach { atm ->
            val marker = Marker(map)
            marker.position = GeoPoint(atm.lat, atm.lon)
            marker.title = atm.name
            map.overlays.add(marker)

            marker.setOnMarkerClickListener { _, _ ->
                showAtmDetail(atm)
                true
            }
        }
        map.invalidate()
    }

    private fun calculateDistance(userLat: Double, userLon: Double, atmLat: Double, atmLon: Double): Double {
        val results = FloatArray(1)
        Location.distanceBetween(userLat, userLon, atmLat, atmLon, results)
        return (results[0] / 1000).toDouble()
    }

    private fun showAtmDetail(atm: Atm) {
        // Foto ATM dari file atm.jpg yang sudah kamu masukkan
        findViewById<ImageView>(R.id.iv_atm_photo).setImageResource(R.drawable.atm)

        findViewById<TextView>(R.id.tv_atm_name).text = atm.name
        findViewById<TextView>(R.id.tv_atm_address).text = atm.address

        val distanceText = if (userLocation != null) {
            val distance = calculateDistance(
                userLocation!!.latitude,
                userLocation!!.longitude,
                atm.lat,
                atm.lon
            )
            String.format("%.1f Km dari lokasi Anda", distance)
        } else {
            "Lokasi Anda belum terdeteksi"
        }
        findViewById<TextView>(R.id.tv_distance).text = distanceText

        findViewById<Button>(R.id.btn_direction).setOnClickListener {
            val label = Uri.encode("${atm.name} - ${atm.address}")
            val gmmIntentUri = Uri.parse("geo:0,0?q=${atm.lat},${atm.lon}($label)")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            if (mapIntent.resolveActivity(packageManager) != null) {
                startActivity(mapIntent)
            } else {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=${atm.lat},${atm.lon}")))
            }
        }

        findViewById<Button>(R.id.btn_call).setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:1500989")
            startActivity(intent)
        }

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getUserLocationAndSetupMap()
        }
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}
