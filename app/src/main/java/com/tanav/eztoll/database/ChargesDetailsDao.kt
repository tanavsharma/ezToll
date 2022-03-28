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
interface ChargesDetailsDao {
    //defining an insert method using @Insert Annotation
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChargesDetails(chargesDetailsModel: ChargesDetailsModel)

    //defining a query method using @Query Annotation
    @Query("SELECT * FROM charges_details WHERE tracking_date =:trackingDate ORDER BY start_time ASC")
    fun getChargesDetailsByDate(trackingDate: Int) : LiveData<List<ChargesDetailsModel>>
}