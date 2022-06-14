package com.example.oneseed

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class CreateActivity : AppCompatActivity(), LocationListener {
    private lateinit var locationManager: LocationManager
    private lateinit var tvGpsLocation: TextView
    private val locationPermissionCode = 2
    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        val backButton = findViewById<Button>(R.id.return_to_main_button)
        backButton.setOnClickListener {
            finishAndRemoveTask()
            Thread.sleep(50)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)


            }
        val button: Button = findViewById(R.id.pin_location_button)
        button.setOnClickListener {
            tvGpsLocation = findViewById(R.id.location_textView)
            tvGpsLocation.text = "Определение..."
            getLocation()
        }

        //Открытие карт с полученными координатами при нажатии на эти координаты
        val textView: TextView = findViewById(R.id.location_textView)
        textView.setOnClickListener {
            try {

/*

                val gmmIntentUri =
                    Uri.parse("geo:0,0?q=-33.8666,151.1957(Google+Sydney)")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
*/


                //Массив из координат, которые отображаются при определении
                val strs = textView.text.toString().split(",").toTypedArray()
                val latitude = strs[0]
                val longitude = strs[1]

                val builder = AlertDialog.Builder(this)
                builder.setTitle("Выберите приложение:")
                builder.setPositiveButton("Yandex-карты") { _, _ ->
                    val intent = Intent(Intent.ACTION_VIEW,
                        Uri.parse("yandexmaps://maps.yandex.ru/?pt=$longitude,$latitude&z=12&l=map"))
                    startActivity(intent)
                }
                builder.setNegativeButton("Google-карты") { _, _ ->
                    val gmmIntentUri =
                        Uri.parse("geo:0,0?q=$latitude,$longitude(Ваше местоположением)")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    startActivity(mapIntent)
                }

                val alertDialog = builder.create()
                //alertDialog.window?.decorView?.setBackgroundResource(R.drawable.ic_launcher_background)
                alertDialog.show()

                val yandexbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                with(yandexbutton) {
                    setTextColor(Color.BLACK)
                }
                val googlebutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                with(googlebutton) {
                    setTextColor(Color.BLACK)
                }
            }
            catch (e: Exception){ }
        }

    }

    private fun getLocation() {
        try {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if ((ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    locationPermissionCode)
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 1f, this)
        } catch (e: Exception) {
            gpsPermissionWrongAlert()
        }
    }



    /**
     * Отображение диалогового окна об ошибки при отсутствии доступа к местоположению.
     */
    private fun gpsPermissionWrongAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Ошибка в определении местоположения")
        builder.setMessage("Чтобы мы смогли точно определять Ваше местоположение, необходимо предоставить разрешение " +
                "на использование местоположения. Зайдите в настройки приложения " +
                "и разрешите доступ к местоположению")
        builder.setPositiveButton("Понятно") { _, _ -> }

        val alertDialog = builder.create()
        alertDialog.show()

        val button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
        with(button) {
            setTextColor(Color.GREEN)

        }
    }









    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Разрешение получено", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Нет доступа к местоположению", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onLocationChanged(location: Location) {
        tvGpsLocation = findViewById(R.id.location_textView)
        var lant = location.latitude.toString()
        lant = lant.take(10)
        var long = location.longitude.toString()
        long = long.take(10)
        tvGpsLocation.text = "$lant,$long"
        locationManager.removeUpdates(this)
    }

}