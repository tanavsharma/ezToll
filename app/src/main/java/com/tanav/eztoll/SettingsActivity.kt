package com.tanav.eztoll

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager


class SettingsActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var pref: SharedPreferences
    var trackToggleAlarm: TrackToggleAlarmReceiver = TrackToggleAlarmReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pref = PreferenceManager.getDefaultSharedPreferences(this)

        if (supportFragmentManager.findFragmentById(android.R.id.content) == null) {
            supportFragmentManager.beginTransaction()
                .add(android.R.id.content, SettingsFragment()).commit()
        }
    }

    override fun onResume() {
        super.onResume()
        pref.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        pref.unregisterOnSharedPreferenceChangeListener(this)
    }


    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        Log.d("sch", "SettingsActivity, onSharedPreferenceChanged running")
        if (key == "sp_key_do_tracking") {
            if (pref.getBoolean("sp_key_do_tracking", true)) {
                Log.d("sch", getString(R.string.sp_do_tracking_enabled))
                Toast.makeText(this, getString(R.string.sp_do_tracking_enabled), Toast.LENGTH_SHORT).show()
                trackToggleAlarm.cancelAlarm(this)
            } else {
                Log.d("sch", getString(R.string.sp_do_tracking_disabled))
                Toast.makeText(this, getString(R.string.sp_do_tracking_disabled), Toast.LENGTH_SHORT).show()
                trackToggleAlarm.setAlarm(this)
            }
        }
    }

}