package com.tanav.eztoll

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.tanav.eztoll.services.BillingJobIntentService
import com.tanav.eztoll.utilities.Utility

class BillingReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.equals("android.intent.action.BOOT_COMPLETED")) {
            Log.d("sch", "BillingReceiver, onReceive(), just booted up")
            Utility.setAlarmBilling(context)
        }
        //call billing job intent service
        Log.d("sch", "BillingReceiver, onReceive(), call job intent service")
        val billingIntent = Intent(context, BillingJobIntentService::class.java)
        BillingJobIntentService.enqueueWork(context, billingIntent)
    }
}