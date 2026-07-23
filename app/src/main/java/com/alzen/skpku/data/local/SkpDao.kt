package com.alzen.skpku.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface SkpDao {
    @Query("SELECT * FROM skp_records ORDER BY timestamp DESC")
    fun getAllSkpRecords(): Flow<List<SkpEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkpRecords(records: List<SkpEntity>)

    @Query("DELETE FROM skp_records")
    suspend fun clearAllRecords()

    /**
     * Updates the local cache atomically within a transaction.
     * This prevents the UI from showing an empty state if the operation is interrupted.
     */
    @Transaction
    suspend fun updateLocalCache(records: List<SkpEntity>) {
        clearAllRecords()
        insertSkpRecords(records)
    }
}
