package com.assignment.myportfolio.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.assignment.myportfolio.data.PortfolioRepositoryImpl
import com.assignment.myportfolio.data.local.PortfolioDatabase
import com.assignment.myportfolio.data.local.HoldingsDao
import com.assignment.myportfolio.domain.repository.PortfolioRepository
import com.assignment.myportfolio.domain.usecase.ComputePortfolioUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindingsModule {
	@Binds
	@Singleton
	abstract fun bindPortfolioRepository(impl: PortfolioRepositoryImpl): PortfolioRepository
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
	@Provides
	@Singleton
	fun provideContext(app: Application): Context = app.applicationContext

	@Provides
	@Singleton
	fun provideDatabase(context: Context): PortfolioDatabase = 
		Room.databaseBuilder(
			context,
			PortfolioDatabase::class.java,
			"portfolio.db"
		).fallbackToDestructiveMigration().build()

	@Provides
	@Singleton
	fun provideHoldingsDao(db: PortfolioDatabase): HoldingsDao = db.holdingsDao()

	@Provides
	@Singleton
	fun provideComputePortfolioUseCase(): ComputePortfolioUseCase = ComputePortfolioUseCase()
}
