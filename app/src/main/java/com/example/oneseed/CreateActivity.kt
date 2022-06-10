package com.example.oneseed

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class CreateActivity : AppCompatActivity() , LocationListener {
    private lateinit var locationManager: LocationManager
    private lateinit var tvGpsLocation: TextView
    private val locationPermissionCode = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)
        val button: Button = findViewById(R.id.button7)
        button.setOnClickListener {
            tvGpsLocation = findViewById(R.id.textView7)
            tvGpsLocation.text = "Определение..."
            getLocation()
        }
    }

    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode)
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
    }
    @SuppressLint("SetTextI18n")
    override fun onLocationChanged(location: Location) {
        tvGpsLocation = findViewById(R.id.textView7)
        var lant = location.latitude.toString()
        lant = lant.take(8)
        var long = location.longitude.toString()
        long = long.take(8)
        tvGpsLocation.text = "Ширина: $lant\nДолгота: $long"
        locationManager.removeUpdates(this)
    }

}