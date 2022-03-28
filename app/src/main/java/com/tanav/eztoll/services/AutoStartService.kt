package com.tanav.eztoll.services

import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import android.app.*
import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.google.android.gms.location.*
import com.tanav.eztoll.AppConst
import com.tanav.eztoll.MainActivity
import com.tanav.eztoll.R
import com.tanav.eztoll.database.TrackingRepository
import com.tanav.eztoll.database.TrackingViewModel
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class AutoStartService() : Service() {
    private lateinit var pref: SharedPreferences

    private lateinit var notificationManager: NotificationManager
    // FusedLocationProviderClient - Main class for receiving location updates.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    // LocationRequest - Requirements for the location updates, i.e., how often you should receive
    // updates, the priority, etc.
    private lateinit var locationRequest: LocationRequest
    // LocationCallback - Called when FusedLocationProviderClient has a new Location.
    private lateinit var locationCallback: LocationCallback

    // Used only for local storage of the last known location.
    private var currentLocation: Location? = null
    private var isFreshStart: Boolean = true

    override fun onCreate() {
        super.onCreate()
        Log.d("sch", "AutoStartService, onCreate()")
        pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create().apply {
            // Sets the desired interval for active location updates. This interval is inexact. You
            // may not receive updates at all if no location sources are available, or you may
            // receive them less frequently than requested. You may also receive updates more
            // frequently than requested if other applications are requesting location at a more
            // frequent interval.
            //
            // IMPORTANT NOTE: Apps running on Android 8.0 and higher devices (regardless of
            // targetSdkVersion) may receive updates less frequently than this interval when the app
            // is no longer in the foreground.
            interval = TimeUnit.SECONDS.toMillis(10)

            // Sets the fastest rate for active location updates. This interval is exact, and your
            // application will never receive updates more frequently than this value.
            fastestInterval = TimeUnit.SECONDS.toMillis(3)

            // Sets the maximum time when batched location updates are delivered. Updates may be
            // delivered sooner than this interval.
            maxWaitTime = TimeUnit.SECONDS.toMillis(15)

            //Set the minimum displacement between location updates in meters
            smallestDisplacement = 30F

            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        //Initialize the LocationCallback.
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult ?: return        //exit if no location results

                //Save a new location to a database
                currentLocation = locationResult.lastLocation
                //val now: Long = System.currentTimeMillis()
                val rightNow = LocalDateTime.now()
                val today = (rightNow.year * 10000) + (rightNow.month.value * 100) + (rightNow.dayOfMonth)
                val currentTime = rightNow.hour * 10000 + rightNow.minute * 100 + rightNow.second
                Log.d("sch", "today=$today, currentTime=$currentTime")

                val isTackingEnabled = pref.getBoolean(AppConst.PREF_DO_TRACKING, true)

                Log.d("sch", "isTackingEnabled=$isTackingEnabled")
                if (isTackingEnabled && !isFreshStart) {
                    Log.d("sch",  "AutoStartService, currentLocation=" + currentLocation!!.latitude + "," + currentLocation!!.longitude)
                    TrackingRepository.insertTracking(applicationContext, today, currentTime, currentLocation!!.latitude, currentLocation!!.longitude)
                }
                isFreshStart = false
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val input = intent!!.getStringExtra("inputExtra")
        val notificationIntent = Intent(this, MainActivity::class.java)
        //change to support api 32 only
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        //this is to support api 30 only
        //val pendingIntent = PendingIntent.getActivity(
        //    this,
        //    0, notificationIntent, 0
        //)
        val notification: Notification = NotificationCompat.Builder(this, AppConst.CHANNEL_ID)
            .setContentTitle("Auto Start Service")
            .setContentText(input)
            .setSmallIcon(R.drawable.ic_eztoll_small_icon)
            .setContentIntent(pendingIntent)
            .build()
        val channel = NotificationChannel(
            AppConst.CHANNEL_ID,
            AppConst.CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
        NotificationCompat.Builder(this, AppConst.CHANNEL_ID)
        startForeground(1, notification)

        subscribeToLocationUpdates()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        Log.d("sch", "AutoStartService, onDestroy()")
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null;
    }

    private fun subscribeToLocationUpdates() {
        Log.d(TAG, "subscribeToLocationUpdates()")

        try {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permissions. Couldn't remove updates. $unlikely")
        }
    }

    private fun unsubscribeToLocationUpdates() {
        Log.d(TAG, "unsubscribeToLocationUpdates()")

        try {
            val removeTask = fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            removeTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Location Callback removed.")
                    stopSelf()
                } else {
                    Log.d(TAG, "Failed to remove Location Callback.")
                }
            }
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permissions. Couldn't remove updates. $unlikely")
        }
    }

    companion object {
        private const val TAG = "AutoStartService"

        private const val PACKAGE_NAME = "com.tanav.eztoll"

        internal const val ACTION_LOCATION_BROADCAST =
            "$PACKAGE_NAME.action.LOCATION_BROADCAST"

        internal const val EXTRA_LOCATION = "$PACKAGE_NAME.extra.LOCATION"
    }
}