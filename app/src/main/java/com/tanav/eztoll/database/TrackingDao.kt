package com.tanav.eztoll.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// Room DAO - Data Access Object Interface
// this interface declares database functions
// and does the mapping of SQL queries to functions
@Dao
interface TrackingDao {
    //defining an insert method using @Insert Annotation
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTracking(trackingModel: TrackingModel)

    //defining a query method using @Query Annotation
    @Query("SELECT * FROM tracking WHERE tracking_date =:trackingDate ORDER BY tracking_time ASC")
    fun getTrackingByDate(trackingDate: Int) : LiveData<List<TrackingModel>>

    @Query("SELECT * FROM tracking WHERE tracking_date =:trackingDate AND (lat between :lat1 AND :lat2) AND (lng between :lng1 AND :lng2)")
    fun matchTrackPoint(trackingDate: Int, lat1: Double, lat2: Double, lng1: Double, lng2: Double) : List<TrackingModel>
}