package com.example.rouletteinsight.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spins")
data class SpinEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val number: Int,
    val sessionId: String,
    val timestamp: Long = System.currentTimeMillis()
)
