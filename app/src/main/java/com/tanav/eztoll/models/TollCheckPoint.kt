package com.tanav.eztoll.models

data class  TollCheckPoint(
    val id: Int,
    val type: PointType,
    val lat: Double,
    val lng: Double,
    val remark: String
) {

}