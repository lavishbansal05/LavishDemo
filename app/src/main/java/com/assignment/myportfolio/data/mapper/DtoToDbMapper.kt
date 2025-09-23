package com.assignment.myportfolio.data.mapper

import com.assignment.myportfolio.data.local.HoldingDBEntity
import com.assignment.myportfolio.data.remote.dto.Holding

fun Holding.toDbEntity(userId: String, updatedAt: Long): HoldingDBEntity? {
	val q = quantity ?: return null
	val ap = avgPrice ?: return null
	val c = close ?: 0.0
	val l = ltp ?: return null
	return HoldingDBEntity(
		userId = userId,
		symbol = symbol,
		quantity = q,
		ltp = l,
		avgPrice = ap,
		close = c,
		updatedAt = updatedAt
	)
}
