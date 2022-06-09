package com.example.oneseed

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.findViewById<Button>(R.id.create_button_main).setOnClickListener{
            val intent=Intent(this,CreateActivity::class.java)
            startActivity(intent)
        }
    }
}