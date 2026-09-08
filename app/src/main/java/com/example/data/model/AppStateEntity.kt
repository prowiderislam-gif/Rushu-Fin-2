package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_state")
data class AppStateEntity(
    @PrimaryKey
    val id: Int = 1,
    val initialBalance: Double = 0.0,
    val tier1Password: String = "1234",
    val tier2Password: String = "9999",
    val currencySymbol: String = "₹",
    val lastSyncTime: Long = 0L,
    val googleAccountEmail: String? = null,
    val peakLiability: Double = 0.0,
    val showLiabilities: Boolean = true
)
