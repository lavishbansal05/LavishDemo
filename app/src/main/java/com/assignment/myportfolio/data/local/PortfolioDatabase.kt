package com.assignment.myportfolio.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [HoldingDBEntity::class], version = 1, exportSchema = false)
abstract class PortfolioDatabase : RoomDatabase() {
	abstract fun holdingsDao(): HoldingsDao

	companion object {
		@Volatile private var INSTANCE: PortfolioDatabase? = null
		fun get(context: Context): PortfolioDatabase = INSTANCE ?: synchronized(this) {
			INSTANCE ?: Room.databaseBuilder(
				context.applicationContext,
				PortfolioDatabase::class.java,
				"portfolio.db"
			).fallbackToDestructiveMigration().build().also { INSTANCE = it }
		}
	}
} 