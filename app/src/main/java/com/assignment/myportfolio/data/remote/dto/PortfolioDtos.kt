package com.assignment.myportfolio.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PortfolioResponse(
	@SerialName("data") val data: PortfolioData
)

@Serializable
data class PortfolioData(
	@SerialName("userHolding") val userHolding: List<Holding> = emptyList()
)

@Serializable
data class Holding(
	@SerialName("symbol") val symbol: String,
	@SerialName("quantity") val quantity: Double? = null,
	@SerialName("ltp") val ltp: Double? = null,
	@SerialName("avgPrice") val avgPrice: Double? = null,
	@SerialName("close") val close: Double? = null
) 