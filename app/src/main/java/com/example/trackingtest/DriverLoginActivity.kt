
package com.example.trackingtest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlin.math.log

class DriverLoginActivity : AppCompatActivity() {
    private val TAG = "ReposeDriverLoginActivity"

    private lateinit var mEmail: EditText
    private lateinit var mPassword: EditText

    private lateinit var mLogin: Button
    private lateinit var mRegister: Button

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseAuthStateListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_login_activity)

        mAuth = FirebaseAuth.getInstance()

        firebaseAuthStateListener = FirebaseAuth.AuthStateListener {
            var user = FirebaseAuth.getInstance().currentUser
            if(user!=null) {
                var intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
            }
        }
        mEmail = findViewById(R.id.email)
        mPassword = findViewById(R.id.password)

        mLogin = findViewById(R.id.login)
        mRegister = findViewById(R.id.registration)

        mRegister.setOnClickListener{
            var email = mEmail.text.toString()
            var password = mPassword.text.toString()
            try {
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) {
                    if (!it.isSuccessful) {
                        Log.d(TAG, "onCreate: ${it.result}")
                        Log.d(TAG, "onCreate: ${it.exception}")
                        Toast.makeText(applicationContext, "Sign up error", Toast.LENGTH_SHORT).show()
                    } else {
                        var userId = mAuth.currentUser!!.uid
                        var currentUserDb = FirebaseDatabase.getInstance().reference.child("Users").child("Drivers").child(userId)
                        currentUserDb.setValue(true)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        mLogin.setOnClickListener{
            var email = mEmail.text.toString()
            var password = mPassword.text.toString()
            try {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {
                    if (!it.isSuccessful) {
                        Toast.makeText(applicationContext, "Login error", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(firebaseAuthStateListener)
    }

    override fun onStop() {
        super.onStop()
        mAuth.addAuthStateListener(firebaseAuthStateListener)
    }

}