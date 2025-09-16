package com.assignment.myportfolio.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HoldingsDao {
	@Query("SELECT * FROM holdings WHERE user_id = :userId ORDER BY symbol")
	suspend fun getHoldings(userId: String): List<HoldingDBEntity>

	@Query("DELETE FROM holdings WHERE user_id = :userId")
	suspend fun clearForUser(userId: String)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun upsertAll(items: List<HoldingDBEntity>)
} 