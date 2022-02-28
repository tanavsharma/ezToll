package com.tanav.eztoll

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_forgot_passcode.*

class ForgotPasscode : AppCompatActivity() {

    private lateinit var emailAddressET: EditText
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_passcode)

        emailAddressET = findViewById(R.id.emailAddress)
        email = emailAddressET.text.toString()

        forgotPasscodeBtn.setOnClickListener {
            Firebase.auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Email sent.")
                    }
                }
        }


    }
}