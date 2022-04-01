package com.tanav.eztoll.database

import android.content.Context
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class TrackingRepository {

    //defining database and live data object as companion objects
    companion object {
        private var trackingDatabase: TrackingDatabase? = null
        private var trackingModel: LiveData<TrackingModel>? = null
        private var trackingModelList: LiveData<List<TrackingModel>>? = null
        private var chargesDetailsModelList: LiveData<List<ChargesDetailsModel>>? = null
        private var chargesStatusModel: LiveData<ChargesStatusModel>? = null
        private var chargesStatusModelList: LiveData<List<ChargesStatusModel>>? = null

        //initialize database
        private fun initializeDB(context: Context) : TrackingDatabase {
            return TrackingDatabase.getInstance(context)
        }

        //Initialize insertTracking()
        fun insertTracking(context: Context, trackDate: Int, trackTime: Int, lat: Double, lng: Double) {
            trackingDatabase = initializeDB(context)

            CoroutineScope(IO).launch {
                val trackingDetails = TrackingModel(
                    trackDate,
                    trackTime,
                    lat,
                    lng
                )
                trackingDatabase!!.trackingDao().insertTracking(trackingDetails)
            }
        }

        //Initialize getTrackingByDate()
        fun getTrackingByDate(context: Context, trackingDate: Int) : LiveData<List<TrackingModel>>? {
            trackingDatabase = initializeDB(context)
            trackingModelList = trackingDatabase!!.trackingDao().getTrackingByDate(trackingDate)
            return trackingModelList
        }

        //Initialize matchTrackPoint(). No livedata support
        fun matchTrackPoint(
            context: Context, trackingDate: Int,
            lat1: Double, lat2: Double,
            lng1: Double, lng2: Double
        ): List<TrackingModel>? {
            trackingDatabase = initializeDB(context)
            return trackingDatabase!!.trackingDao()
                .matchTrackPoint(trackingDate, lat1, lat2, lng1, lng2)
        }

        //Initialize insertChargesDetails()
        fun insertChargesDetails(context: Context, trackDate: Int,
                                 startTime: Int, startLat: Double, startLng: Double, startRemark: String,
                                 endTime: Int, endLat: Double, endLng: Double, endRemark: String,
                                 meters: Int
            ) {
            trackingDatabase = initializeDB(context)

            CoroutineScope(IO).launch {
                val chargesDetails = ChargesDetailsModel(
                    trackDate,
                    startTime, startLat, startLng, startRemark,
                    endTime, endLat, endLng, endRemark,
                    meters
                )
                trackingDatabase!!.chargesDetailsDao().insertChargesDetails(chargesDetails)
            }
        }

        //Initialize getChargesDetailsByDate()
        fun getChargesDetailsByDate(context: Context, trackingDate: Int) : LiveData<List<ChargesDetailsModel>>? {
            trackingDatabase = initializeDB(context)
            chargesDetailsModelList = trackingDatabase!!.chargesDetailsDao().getChargesDetailsByDate(trackingDate)
            return chargesDetailsModelList
        }

        //Initialize insertChargesStatus()
        fun insertChargesStatus(context: Context, trackDate: Int, totalKm: Double, totalAmount: Double, paidDate: Int ) {
            trackingDatabase = initializeDB(context)

            CoroutineScope(IO).launch {
                val chargesStatus = ChargesStatusModel(trackDate, totalKm, totalAmount, paidDate)
                trackingDatabase!!.chargesStatusDao().insertChargesStatus(chargesStatus)
            }
        }

        //Initialize updateChargesStatusPaidDate()
        fun updateChargesStatusPaidDate(context: Context, paidDate: Int) {
            trackingDatabase = initializeDB(context)

            CoroutineScope(IO).launch {
                trackingDatabase!!.chargesStatusDao().updateChargesStatusPaidDate(paidDate)
            }
        }

        //Initialize getChargesStatusByDate()
        fun getChargesStatusByDate(context: Context, trackingDate: Int) : LiveData<ChargesStatusModel>? {
            trackingDatabase = initializeDB(context)
            chargesStatusModel = trackingDatabase!!.chargesStatusDao().getChargesStatusByDate(trackingDate)
            return chargesStatusModel
        }

        //Initialize getUnPaidChargesStatus()
        fun getUnPaidChargesStatus(context: Context) : LiveData<List<ChargesStatusModel>>? {
            trackingDatabase = initializeDB(context)
            chargesStatusModelList = trackingDatabase!!.chargesStatusDao().getUnPaidChargesStatus()
            return chargesStatusModelList
        }

        //Initialize getLatestChargesStatus() no live data
        fun getLatestChargesStatus(context: Context) : ChargesStatusModel? {
            trackingDatabase = initializeDB(context)
            return trackingDatabase!!.chargesStatusDao().getLatestChargesStatus()
        }
    }
}