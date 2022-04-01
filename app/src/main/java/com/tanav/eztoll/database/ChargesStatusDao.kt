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

    //defining a update method using @Query Annotation
    @Query("UPDATE charges_status SET paid_date = :paidDate WHERE paid_date = 0")
    fun updateChargesStatusPaidDate(paidDate: Int)

    //defining a query method using @Query Annotation
    @Query("SELECT * FROM charges_status WHERE tracking_date =:trackingDate")
    fun getChargesStatusByDate(trackingDate: Int) : LiveData<ChargesStatusModel>

    //defining a query method using @Query Annotation
    @Query("SELECT * FROM charges_status WHERE paid_date = 0 ORDER BY tracking_date DESC")
    fun getUnPaidChargesStatus() : LiveData<List<ChargesStatusModel>>

    //defining a query method using @Query Annotation
    @Query("SELECT * FROM charges_status ORDER BY tracking_date DESC LIMIT 1")
    fun getLatestChargesStatus() : ChargesStatusModel?
}