package com.tanav.eztoll.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class TrackingViewModel: ViewModel() {
    // calling repository tasks and
    // sending the results to the Activity
    private var liveDataTracking: LiveData<TrackingModel>? = null
    private var liveDataTrackingList: LiveData<List<TrackingModel>>? = null

    //
    fun insertTracking(context: Context, trackingDate: Int, trackingTime: Int, lat: Double, lng: Double) {
        TrackingRepository.insertTracking(context, trackingDate, trackingTime, lat, lng)
    }

    fun getTrackingByDate(context: Context, trackingDate: Int) : LiveData<List<TrackingModel>>? {
        liveDataTrackingList = TrackingRepository.getTrackingByDate(context, trackingDate)
        return liveDataTrackingList
    }
}