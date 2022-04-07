package com.tanav.eztoll

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class HelpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        val newtext = findViewById<View>(R.id.textView9) as TextView
        newtext.movementMethod = LinkMovementMethod.getInstance()
        val newtextDeviceList = findViewById<View>(R.id.textView11) as TextView
        newtextDeviceList.movementMethod = LinkMovementMethod.getInstance()
        val newtextPrivacy = findViewById<View>(R.id.textView12) as TextView
        newtextPrivacy.movementMethod = LinkMovementMethod.getInstance()
        val newtextRefund = findViewById<View>(R.id.textView10) as TextView
        newtextRefund.movementMethod = LinkMovementMethod.getInstance()
    }
    fun goToLink(view: View?) {
        goToUrl("https://iahmed851.wixsite.com/eztoll")
    }

    private fun goToUrl(url: String) {
        val uriUrl: Uri = Uri.parse(url)
        val launchBrowser = Intent(Intent.ACTION_VIEW, uriUrl)
        startActivity(launchBrowser)
    }
}