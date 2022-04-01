package com.tanav.eztoll.utilities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.os.Build
import android.util.Log
import androidx.preference.PreferenceManager
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.tanav.eztoll.AppConst
import com.tanav.eztoll.BillingReceiver
import com.tanav.eztoll.database.*
import com.tanav.eztoll.models.PointType
import com.tanav.eztoll.models.TollMatchPoint
import org.json.JSONArray
import org.json.JSONTokener
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime

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

        fun todayInInt(): Int {
            val rightNow = LocalDateTime.now()
            return ((rightNow.year * 10000) + (rightNow.month.value * 100) + (rightNow.dayOfMonth))
        }

        fun nextDayInInt(workDate: Int): Int {
            var dateObject = intDate2LocalDate(workDate)
            dateObject = dateObject.plusDays(1)
            return ((dateObject.year * 10000) + (dateObject.month.value * 100) + (dateObject.dayOfMonth))
        }

        fun previousDayInInt(workDate: Int): Int {
            var dateObject = intDate2LocalDate(workDate)
            dateObject = dateObject.plusDays(-1)
            return ((dateObject.year * 10000) + (dateObject.month.value * 100) + (dateObject.dayOfMonth))
        }

        fun intDate2LocalDate(intDate: Int): LocalDateTime {
            val year: Int = intDate / 10000
            val month: Int = (intDate - year * 10000) / 100
            val day: Int = intDate - (year * 10000 + month * 100)
            return LocalDateTime.of(year, month, day, 0, 0, 0)
        }

        fun setApp1stRunDate(context: Context) {
            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            if (pref.getInt(AppConst.PREF_1ST_RUN_DATE, -1) == -1) {
                val editor: SharedPreferences.Editor =  pref.edit()
                editor.putInt(AppConst.PREF_1ST_RUN_DATE, todayInInt())
                editor.commit()
            }
        }

        fun getApp1stRunDate(context: Context): Int {
            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            return pref.getInt(AppConst.PREF_1ST_RUN_DATE, -1)
        }

        fun setAlarmBilling(context: Context) {
            Log.d("sch", "Utility, setAlarmBilling() running")
            // every day at 1 am
            val calendar: Calendar = Calendar.getInstance()

            // if it's after or equal 1 am schedule for next day
            if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 1) {
                calendar.add(Calendar.DAY_OF_YEAR, 1) // add, not set!
            }
            calendar.set(Calendar.HOUR_OF_DAY, 1)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, BillingReceiver::class.java)
            intent.action = AppConst.ACTION_BILLING
            val alarmIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            } else {
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            }
            alarmManager.setInexactRepeating(
                AlarmManager.RTC, calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY, alarmIntent
            )
        }

        //This function should not be called directly from the UI thread
        fun findDailyChargesDetails(context: Context, chargesDate: Int, checkPointJsonArray: JSONArray): List<ChargesDetailsModel> {
            var chargesDetailsList = ArrayList<ChargesDetailsModel>()
            var matchedCheckPointsList = ArrayList<TollMatchPoint>()
            for (i in 0 until checkPointJsonArray.length()) {
                val checkPoint = checkPointJsonArray.getJSONObject(i)
                val lat1 = checkPoint.getDouble("lat") - AppConst.GPS_TOLERANCE
                val lat2 = checkPoint.getDouble("lat") + AppConst.GPS_TOLERANCE
                val lng1 = checkPoint.getDouble("lng") - AppConst.GPS_TOLERANCE
                val lng2 = checkPoint.getDouble("lng") + AppConst.GPS_TOLERANCE
                val matchedTrackPointList = TrackingRepository.matchTrackPoint(context, chargesDate, lat1, lat2, lng1, lng2)
                if (matchedTrackPointList != null) {
                    //merge info of both json check points and the route tracking point
                    for (matchedTrackPoint in matchedTrackPointList) {
                        val tollMatchPoint = TollMatchPoint(
                            chargesDate,
                            matchedTrackPoint.trackingTime,
                            checkPoint.getInt("id"),
                            PointType.valueOf(checkPoint.getString("type")),
                            checkPoint.getDouble("lat"),
                            checkPoint.getDouble("lng"),
                            checkPoint.getString("remark")
                        )
                        matchedCheckPointsList.add(tollMatchPoint)
                    }
                }
            }
            //sort the matchedCheckPointsList by time order
            matchedCheckPointsList = ArrayList(matchedCheckPointsList.sortedWith(compareBy({ it.trackingTime })))

            Log.d("sch", "Utility, findDailyChargesDetails(), matchedCheckPointsList:$matchedCheckPointsList")

            if (matchedCheckPointsList.isNotEmpty()) {
                //there should be 2 check points (start & end points) plus at least 1 mid-point for a valid charge
                if (matchedCheckPointsList.size >= 3) {
                    var count = 0
                    while (count < matchedCheckPointsList.size) {
                        var tempChargesDetails = ChargesDetailsModel(0,0,0.0,0.0,"", 0,0.0,0.0,"",0)
                        var currentStartPointId: Int = -999999999
                        //the starting point must not be a mid-point
                        if (matchedCheckPointsList[count].type == PointType.CHECKPOINT ) {
                            //save info of the starting point
                            tempChargesDetails.trackingDate = chargesDate
                            tempChargesDetails.startTime = matchedCheckPointsList[count].trackingTime
                            tempChargesDetails.startLat = matchedCheckPointsList[count].lat
                            tempChargesDetails.startLng = matchedCheckPointsList[count].lng
                            tempChargesDetails.startRemark = matchedCheckPointsList[count].remark
                            currentStartPointId = matchedCheckPointsList[count].matchPointId
                            count++
                            //the 2nd point must be a mid point
                            if (count < matchedCheckPointsList.size && matchedCheckPointsList[count].type == PointType.MIDPOINT) {
                                //loop until the next check point found
                                do {
                                    count++
                                } while (count < matchedCheckPointsList.size && matchedCheckPointsList[count].type != PointType.CHECKPOINT)
                                //at this moment, found the next check point or no more points to handle can exit the while loop
                                if (count < matchedCheckPointsList.size) {
                                    //check the next check point whether it is in sequence
                                    if (kotlin.math.abs(currentStartPointId - matchedCheckPointsList[count].matchPointId) == 1) {
                                        tempChargesDetails.endTime = matchedCheckPointsList[count].trackingTime
                                        tempChargesDetails.endLat = matchedCheckPointsList[count].lat
                                        tempChargesDetails.endLng = matchedCheckPointsList[count].lng
                                        tempChargesDetails.endRemark = matchedCheckPointsList[count].remark
                                        //get the distance in meters between the 2 check points
                                        val startLatLng = LatLng(tempChargesDetails.startLat,
                                            tempChargesDetails.startLng
                                        )
                                        val endLatLng = LatLng(tempChargesDetails.endLat,
                                            tempChargesDetails.endLng
                                        )
                                        tempChargesDetails.meters =
                                            SphericalUtil.computeDistanceBetween( startLatLng, endLatLng).toInt()
                                        chargesDetailsList.add(tempChargesDetails)
                                    }
                                    //do not increment the count as the ending point will be the next starting point
                                }
                            } else {
                                //do nothing here
                                //discard the 1st point and take the current point as the next starting check point
                            }
                        } else {
                            //1st point is not a check point, so skip it
                            count++
                        }
                    }
                }
            }
            Log.d("sch", "Utility, findDailyChargesDetails() computed, chargesDetailsList:$chargesDetailsList")
            chargesDetailsList = removeZeroChargesDetails(chargesDetailsList)
            chargesDetailsList = mergeChargesDetails(chargesDetailsList)

            Log.d("sch", "Utility, findDailyChargesDetails() merged, chargesDetailsList:$chargesDetailsList")
            return chargesDetailsList
        }

        private fun removeZeroChargesDetails(cdList: ArrayList<ChargesDetailsModel>): ArrayList<ChargesDetailsModel> {
            for (cd in cdList) {
                if (cd.meters == 0)
                    cdList.remove(cd)
            }
            return cdList
        }

        private fun mergeChargesDetails(cdList: ArrayList<ChargesDetailsModel>): ArrayList<ChargesDetailsModel> {
            var i = 0
            while (i < (cdList.size - 1)) {
                if (cdList[i].endLat == cdList[i+1].startLat && cdList[i].endLng == cdList[i+1].startLng) {
                    cdList[i].endTime = cdList[i+1].endTime
                    cdList[i].endLat  = cdList[i+1].endLat
                    cdList[i].endLng  = cdList[i+1].endLng
                    cdList[i].endRemark = cdList[i+1].endRemark
                    cdList[i].meters = cdList[i].meters + cdList[i+1].meters
                    cdList.removeAt(i+1)
                    i--   //work again on the current record as the next record can be in a chain
                }
                i++
            }
            return cdList
        }
    }
}