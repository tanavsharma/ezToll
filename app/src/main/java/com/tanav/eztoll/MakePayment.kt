package com.tanav.eztoll

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tanav.eztoll.database.ChargesStatusViewModel
import java.time.LocalDateTime
import java.time.temporal.ChronoField
import java.util.*
import java.util.UUID


class MakePayment : AppCompatActivity() {

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
    private lateinit var valid: TextView
    private lateinit var payBill: Button

    private lateinit var auth: FirebaseAuth

    private lateinit var chargesStatusViewModel: ChargesStatusViewModel
    private lateinit var costToCxTV: TextView


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_payment)

        auth = Firebase.auth

        ccET = findViewById(R.id.creditcard)
        nameOnCardET = findViewById(R.id.nameOnCard)
        expiryMonthET = findViewById(R.id.expiryMonth)
        expiryYearET = findViewById(R.id.expiryYear)
        cvvET = findViewById(R.id.cvv)
        valid = findViewById(R.id.valid)
        payBill = findViewById(R.id.payBill)

        validTV = findViewById(R.id.valid)
        costToCxTV = findViewById(R.id.costToCx)

        //read room db outstanding amounts
        chargesStatusViewModel = ViewModelProvider(this).get(ChargesStatusViewModel::class.java)
        chargesStatusViewModel.getUnPaidChargesStatus(applicationContext)!!.observe(this, {
            if (it.isNotEmpty()) {
                var outstandingAmount = 0.00
                for (cs in it) {
                    outstandingAmount += cs.totalAmount
                }
                costToCxTV.text = getString(R.string.total_due, outstandingAmount.toFloat())
            }
            setPayBillListener()
        })
    }

    private fun setPayBillListener() {
        payBill.setOnClickListener {
            val db = Firebase.firestore
            val user = auth.currentUser!!.uid
            val current = LocalDateTime.now()

            val currentDay = current.get(ChronoField.DAY_OF_MONTH)
            val currentMonth = current.get(ChronoField.MONTH_OF_YEAR)
            val currentYear = current.get(ChronoField.YEAR)

            var monthDict = mapOf(
                "1" to "January",
                "2" to "Feburary",
                "3" to "March",
                "4" to "April",
                "5" to "May",
                "6" to "June",
                "7" to "July",
                "8" to "August",
                "9" to "September",
                "10" to "October",
                "11" to "November",
                "12" to "December"
            )

            val todayDate =
                currentDay.toString() + " " + monthDict[currentMonth.toString()] + ", " + currentYear.toString()

            ccNumber = ccET.text.toString()
            chName = nameOnCardET.text.toString()
            ccExpiryMonth = expiryMonthET.text.toString()
            ccExpiryYear = expiryYearET.text.toString()
            ccCVV = cvvET.text.toString()

            if (isValid(ccNumber) && !ccNumber.isEmpty() && !chName.isEmpty() && !ccExpiryMonth.isEmpty() && !ccExpiryYear.isEmpty() && isExpired(
                    ccExpiryMonth,
                    ccExpiryYear
                ) && !ccCVV.isEmpty()
            ) {

                val rcpt = hashMapOf(
                    "chName" to chName,
                    "expiryMonth" to ccExpiryMonth,
                    "expiryYear" to ccExpiryYear,
                    "dateOfPayment" to todayDate,
                    "amount" to costToCxTV.text
                )

                db.collection(user).add(rcpt).addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                }.addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }


                val intent = Intent(applicationContext, PaymentConfirmed::class.java)
                startActivity(intent)
            } else {
                valid.text = getString(R.string.not_valid_card)
            }
        }
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

    private fun isValid(cardNumber: String): Boolean {

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