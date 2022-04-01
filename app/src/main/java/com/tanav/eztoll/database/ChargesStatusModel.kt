package com.tanav.eztoll.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "charges_status")
class ChargesStatusModel (
    @ColumnInfo(name = "tracking_date")    var trackingDate: Int,
    @ColumnInfo(name = "total_km")         var totalKm: Double,
    @ColumnInfo(name = "total_amount")     var totalAmount: Double,
    @ColumnInfo(name = "paid_date")        var paidDate: Int
)
{
    //defining a primary key field Id
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "charges_status_id")   var chargeStatusId: Int? = null

    override fun toString(): String {
        return "trackingDate:$trackingDate, totalKm:$totalKm, totalAmount:$totalAmount, paidDate$paidDate"
    }
}