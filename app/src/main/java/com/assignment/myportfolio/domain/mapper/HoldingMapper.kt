package com.assignment.myportfolio.domain.mapper

import com.assignment.myportfolio.data.remote.dto.Holding
import com.assignment.myportfolio.domain.model.HoldingEntity

fun Holding.toDomain(): HoldingEntity? {
	val q = quantity ?: return null
	val ap = avgPrice ?: return null
	val c = close ?: 0.0
	val l = ltp ?: return null
	return HoldingEntity(
		symbol = symbol,
		quantity = q,
		averagePrice = ap,
		close = c,
		ltp = l
	)
} 