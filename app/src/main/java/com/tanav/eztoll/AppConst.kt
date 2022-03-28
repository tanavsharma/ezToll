package com.tanav.eztoll

object AppConst {
    //Used by shared preference (key definition)
    const val PREF_FILE = "team4"               //shared preference xml filename
    const val PREF_USERNAME = "user_name"       //preference user login name
    const val PREF_VALUE_UNKNOWN = "unknown"    //preference user login name
    const val PREF_DO_TRACKING = "sp_key_do_tracking"       //preference enable tracking or not
    const val TEMP_OFF_DURATION = 60            //Duration to automatically resume the tracking in minutes
    const val NOTIFICATION_ID_REMINDER = 10001  //Notification ID for the tracking enable reminder
    const val ACTION_REMINDER = "com.tanav.eztoll.TrackToggleAlarmReceiver.ACTION_REMINDER"  //Intent filter to reminder alarm
    const val TOLL_ROAD_FILE = "toll_checkpoints.json"
    const val CHANNEL_ID = "eztollServiceChannel"
    const val CHANNEL_NAME = "Eztoll Start Service Channel"
    const val GPS_TOLERANCE =0.0004505          //50 meters. since 0.001 degree = 111 meters

}