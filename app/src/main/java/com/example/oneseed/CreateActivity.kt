package com.example.oneseed


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class CreateActivity : AppCompatActivity(), LocationListener {

    private lateinit var locationManager: LocationManager
    private lateinit var tvGpsLocation: TextView
    private lateinit var dateAndTimeTextView: TextView
    private lateinit var image: ImageView

    private val locationPermissionCode = 2

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)
        image = findViewById(R.id.loaded_photo)
        image.setImageResource(0)

        setTime()
        /** Функциональная часть кнопки "назад"*/
        val backButton = findViewById<Button>(R.id.return_to_main_button)
        backButton.setOnClickListener {
            finishAndRemoveTask()
            Thread.sleep(50)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        /** Функциональная часть кнопки "прикрепить местоположение"*/
        val findGpsBtn: Button = findViewById(R.id.pin_location_button)
        findGpsBtn.setOnClickListener {
            tvGpsLocation = findViewById(R.id.location_textView)
            tvGpsLocation.text = "Определение..."
            getLocation()
        }

        /** Функциональная часть кнопки "Выбрать фотографию"*/
        val openPhotoBtn: Button = findViewById(R.id.load_photo_button)
        openPhotoBtn.setOnClickListener {
            pickImageGallery()
        }


        /**Открытие карт с полученными координатами при нажатии на эти координаты*/
        val textView: TextView = findViewById(R.id.location_textView)
        textView.setOnClickListener {
            try {
                //Массив из координат, которые отображаются при определении
                val strs = textView.text.toString().split(",").toTypedArray()
                val latitude = strs[0]
                val longitude = strs[1]

                /** Отображение диалогового окна с предложением выбора в каком приложении будут открываться карты*/
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
                alertDialog.show()

                val yandexbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                with(yandexbutton) {
                    setTextColor(Color.BLACK)
                }
                val googlebutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                with(googlebutton) {
                    setTextColor(Color.BLACK)
                }
            } catch (e: Exception) {
            }
        }

    }


    /**
     * Функция которая реализует выбор однй фотографии из галереи
     * */
    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }


    /**
     * Переменная которая содержит в себе результат взаимодействия при открытии фотографии из
     * функции pickImageGallery
     * */
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                image.setImageURI(data?.data)
            }
        }


    /**
     * Функция для определения времени в данный момент
     * */
    @SuppressLint("SetTextI18n")
    private fun setTime() {
        dateAndTimeTextView = findViewById(R.id.dateAndTimeTextView)
        val currentDate = Date()
        val dateFormat: DateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val dateText: String = dateFormat.format(currentDate)
        val timeFormat: DateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val timeText: String = timeFormat.format(currentDate)
        dateAndTimeTextView.text = "$dateText\n$timeText"
    }


    /**Функция для определения геопозиции пользователя*/
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
        builder.setTitle("Предупреждение")
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

    /**
     * Запрос разрешения на доступ к геолокации
     * */
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


    /**
     * Функция которая определяет геопозицию в конкретный момент времени
     * */
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