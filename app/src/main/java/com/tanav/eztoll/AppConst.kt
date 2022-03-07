package com.tanav.eztoll

object AppConst {
    //Used by shared preference (key definition)
    const val PREF_FILE = "team4"               //shared preference xml filename
    const val TEMP_OFF_DURATION = 60             //Duration to automatically resume the tracking in minutes
    const val NOTIFICATION_ID_REMINDER = 10001  //Notification ID for the tracking enable reminder
    const val ACTION_REMINDER = "com.tanav.eztool.TrackToggleAlarmReceiver.ACTION_REMINDER"  //Intent filter to reminder alarm

    const val TOLL_ROAD_FILE = "toll_checkpoints.json"

}