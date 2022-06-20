package com.example.oneseed

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.oneseed.database.MyDbManager
import com.google.firebase.storage.FirebaseStorage
import java.io.File


class MainActivity : AppCompatActivity() {
    private val myDbManager = MyDbManager(this)
    private val permissionStorage = 100

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        /** Функциональная часть кнопки "Создать"*/
        this.findViewById<Button>(R.id.create_button_main).setOnClickListener {

            val intent = Intent(this, CreateActivity::class.java)
            startActivity(intent)
        }

        /** Функциональная часть кнопки "Рассчитать"*/
        this.findViewById<Button>(R.id.calculate_button).setOnClickListener {
            try{
            if (isOnline(this)) {
                getStorage()
                myDbManager.openDB()
                var i = 0
                val dataList = myDbManager.readDBDataPhotoUriText()
                for (item in dataList) {
                    val name = i.toString()
                    val refStorageRoot = FirebaseStorage.getInstance().reference
                    val path = refStorageRoot.child(name)
                    val file = Uri.fromFile(File(item))
                    path.putFile(file)

                    i += 1
                }
              myDbManager.dropDB()
                myDbManager.createDB()

                myDbManager.closeDB()
            } else {
                Toast.makeText(this, "Ошибка. Нет интернета!", Toast.LENGTH_SHORT).show()
            }
        }catch (e: Exception){
            Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show()
            }
        }

    }

    //Позже исправить на более актуальный метод
    private fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }

    override fun onBackPressed() {
        try {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Вы точно хотите выйти?")
            builder.setPositiveButton("Да") { _, _ ->
                moveTaskToBack(true)
                Thread.sleep(500)
                android.os.Process.killProcess(android.os.Process.myPid())
            }
            builder.setNegativeButton("Нет") { _, _ -> }

            val alertDialog = builder.create()
            alertDialog.show()

            val button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            with(button) {
                setTextColor(Color.GREEN)
            }
            val button1 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            with(button1) {
                setTextColor(Color.RED)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show()
        }
    }


    private fun getStorage() {
        try {
            if ((ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        permissionStorage)
                }
            }
        } catch (e: Exception) {

        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionStorage) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Разрешение получено", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Нет доступа к местоположению", Toast.LENGTH_SHORT).show()
            }
        }
    }


}