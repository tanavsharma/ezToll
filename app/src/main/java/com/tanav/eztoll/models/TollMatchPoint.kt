package com.tanav.eztoll.models

data class TollMatchPoint (
    var trackingDate: Int,
    var trackingTime: Int,
    val matchPointId: Int,      //from TollCheckPoint object
    val type: PointType,        //from TollCheckPoint object
    val lat: Double,            //from TollCheckPoint object
    val lng: Double,            //from TollCheckPoint object
    val remark: String          //from TollCheckPoint object
) {
    override fun toString(): String {
        return "trackingDate:$trackingDate, " +
                "trackingTime:$trackingTime, " +
                "matchPointId:$matchPointId, " +
                "type:$type, " +
                "lat:$lat, " +
                "lng:$lng "
    }
}