package com.tanav.eztoll

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.tanav.eztoll.database.ChargesStatusViewModel
import com.tanav.eztoll.utilities.Utility
import kotlinx.android.synthetic.main.activity_payment_confirmed.*

class PaymentConfirmed : AppCompatActivity() {
    lateinit var chargesStatusViewModel: ChargesStatusViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_confirmed)

        chargesStatusViewModel = ViewModelProvider(this).get(ChargesStatusViewModel::class.java)
        chargesStatusViewModel.updateChargesStatusPaidDate(this, Utility.todayInInt())

        goToPaymentHistory.setOnClickListener {
            val intent = Intent(applicationContext, UserInterface::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        //disable the back button
    }
}