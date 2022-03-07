package com.tanav.eztoll.utilities

import android.content.Context
import android.util.Log
import com.tanav.eztoll.AppConst
import org.json.JSONArray
import org.json.JSONTokener
import java.io.IOException

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
            Log.d("sch", "Utility.readCheckPointData(), json string:" + jsonString)
            return JSONTokener(jsonString).nextValue() as JSONArray
        }
    }
}