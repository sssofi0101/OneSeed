package com.example.oneseed

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class CreateActivity : AppCompatActivity() {
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
    }
}