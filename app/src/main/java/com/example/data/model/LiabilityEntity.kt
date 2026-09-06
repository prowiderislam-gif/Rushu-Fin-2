package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "liabilities")
data class LiabilityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val actionType: String, // "ADD_LIABILITY" (increasing debt) or "PAY_LIABILITY" (decreasing debt)
    val description: String,
    val dateString: String,
    val timeString: String,
    val timestamp: Long = System.currentTimeMillis()
)
