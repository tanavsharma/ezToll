package com.tanav.eztoll

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tanav.eztoll.services.AutoStartService

class AutoStartReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val serviceIntent = Intent(context, AutoStartService::class.java)
        context!!.startForegroundService(serviceIntent)
    }
}