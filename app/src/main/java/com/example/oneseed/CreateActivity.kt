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
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import br.com.onimur.handlepathoz.HandlePathOz
import br.com.onimur.handlepathoz.HandlePathOzListener.SingleUri
import br.com.onimur.handlepathoz.model.PathOz
import com.example.oneseed.database.MyDbManager
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class CreateActivity : AppCompatActivity(), LocationListener, SingleUri {

    private lateinit var locationManager: LocationManager
    private lateinit var tvGpsLocation: TextView
    private lateinit var gpsUserOwn: EditText
    private lateinit var dateAndTimeTextView: TextView
    private lateinit var image: ImageView
    private lateinit var handlePathOz: HandlePathOz
    private val myDbManager = MyDbManager(this)


    private var photoAddress: Uri? = null
    private val locationPermissionCode = 2

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        var statusOfLocation = findViewById<ImageView>(R.id.status_of_location_imageView)

        handlePathOz = HandlePathOz(this, this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)
        image = findViewById(R.id.loaded_photo)
        tvGpsLocation = findViewById(R.id.location_textView)
        gpsUserOwn = findViewById(R.id.userOwnGps)
        image.setImageResource(0)
        setTime()

        /** Функциональная часть кнопки "назад"*/
        val backButton = findViewById<Button>(R.id.return_to_main_button)
        backButton.setOnClickListener {
            backMainApp()
        }

        /** Функциональная часть кнопки "Добавить"*/
        val addBtn = findViewById<Button>(R.id.add_button)
        addBtn.setOnClickListener {
            val loadPhotoStatus = findViewById<ImageView>(R.id.load_photo_status)
            statusOfLocation = findViewById(R.id.status_of_location_imageView)
            if (loadPhotoStatus.visibility == View.INVISIBLE
                || statusOfLocation.visibility == View.INVISIBLE
            ) {
                Toast.makeText(this, "Ошибка. Необходимо заполнить " +
                        "все обязательные поля", Toast.LENGTH_SHORT).show()
            } else {
                try {
                    myDbManager.openDB()

                    val commentEditText = findViewById<EditText>(R.id.comment_editText)
                    val varietiesSpinner = findViewById<Spinner>(R.id.varietiesSpinner)
                    val nameEditText = findViewById<EditText>(R.id.name_editText)
                    if (gpsUserOwn.visibility == View.VISIBLE) {
                        myDbManager.insertToDB(nameEditText.text.toString(),
                            gpsUserOwn.text.toString(),
                            photoAddress.toString(),
                            varietiesSpinner.selectedItem.toString(),
                            commentEditText.text.toString(),
                            0,
                            0f)
                    } else {
                        myDbManager.insertToDB(nameEditText.text.toString(),
                            tvGpsLocation.text.toString(),
                            photoAddress.toString(),
                            varietiesSpinner.selectedItem.toString(),
                            commentEditText.text.toString(),
                            0,
                            0f)
                    }


                    Toast.makeText(this, "Успешно добавлено!", Toast.LENGTH_SHORT).show()



                    myDbManager.closeDB()
                    backMainApp()

                } catch (e: Exception) {
                }
            }


        }
        gpsUserOwn.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (gpsUserOwn.text.toString() != "") {
                    statusOfLocation.visibility = View.VISIBLE
                } else {
                    statusOfLocation.visibility = View.INVISIBLE
                }
            }
        })


        /** Функциональная часть кнопки "прикрепить местоположение"*/
        val findGpsBtn: Button = findViewById(R.id.pin_location_button)
        findGpsBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setPositiveButton("Автоматически") { _, _ ->
                gpsUserOwn.visibility = View.GONE
                tvGpsLocation.visibility = View.VISIBLE
                getLocation()

            }
            builder.setNeutralButton("Вручную") { _, _ ->
                gpsUserOwn.visibility = View.VISIBLE
                tvGpsLocation.visibility = View.GONE
                statusOfLocation = findViewById(R.id.status_of_location_imageView)
                statusOfLocation.visibility = View.INVISIBLE
            }
            val alertDialog = builder.create()
            alertDialog.show()

            val autoBtn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            with(autoBtn) {
                setTextColor(Color.BLACK)
            }
            val userBtn = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL)
            with(userBtn) {
                setTextColor(Color.BLACK)
            }


        }

        val spinner: Spinner = findViewById(R.id.varietiesSpinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.Varieties,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
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
                builder.setNeutralButton("Google-карты") { _, _ ->
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
                val googlebutton = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL)
                with(googlebutton) {
                    setTextColor(Color.BLACK)
                }
            } catch (e: Exception) {
            }
        }


    }

    private fun backMainApp() {
        finishAndRemoveTask()
        Thread.sleep(50)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    /**
     * Функция которая реализует выбор одной фотографии из галереи
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
                if (data != null) {
                    if (data.data != null) {
                        photoAddress = data.data!!
                        val loadPhotoStatus = findViewById<ImageView>(R.id.load_photo_status)
                        loadPhotoStatus.visibility = View.VISIBLE

                    }
                }
            }
            photoAddress?.let { handlePathOz.getRealPath(it) }
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
        tvGpsLocation = findViewById(R.id.location_textView)
        try {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if ((ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    locationPermissionCode)
            }
            tvGpsLocation.text = "Определение..."
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 1f, this)
        } catch (e: Exception) {
            gpsPermissionWrongAlert()
            tvGpsLocation.text = "Ошибка определния местоположения"
            val statusOfLocation = findViewById<ImageView>(R.id.status_of_location_imageView)
            statusOfLocation.visibility = View.INVISIBLE
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
        val statusOfLocation = findViewById<ImageView>(R.id.status_of_location_imageView)
        tvGpsLocation = findViewById(R.id.location_textView)
        var lant = location.latitude.toString()
        lant = lant.take(10)
        var long = location.longitude.toString()
        long = long.take(10)
        statusOfLocation.visibility = View.VISIBLE
        tvGpsLocation.text = "$lant,$long"
        locationManager.removeUpdates(this)
    }


    /**Необходимо чтобы перевести Context Uri в прямой путь к файлу*/
    override fun onRequestHandlePathOz(pathOz: PathOz, tr: Throwable?) {
        photoAddress = Uri.parse(pathOz.path)
        tr?.let {
            Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

}
