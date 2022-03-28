package com.tanav.eztoll.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class ChargesStatusViewModel: ViewModel() {
    // calling repository tasks and
    // sending the results to the Activity
    var liveDataChargesStatus: LiveData<ChargesStatusModel>? = null

    //
    fun insertChargesStatus(context: Context, trackingDate: Int, invoiceId: Int) {
        TrackingRepository.insertChargesStatus(context, trackingDate, invoiceId)
    }

    fun getChargesStatusByDate(context: Context, trackingDate: Int) : LiveData<ChargesStatusModel>? {
        liveDataChargesStatus = TrackingRepository.getChargesStatusByDate(context, trackingDate)
        return liveDataChargesStatus
    }
}