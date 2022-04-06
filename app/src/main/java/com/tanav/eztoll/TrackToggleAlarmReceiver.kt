package com.tanav.eztoll

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.SystemClock
import android.util.Log
import androidx.preference.PreferenceManager
import androidx.legacy.content.WakefulBroadcastReceiver
import com.tanav.eztoll.notification.NotificationDecorator

class TrackToggleAlarmReceiver : WakefulBroadcastReceiver() {

    private var alarmMgr: AlarmManager? = null
    // The pending intent that is triggered when the alarm fires.
    private lateinit var alarmIntent: PendingIntent

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("sch", "TrackToggleAlarmReceiver, onReceive() running")

        // resume the tracking
        val pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context!!)
        val editor: SharedPreferences.Editor = pref.edit()
        editor.putBoolean(AppConst.PREF_DO_TRACKING, true)
        editor.commit()

        sendNotification(context!!)
    }

    fun setAlarm(context: Context) {
        Log.d("sch", "TrackToggleAlarmReceiver, setAlarm() running")
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        if (!pref.getBoolean(AppConst.PREF_DO_TRACKING, true)) {
            alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            //val intent = Intent(context, TrackToggleAlarmReceiver::class.java)
            val intent = Intent(AppConst.ACTION_REMINDER)

            alarmIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getBroadcast(context, 0, intent,  PendingIntent.FLAG_MUTABLE)
            } else {
                PendingIntent.getBroadcast(context, 0, intent, 0)
            }
            alarmMgr!!.setExactAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + (AppConst.TEMP_OFF_DURATION * 60000).toLong(), alarmIntent
            )

        }
    }
    fun cancelAlarm(context: Context) {
        Log.d("sch", "TrackToggleAlarmReceiver, cancelAlarm() running")
        // If the alarm has been set, cancel it.
        alarmMgr?.cancel(alarmIntent)

    }

    private fun sendNotification(context: Context) {

        Log.d("sch", "ReminderScheduleService, sendNotification() running...")
        val notificationMgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var notificationDecorator = NotificationDecorator(context, notificationMgr)
        notificationDecorator.displaySimpleNotification(context.getString(R.string.app_name), context.getString(R.string.auto_resume_message))
    }

}