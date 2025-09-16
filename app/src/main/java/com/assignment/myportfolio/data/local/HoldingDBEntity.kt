package com.assignment.myportfolio.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "holdings")
data class HoldingDBEntity(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	@ColumnInfo(name = "user_id") val userId: String,
	@ColumnInfo(name = "symbol") val symbol: String,
	@ColumnInfo(name = "quantity") val quantity: Double,
	@ColumnInfo(name = "ltp") val ltp: Double,
	@ColumnInfo(name = "avg_price") val avgPrice: Double,
	@ColumnInfo(name = "close") val close: Double,
	@ColumnInfo(name = "updated_at") val updatedAt: Long
) 