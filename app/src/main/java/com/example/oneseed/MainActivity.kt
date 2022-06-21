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
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.oneseed.database.MyDbManager
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.File


data class User(val username: String? = null, val email: String? = null) {}


class MainActivity : AppCompatActivity() {
    private val myDbManager = MyDbManager(this)
    private val permissionStorage = 100
    private val username = "username"
    private lateinit var database: DatabaseReference
    var currentId = 0
    var actualMaxId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        database = FirebaseDatabase.getInstance().reference





        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        /** Функциональная часть кнопки "Создать"*/
        this.findViewById<Button>(R.id.create_button_main).setOnClickListener {

            val intent = Intent(this, CreateActivity::class.java)
            startActivity(intent)
        }

        //перенести код в функции
        this.findViewById<ImageButton>(R.id.imageButton).setOnClickListener {

            /*database = FirebaseDatabase.getInstance().reference
            val globalarray: MutableList<Array<String>> = ArrayList()

            Thread.sleep(1000)
            database.child("users").child("username").child("maxId").get()
                .addOnSuccessListener {
                    Log.i("firebase", "Got value ${it.value}")
                    actualMaxId = it.value.toString().toInt()
                    Log.i("firebase1", "Got value $actualMaxId")
                }.addOnFailureListener { Log.e("firebase", "Error getting data", it) }



            while (currentId <= actualMaxId) {
                val array = arrayOf("", "", "", "", "", "")
                var string: String
                database.child("users").child(username).child("0").get()
                    .addOnSuccessListener {

                        string = it.value.toString()
*//*                        array[0] = string.substringAfter("comment=").substringBefore("}")
                        array[1] = string.substringAfter("coordinates=").substringBefore(",")
                        array[2] = string.substringAfter("name=").substringBefore(",")
                        array[3] = string.substringAfter("photo=").substringBefore(",")
                        array[4] = string.substringAfter("result=").substringBefore(",")
                        array[5] = string.substringAfter("varieties=")*//*
                    }
                globalarray.add(array)
                currentId += 1
            }

            for (item in globalarray) {
                Log.i("array", item[0])

            }
*/
        }

        /** Функциональная часть кнопки "Рассчитать"*/
        this.findViewById<Button>(R.id.calculate_button).setOnClickListener {
            database = FirebaseDatabase.getInstance().reference

            try {
                if (isOnline(this)) {
                    getStorage()
                    myDbManager.openDB()
                    var i = 0
                    val dataArray = myDbManager.readDBDataPhotoUriText()
                    for (item in dataArray) {
                        val name = i.toString()
                        val refStorageRoot = FirebaseStorage.getInstance().reference
                        val path = refStorageRoot.child(username).child("$username$name")
                        val file = Uri.fromFile(File(item))
                        path.putFile(file)
                        i += 1
                    }

                    val dataList = myDbManager.readDBAllData()
                    for (item in dataList) {
                        database.child("users").child(username).child("$actualMaxId").child("name").setValue(item[0])
                        database.child("users").child(username).child("$actualMaxId").child("coordinates").setValue(item[1])
                        database.child("users").child(username).child("$actualMaxId").child("photo").setValue("$username$actualMaxId")
                        database.child("users").child(username).child("$actualMaxId").child("varieties").setValue(item[3])
                        database.child("users").child(username).child("$actualMaxId").child("comment").setValue(item[4])
                        database.child("users").child(username).child("$actualMaxId").child("result").setValue(item[5])
                        database.child("users").child(username).child("maxId").setValue(actualMaxId)
                        actualMaxId += 1
                    }
                    myDbManager.dropDB()
                    myDbManager.createDB()

                    myDbManager.closeDB()
                } else {
                    Toast.makeText(this, "Ошибка. Нет интернета!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show()
            }
        }

    }


    //будет использоваться в дальнейшем когда будет решён баг с недостаточным времем для получения результата
    private fun getValues(username: String, id: String): Array<String> {
        val array = arrayOf("", "", "", "", "", "")
        var string: String
        database.child("users").child(username).child(id).get().addOnSuccessListener {

            string = it.value.toString()
            array[0] = string.substringAfter("comment=").substringBefore(",")
            array[1] = string.substringAfter("coordinates=").substringBefore(",")
            array[2] = string.substringAfter("name=").substringBefore(",")
            array[3] = string.substringAfter("photo=").substringBefore(",")
            array[4] = string.substringAfter("result=").substringBefore(",")
            array[5] = string.substringAfter("varieties=")
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }
        return array
    }


    //будет использоваться в дальнейшем когда будет решён баг с недостаточным времем для получения результата
    private fun getMaxId(): String {
        var maxId: String = "0"
        database.child("users").child("username").child("maxId").get().addOnSuccessListener {
            maxId = it.value.toString()
            Log.e("firebase", maxId)
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }
        return maxId
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