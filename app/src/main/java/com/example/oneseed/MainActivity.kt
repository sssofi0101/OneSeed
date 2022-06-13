package com.example.oneseed

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.findViewById<Button>(R.id.create_button_main).setOnClickListener {

            val intent = Intent(this, CreateActivity::class.java)
            startActivity(intent)
        }
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
}