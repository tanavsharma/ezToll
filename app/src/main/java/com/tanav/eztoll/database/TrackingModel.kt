package com.tanav.eztoll.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracking")
class TrackingModel (
    @ColumnInfo(name = "tracking_date")    var trackingDate: Int,
    @ColumnInfo(name = "tracking_time")    var trackingTime: Int,
    @ColumnInfo(name = "lat")              var lat: Double,
    @ColumnInfo(name = "lng")              var lng: Double
)
{
    //defining a primary key field Id
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "tracking_id")       var trackingId: Int? = null

    override fun toString(): String {
        return "trackingDate:$trackingDate, trackingTime:$trackingTime, lat:$lat, lng:$lng"
    }
}