package com.tanav.eztoll

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_payment_confirmed.*

class PaymentConfirmed : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_confirmed)

        goToPaymentHistory.setOnClickListener {
            val intent = Intent(applicationContext, PastPayements::class.java)
            startActivity(intent)
        }
    }
}