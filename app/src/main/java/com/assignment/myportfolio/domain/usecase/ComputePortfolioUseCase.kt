package com.assignment.myportfolio.domain.usecase

import com.assignment.myportfolio.domain.model.HoldingEntity
import com.assignment.myportfolio.domain.model.PortfolioSummary

class ComputePortfolioUseCase {
	operator fun invoke(holdingEntities: List<HoldingEntity>): PortfolioSummary {
		var current = 0.0
		var investment = 0.0
		var today = 0.0
		holdingEntities.forEach { h ->
			current += h.currentValue
			investment += h.investmentValue
			today += h.todayPnl
		}
		return PortfolioSummary(
			currentValue = current,
			totalInvestment = investment,
			totalPnl = current - investment,
			todaysPnl = today
		)
	}
} 