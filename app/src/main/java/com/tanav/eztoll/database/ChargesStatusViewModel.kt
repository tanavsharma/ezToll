package com.tanav.eztoll.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class ChargesStatusViewModel: ViewModel() {
    // calling repository tasks and
    // sending the results to the Activity
    var liveDataChargesStatus: LiveData<ChargesStatusModel>? = null
    var liveDataChargesStatusList: LiveData<List<ChargesStatusModel>>? = null

    //
    fun insertChargesStatus(context: Context, trackingDate: Int, totalKm: Double, totalAmount: Double, paidDate: Int) {
        TrackingRepository.insertChargesStatus(context, trackingDate, totalKm, totalAmount, paidDate)
    }

    fun updateChargesStatusPaidDate(context: Context, paidDate: Int) {
        TrackingRepository.updateChargesStatusPaidDate(context, paidDate)
    }

    fun getChargesStatusByDate(context: Context, trackingDate: Int) : LiveData<ChargesStatusModel>? {
        liveDataChargesStatus = TrackingRepository.getChargesStatusByDate(context, trackingDate)
        return liveDataChargesStatus
    }
    fun getUnPaidChargesStatus(context: Context) : LiveData<List<ChargesStatusModel>>? {
        liveDataChargesStatusList = TrackingRepository.getUnPaidChargesStatus(context)
        return liveDataChargesStatusList
    }
}