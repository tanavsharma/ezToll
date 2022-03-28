package com.tanav.eztoll.utilities

import android.content.Context
import android.util.Log
import androidx.lifecycle.Observer
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.tanav.eztoll.AppConst
import com.tanav.eztoll.database.*
import com.tanav.eztoll.fragments.TrackMapFragment
import com.tanav.eztoll.models.PointType
import com.tanav.eztoll.models.TollMatchPoint
import org.json.JSONArray
import org.json.JSONTokener
import java.io.IOException
import java.lang.Math.abs

class Utility {
    companion object {
        fun readCheckPointData(context: Context): JSONArray {
            var jsonString = ""
            try {
                jsonString =
                    context.assets.open(AppConst.TOLL_ROAD_FILE).bufferedReader()
                        .use { it.readText() }
            } catch (ioException: IOException) {
                ioException.printStackTrace()
                Log.d("sch", "Utility.readCheckPointData() exception")
            }
            //Log.d("sch", "Utility.readCheckPointData(), json string:" + jsonString)
            return JSONTokener(jsonString).nextValue() as JSONArray
        }
    }
}