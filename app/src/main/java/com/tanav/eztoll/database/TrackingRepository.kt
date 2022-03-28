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

    }
}