package com.tanav.eztoll

import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var pass: EditText
    private lateinit var signupEmail: EditText
    private lateinit var signupPassword: EditText
    private lateinit var verifyPassword: EditText

    private lateinit var userEmail: String
    private lateinit var userPassword: String

    private lateinit var loginEmail: String
    private lateinit var loginPassword: String

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var receiver: TrackToggleAlarmReceiver

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imgArray = intArrayOf(R.drawable.eztoll, R.drawable.eztoll2, R.drawable.eztoll3)
        flipper = findViewById(R.id.ezTollLogo)

        for (j in imgArray) {
            showImages(j)
        }

        auth = Firebase.auth
        database = Firebase.database.getReference("Users")

        email = findViewById(R.id.email)
        pass = findViewById(R.id.password)

        signupEmail = findViewById(R.id.signupEmail)
        signupPassword = findViewById(R.id.SignUpPassword)
        verifyPassword = findViewById(R.id.VerifyPassword)

        forgotPasscode.setOnClickListener {
            val intent = Intent(this, ForgotPasscode::class.java)
            startActivity(intent)
        }

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
            mainBtn.setText("Login")
        }

        mainBtn.setOnClickListener {

            if(mainBtn.text.equals("Login")){
                Toast.makeText(applicationContext, "You Pressed The Login Button", Toast.LENGTH_SHORT).show()

                if(!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()){
                    email.error = "Please enter valid email"
                    email.requestFocus()
                }else{
                    loginEmail = email.text.toString()
                }

                if(pass.text.toString().isEmpty()){
                    pass.error = "Please enter a password"
                    pass.requestFocus()
                }else{
                    loginPassword = pass.text.toString()
                }

                auth.signInWithEmailAndPassword(loginEmail, loginPassword)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(applicationContext, "Signing You In....", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this, UserInterface::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(applicationContext, "Wrong, Try again!", Toast.LENGTH_SHORT).show()
                        }
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

                if(signupPassword.text.toString() != verifyPassword.text.toString()){
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

                            var userUniqueID = auth.currentUser!!.uid
                            database.child(userUniqueID)

                            val intent = Intent(this, UserInformation::class.java)
                            intent.putExtra("uniqueID",userUniqueID)
                            startActivity(intent)

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            }

        }

        receiver = TrackToggleAlarmReceiver()
        val reminderFilter : IntentFilter  = IntentFilter(AppConst.ACTION_REMINDER)
        registerReceiver(receiver, reminderFilter)
    }
    private var flipper: ViewFlipper? = null
    fun showImages(img: Int) {
        val imageView = ImageView(this)
        imageView.setBackgroundResource(img)
        flipper?.addView(imageView)
        flipper?.setFlipInterval(5000)
        flipper?.setAutoStart(true)
        flipper?.setInAnimation(this, android.R.anim.slide_in_left)
        flipper?.setInAnimation(this, android.R.anim.slide_out_right)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}