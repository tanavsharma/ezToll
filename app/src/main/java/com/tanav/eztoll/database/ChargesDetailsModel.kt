package com.tanav.eztoll.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "charges_details")
class ChargesDetailsModel (
    @ColumnInfo(name = "tracking_date")    var trackingDate: Int,
    @ColumnInfo(name = "start_time")       var startTime: Int,
    @ColumnInfo(name = "start_lat")        var startLat: Double,
    @ColumnInfo(name = "start_lng")        var startLng: Double,
    @ColumnInfo(name = "start_remark")     var startRemark: String,
    @ColumnInfo(name = "end_time")         var endTime: Int,
    @ColumnInfo(name = "end_lat")          var endLat: Double,
    @ColumnInfo(name = "end_lng")          var endLng: Double,
    @ColumnInfo(name = "end_remark")       var endRemark: String,
    @ColumnInfo(name = "meters")           var meters: Int
)
{
    //defining a primary key field Id
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "charges_details_id")   var chargeDetailsId: Int? = null

    override fun toString(): String {
        return "trackingDate:$trackingDate," +
                " startTime:$startTime, startLat:$startLat, startLng:$startLng, startRemark:$startRemark" +
                " endTime:$endTime, endLat:$endLat, endLng$endLng, endRemark:$endRemark meters:$meters"
    }
}