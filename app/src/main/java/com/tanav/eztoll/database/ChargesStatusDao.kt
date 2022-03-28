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
interface ChargesStatusDao {
    //defining an insert method using @Insert Annotation
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChargesStatus(chargesStatusModel: ChargesStatusModel)

    //defining a query method using @Query Annotation
    @Query("SELECT * FROM charges_status WHERE tracking_date =:trackingDate")
    fun getChargesStatusByDate(trackingDate: Int) : LiveData<ChargesStatusModel>
}