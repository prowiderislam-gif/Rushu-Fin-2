package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val type: String, // "INCOME" or "EXPENSE"
    val description: String,
    val dateString: String,
    val timeString: String,
    val timestamp: Long = System.currentTimeMillis()
)
