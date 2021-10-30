package com.hofit.greenroad.service_location.db

import androidx.room.*
import javax.inject.Singleton

@Entity (tableName = "triggers_logs")
data class TriggerLog(
    @ColumnInfo(name = "Latitude") val latitude: Double?,
    @ColumnInfo(name = "Longitude") val longitude: Double?,
    @ColumnInfo(name = "Timestamp") val timestamp: Long?
){
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0
}

@Dao
interface TriggerLogsDao {
    @Query("SELECT * FROM triggers_logs")
    fun getAll(): List<TriggerLog>

    @Query("SELECT * FROM triggers_logs WHERE uid IN (:triggerLogsIds)")
    fun loadAllByIds(triggerLogsIds: IntArray): List<TriggerLog>

//    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " + "last_name LIKE :last LIMIT 1")
//    fun findByName(first: String, last: String): TriggerLog

    @Insert
    fun insertAll(vararg locations: TriggerLog)

    @Delete
    fun delete(trigger: TriggerLog)
}

@Database(entities = [TriggerLog::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun triggerLogsDao(): TriggerLogsDao
}
