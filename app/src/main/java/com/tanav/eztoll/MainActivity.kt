package com.tanav.eztoll

import android.content.ContentValues.TAG
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var email: EditText
    lateinit var pass: EditText
    lateinit var signupEmail: EditText
    lateinit var signupPassword: EditText
    lateinit var verifyPassword: EditText

    lateinit var userEmail: String
    lateinit var userPassword: String

    private lateinit var auth: FirebaseAuth;
    
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth

        email = findViewById<EditText>(R.id.email)
        pass = findViewById<EditText>(R.id.password)

        signupEmail = findViewById<EditText>(R.id.signupEmail)
        signupPassword = findViewById<EditText>(R.id.SignUpPassword)
        verifyPassword = findViewById<EditText>(R.id.VerifyPassword)

        signUpToggle.setOnClickListener {
            signUpToggle.background = resources.getDrawable(R.drawable.switch_highlighted, null)
            signUpToggle.setTextColor(resources.getColor(R.color.textColor, null))
            logInToggle.background = null
            SignUpLayout.visibility = View.VISIBLE
            LogInLayout.visibility = View.GONE
            logInToggle.setTextColor(resources.getColor(R.color.pinkColor, null))
            mainBtn.setText("Sign Up!")
        }
        logInToggle.setOnClickListener {
            logInToggle.background = resources.getDrawable(R.drawable.switch_highlighted, null)
            logInToggle.setTextColor(resources.getColor(R.color.textColor, null))
            signUpToggle.background = null
            LogInLayout.visibility = View.VISIBLE
            SignUpLayout.visibility = View.GONE
            signUpToggle.setTextColor(resources.getColor(R.color.pinkColor, null))
        }

        mainBtn.setOnClickListener {

            if(mainBtn.text.equals("Login")){
                Toast.makeText(applicationContext, "You Pressed The Login Button", Toast.LENGTH_SHORT).show()

                if(!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()){
                    email.error = "Please enter valid email"
                    email.requestFocus()
                }
                if(pass.text.toString().isEmpty()){
                    pass.error = "Please enter a password"
                    pass.requestFocus()
                }
            }


            if(mainBtn.text.equals("Sign Up!")){
                Toast.makeText(applicationContext, "You Pressed The Sign Up Button", Toast.LENGTH_SHORT).show()

                if(!Patterns.EMAIL_ADDRESS.matcher(signupEmail.text.toString()).matches()){
                    signupEmail.error = "Please enter valid email"
                    signupEmail.requestFocus()
                }else{
                    userEmail = signupEmail.text.toString()
                }

                if(signupPassword.text.toString().isEmpty()){
                    signupPassword.error = "Please enter a password"
                    signupPassword.requestFocus()
                }
                if(verifyPassword.text.toString().isEmpty()){
                    verifyPassword.error = "Please enter a password"
                    verifyPassword.requestFocus()
                }else{
                    userPassword = verifyPassword.text.toString()
                }

                if(!signupPassword.text.toString().equals(verifyPassword.text.toString())){
                    signupPassword.error = "Passwords don't match"
                    verifyPassword.error = "Passwords don't match"
                    signupPassword.requestFocus()
                    verifyPassword.requestFocus()
                }
                auth.createUserWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success")
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            }

        }





    }
}