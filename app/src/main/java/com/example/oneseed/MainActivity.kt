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
import com.example.oneseed.databinding.ActivityMainBinding
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.File


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    val rcAdapter = RecordAdapter()
    private val myDbManager = MyDbManager(this)
    private val permissionStorage = 100
    private val username = "username"
    private lateinit var database: DatabaseReference
    private var currentId = 0
    var actualMaxId = 0
    private var allArraysSplit: ArrayList<ArrayList<String>> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        allArraysSplit = arrayListOf()
        currentId = 0
        actualMaxId = 0

        binding = ActivityMainBinding.inflate(layoutInflater)
        database = FirebaseDatabase.getInstance().reference

        val db = FirebaseDatabase.getInstance().reference.database
        val myRef = db.getReference("users").child("username").child("maxId")
        getMaxId(myRef)
        val myRefer = db.getReference("users").child("username")
        getValues(myRefer)
        //Toast.makeText(this, "$actualMaxId", Toast.LENGTH_SHORT).show()


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
   /*         allArraysSplit = arrayListOf()
            currentId = 0
            actualMaxId = 0
            val myRefer = db.getReference("users").child("username")
            getValues(myRefer)*/

            initRecyclerView()

        }

        /** Функциональная часть кнопки "Рассчитать"*/
        this.findViewById<Button>(R.id.calculate_button).setOnClickListener {
            database = FirebaseDatabase.getInstance().reference
            try {
                if (isOnline(this)) {
                    getStorage()
                    myDbManager.openDB()
                    val dataArray = myDbManager.readDBDataPhotoUriText()
                    for (item in dataArray) {
                        actualMaxId += 1
                        val refStorageRoot = FirebaseStorage.getInstance().reference
                        val path =
                            refStorageRoot.child(username).child("$username$actualMaxId")
                        val file = Uri.fromFile(File(item))
                        path.putFile(file)
                    }
                    //Toast.makeText(this, "$actualMaxId", Toast.LENGTH_SHORT).show()
                    val dataList = myDbManager.readDBAllData()
                    for (item in dataList) {
                        database.child("users").child(username).child("$actualMaxId")
                            .child("name").setValue(item[0])
                        database.child("users").child(username).child("$actualMaxId")
                            .child("coordinates").setValue(item[1])
                        database.child("users").child(username).child("$actualMaxId")
                            .child("photo").setValue("$username$actualMaxId")
                        database.child("users").child(username).child("$actualMaxId")
                            .child("varieties").setValue(item[3])
                        database.child("users").child(username).child("$actualMaxId")
                            .child("comment").setValue(item[4])
                        database.child("users").child(username).child("$actualMaxId")
                            .child("result").setValue(item[5])
                        database.child("users").child(username).child("$actualMaxId")
                            .child("datetime").setValue(item[6])
                        database.child("users").child(username).child("maxId")
                            .setValue(actualMaxId)
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

    private fun getMaxId(dRef: DatabaseReference) {
        dRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                actualMaxId = snapshot.value.toString().toInt()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }


    //будет использоваться в дальнейшем когда будет решён баг с недостаточным времем для получения результата
    private fun getValues(dRef: DatabaseReference) {
        var i = 0
        dRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (h in snapshot.children) {
                        if (i < actualMaxId) {
                            allArraysSplit.add(splitResults(h.value.toString()))
                            Log.d("Data", "$allArraysSplit")
                            i += 1
                        }
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {}
        })

    }

    private fun splitResults(value: String): ArrayList<String> {
        val array = arrayListOf("", "", "", "", "", "", "", "")
        array[0] = currentId.toString()
        array[1] = value.substringAfter("result=").substringBefore(",")
        array[2] = value.substringAfter("varieties=").substringBefore(",")
        array[3] = value.substringAfter("coordinates=").substringBefore(",")
        array[4] = value.substringAfter("name=").substringBefore(",")
        array[5] = value.substringAfter("photo=").substringBefore(",")
        array[6] = value.substringAfter("datetime=").substringBefore(",")
        array[7] = value.substringAfter("comment=")
        currentId += 1
        return array
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

    private fun initRecyclerView(){
        binding.apply {
            recyclerView.layoutManager = LinearLayoutManager(root.context)
            recyclerView.adapter = rcAdapter
            for (item in allArraysSplit){
                val statusImg = this@MainActivity.intent.getIntExtra("status_image", 0)
                val recordName = allArraysSplit[0][4]
                val dateNTime = allArraysSplit[0][6]
                val location = allArraysSplit[0][3]
                val result = allArraysSplit[0][1]
                val record = Record(statusImg, recordName, dateNTime, result, location)
                rcAdapter.addRecord(record)
            }
            }
        }


}