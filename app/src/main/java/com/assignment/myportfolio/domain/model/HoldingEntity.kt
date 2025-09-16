package com.assignment.myportfolio.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class HoldingEntity(
	val symbol: String,
	val quantity: Double,
	val averagePrice: Double,
	val close: Double,
	val ltp: Double,
) {
    val investmentValue: Double get() = averagePrice * quantity
    val currentValue: Double get() = ltp * quantity
    private val todayPnlPerShare: Double get() = close - ltp
    val todayPnl: Double get() = todayPnlPerShare * quantity
    val totalPnl: Double get() = currentValue - investmentValue
}

@Serializable
data class PortfolioSummary(
	val currentValue: Double,
	val totalInvestment: Double,
	val totalPnl: Double,
	val todaysPnl: Double,
) {
    val totalPnlPercent: Double get() = if (totalInvestment == 0.0) 0.0 else (totalPnl / totalInvestment) * 100.0
} 