package com.assignment.myportfolio.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [HoldingDBEntity::class], version = 1)
abstract class PortfolioDatabase : RoomDatabase() {
	abstract fun holdingsDao(): HoldingsDao
}
