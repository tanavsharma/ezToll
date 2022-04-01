package com.tanav.eztoll.services

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.core.app.JobIntentService
import com.tanav.eztoll.AppConst
import com.tanav.eztoll.R
import com.tanav.eztoll.database.TrackingRepository
import com.tanav.eztoll.notification.NotificationDecorator
import com.tanav.eztoll.utilities.Utility
import org.json.JSONArray
import java.util.*
import kotlin.math.roundToInt

class BillingJobIntentService: JobIntentService() {

    companion object {
        private const val JOB_ID = 9001

        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, BillingJobIntentService::class.java, JOB_ID, work)
        }
    }

    override fun onHandleWork(intent: Intent) {
        doBilling()
        stopSelf()
    }

    private fun doBilling() {
        val app1stRunDate = Utility.getApp1stRunDate(applicationContext)
        val LatestChargesStatus = TrackingRepository.getLatestChargesStatus(applicationContext)
        var calculationStartDate =
            if (LatestChargesStatus == null) { app1stRunDate }
            else { Utility.nextDayInInt(LatestChargesStatus.trackingDate) }
        //The billing is triggered after midnight. The end date is the day before
        val calculationEndDate = Utility.previousDayInInt(Utility.todayInInt())

        Log.d("sch", "BillingJobIntentService, doBilling(), calculationStartDate$calculationStartDate, calculationEndDate$calculationEndDate")
        //the device can be re-boot several times in a day. The billing may have been calculated already
        if (calculationEndDate >= calculationStartDate) {
            val checkPointJsonArray = Utility.readCheckPointData(applicationContext)
            for (theDate in calculationStartDate..calculationEndDate) {
                val chargesDetailsList = Utility.findDailyChargesDetails(
                    applicationContext,
                    theDate,
                    checkPointJsonArray
                )
                if (chargesDetailsList.isNotEmpty()) {
                    var totalMeters = 0F
                    for (cd in chargesDetailsList) {
                        totalMeters += cd.meters
                    }
                    var totalCharges = (AppConst.UNIT_CHARGES_PER_METER * totalMeters)
                    val totalKm = ((totalMeters / 1000) * 100.0).roundToInt() / 100.0
                    TrackingRepository.insertChargesStatus(
                        applicationContext,
                        theDate,
                        totalKm,
                        totalCharges,
                        0
                    )
                    sendNotification(applicationContext)
                }
            }
        }
    }

    private fun sendNotification(context: Context) {
        Log.d("sch", "BillingJobIntentService, sendNotification() running...")
        val notificationMgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var notificationDecorator = NotificationDecorator(context, notificationMgr)
        notificationDecorator.displaySimpleNotification(context.getString(R.string.app_name), context.getString(
            R.string.outstanding_charges_message))
    }
}