package com.example.trackingtest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    private lateinit var mDriver: Button
    private lateinit var mCustomer: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mDriver = findViewById(R.id.driver)
        mCustomer = findViewById(R.id.customer)

        mDriver.setOnClickListener{
            var intent = Intent(this, DriverLoginActivity::class.java)
            startActivity(intent)
        }

        mCustomer.setOnClickListener{
            var intent = Intent(this, CustomerLoginActivity::class.java)
            startActivity(intent)
        }
    }
}