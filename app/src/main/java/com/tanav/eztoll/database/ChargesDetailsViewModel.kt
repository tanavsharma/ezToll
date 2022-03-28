package com.tanav.eztoll.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class ChargesDetailsViewModel: ViewModel() {
    // calling repository tasks and
    // sending the results to the Activity
    var liveDataChargesDetailsList: LiveData<List<ChargesDetailsModel>>? = null

    //
    fun insertChargesDetails(context: Context, trackingDate: Int,
                             startTime: Int, startLat: Double, startLng: Double, startRemark: String,
                             endTime: Int, endLat: Double, endLng: Double, endRemark: String,
                             meters: Int
    ) {
        TrackingRepository.insertChargesDetails(context, trackingDate, startTime, startLat, startLng, startRemark,
            endTime, endLat, endLng, endRemark, meters )
    }

    fun getChargesDetailsByDate(context: Context, trackingDate: Int) : LiveData<List<ChargesDetailsModel>>? {
        liveDataChargesDetailsList = TrackingRepository.getChargesDetailsByDate(context, trackingDate)
        return liveDataChargesDetailsList
    }
}