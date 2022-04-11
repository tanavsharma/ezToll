package com.tanav.eztoll

import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.tanav.eztoll.database.ChargesStatusViewModel
import com.tanav.eztoll.utilities.Utility
import kotlinx.android.synthetic.main.activity_payment_confirmed.*
import java.io.IOException

class PaymentConfirmed : AppCompatActivity() {
    lateinit var chargesStatusViewModel: ChargesStatusViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_confirmed)

        chargesStatusViewModel = ViewModelProvider(this).get(ChargesStatusViewModel::class.java)
        chargesStatusViewModel.updateChargesStatusPaidDate(this, Utility.todayInInt())

        playAudio()

        goToPaymentHistory.setOnClickListener {
            val intent = Intent(applicationContext, UserInterface::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }

    private fun playAudio(){
        val audioURL = "https://www.mboxdrive.com/payment.mp3"
        var mediaPlayer = MediaPlayer()
        mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        try {
            mediaPlayer!!.setDataSource(audioURL)
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()
        }catch (e : IOException){
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        //disable the back button
    }
}