package com.example.hastashilpa.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.hastashilpa.data.model.MaterialLog
import kotlinx.coroutines.flow.Flow

@Dao
interface MaterialLogDao {
    @Insert
    suspend fun insert(log: MaterialLog)

    @Query("SELECT * FROM material_logs ORDER BY id DESC")
    fun getAllLogs(): Flow<List<MaterialLog>>

    @Query("DELETE FROM material_logs WHERE id = :id")
    suspend fun deleteById(id: Int)
}