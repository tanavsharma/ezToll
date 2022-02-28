package com.tanav.eztoll

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_forgot_passcode.*

class ForgotPasscode : AppCompatActivity() {

    private lateinit var emailAddressET: EditText
    private lateinit var email: String
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_passcode)

        emailAddressET = findViewById(R.id.emailAddress)

        auth = Firebase.auth

        forgotPasscodeBtn.setOnClickListener {
            email = emailAddressET.text.toString()

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //Log.d(TAG, "Email sent.")
                    }else{

                    }
                }


        }


    }
}