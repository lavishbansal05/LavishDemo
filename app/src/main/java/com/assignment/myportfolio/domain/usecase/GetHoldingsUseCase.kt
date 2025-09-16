package com.assignment.myportfolio.domain.usecase

import com.assignment.myportfolio.domain.model.HoldingEntity
import com.assignment.myportfolio.domain.repository.PortfolioRepository
import javax.inject.Inject

class GetHoldingsUseCase @Inject constructor(private val repository: PortfolioRepository) {
	suspend operator fun invoke(forceRefresh: Boolean): Result<List<HoldingEntity>> = repository.getHoldings(forceRefresh)
} 