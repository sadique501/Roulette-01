package com.example.rouletteinsight.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SpinDao {
    @Insert
    suspend fun insert(spin: SpinEntity)

    @Query("SELECT * FROM spins ORDER BY id DESC")
    fun observeNewestFirst(): Flow<List<SpinEntity>>

    @Query("DELETE FROM spins WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM spins")
    suspend fun deleteAll()
}
