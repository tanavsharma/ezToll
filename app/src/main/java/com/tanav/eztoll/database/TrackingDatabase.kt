package com.tanav.eztoll.database
import android.content.Context
import androidx.room.*

//Room database
@Database(
    entities = [
        TrackingModel::class,
        ChargesDetailsModel::class,
        ChargesStatusModel::class
    ],
    version = 1,
    exportSchema = true
)
abstract class TrackingDatabase: RoomDatabase() {
    abstract fun trackingDao() : TrackingDao
    abstract fun chargesDetailsDao() : ChargesDetailsDao
    abstract fun chargesStatusDao() : ChargesStatusDao

    companion object {
        @Volatile
        private var INSTANCE: TrackingDatabase? = null

        fun getInstance(context: Context) : TrackingDatabase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context,
                    TrackingDatabase::class.java,
                    "TRACKING_DB"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }
}