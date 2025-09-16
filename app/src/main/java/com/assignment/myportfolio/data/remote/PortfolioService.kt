package com.assignment.myportfolio.data.remote

import com.assignment.myportfolio.data.remote.dto.PortfolioResponse
import retrofit2.http.GET

interface PortfolioService {
	@GET("/")
	suspend fun getPortfolio(): PortfolioResponse
} 