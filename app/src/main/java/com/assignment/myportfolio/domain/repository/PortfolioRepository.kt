package com.assignment.myportfolio.domain.repository

import com.assignment.myportfolio.domain.model.HoldingEntity

interface PortfolioRepository {
	suspend fun getHoldings(): Result<List<HoldingEntity>>
} 