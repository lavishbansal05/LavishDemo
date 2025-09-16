package com.assignment.myportfolio.data.remote

import retrofit2.Retrofit
import javax.inject.Inject

class PortfolioServiceFactory @Inject constructor(private val retrofit: Retrofit) {
	fun getPortfolioService(): PortfolioService {
		return retrofit.create(PortfolioService::class.java)
	}
}
