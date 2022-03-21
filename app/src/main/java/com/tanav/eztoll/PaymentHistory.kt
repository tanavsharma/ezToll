package com.tanav.eztoll

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_payment_history.*
import java.time.LocalDateTime
import java.time.temporal.ChronoField
import java.util.*
import java.util.UUID


class PaymentHistory : AppCompatActivity() {

    private lateinit var ccET: EditText
    private lateinit var nameOnCardET: EditText
    private lateinit var expiryMonthET: EditText
    private lateinit var expiryYearET: EditText
    private lateinit var cvvET: EditText
    private lateinit var validTV: TextView

    private lateinit var ccNumber: String
    private lateinit var chName: String
    private lateinit var ccExpiryMonth: String
    private lateinit var ccExpiryYear: String
    private lateinit var ccCVV: String

    private lateinit var auth: FirebaseAuth




    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_history)

        val db = Firebase.firestore
        auth = Firebase.auth

        ccET = findViewById(R.id.creditcard)
        nameOnCardET = findViewById(R.id.nameOnCard)
        expiryMonthET = findViewById(R.id.expiryMonth)
        expiryYearET = findViewById(R.id.expiryYear)
        cvvET = findViewById(R.id.cvv)


        validTV = findViewById(R.id.valid)


        payBill.setOnClickListener {

            val user = auth.currentUser!!.uid
            val current = LocalDateTime.now()

            val currentDay = current.get(ChronoField.DAY_OF_MONTH)
            val currentMonth = current.get(ChronoField.MONTH_OF_YEAR)
            val currentYear = current.get(ChronoField.YEAR)

            val todayDate = currentDay.toString() + " " + currentMonth.toString() + ", " + currentYear.toString()

            ccNumber = ccET.text.toString()
            chName = nameOnCardET.text.toString()
            ccExpiryMonth = expiryMonthET.text.toString()
            ccExpiryYear = expiryYearET.text.toString()
            ccCVV = cvvET.text.toString()

            if(isValid(ccNumber) && !ccNumber.isEmpty() && !chName.isEmpty() && !ccExpiryMonth.isEmpty() && !ccExpiryYear.isEmpty() && isExpired(ccExpiryMonth,ccExpiryYear) && ! ccCVV.isEmpty()){

                val rcpt = hashMapOf(
                    "Card Holder Name" to chName,
                    "Expiry Month" to ccExpiryMonth,
                    "Expiry Year" to ccExpiryYear,
                    "Date of Payment" to todayDate,
                    "Amount" to "$34.99"
                )

                db.collection(user).add(rcpt).addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                }.addOnFailureListener{ e ->
                    Log.w(TAG, "Error adding document", e)
                }


                val intent = Intent(applicationContext, PaymentConfirmed::class.java)
                startActivity(intent)
            }else{
                valid.text = getString(R.string.not_valid_card)
            }


        }
        viewPreviousBills.setOnClickListener {
            val intent = Intent(applicationContext, PastPayements::class.java)
            startActivity(intent)
        }

    }

    fun isValid(cardNumber: String): Boolean {

        var s1 = 0
        var s2 = 0
        val reverse = StringBuffer(cardNumber).reverse().toString()
        for (i in reverse.indices) {
            val digit = Character.digit(reverse[i], 10)
            when {
                i % 2 == 0 -> s1 += digit
                else -> {
                    s2 += 2 * digit
                    when {
                        digit >= 5 -> s2 -= 9
                    }
                }
            }
        }
        return (s1 + s2) % 10 == 0
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isExpired(expiryMonth: String, expiryYear: String): Boolean {

        var re = false
        val current = LocalDateTime.now()
        val currentMonth = current.get(ChronoField.MONTH_OF_YEAR)
        val currentYear = current.get(ChronoField.YEAR)

        if(currentMonth.toInt() > expiryMonth.toInt() || currentYear.toInt() > expiryYear.toInt()){
            println("Expired Card")
            re = false
        }else{
            println("valid card")
            re = true
        }
        println(re)
        return re
    }
}
