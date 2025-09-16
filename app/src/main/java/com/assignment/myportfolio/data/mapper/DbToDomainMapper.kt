package com.assignment.myportfolio.data.mapper

import com.assignment.myportfolio.data.local.HoldingDBEntity
import com.assignment.myportfolio.domain.model.HoldingEntity

fun HoldingDBEntity.toDomain(): HoldingEntity =
	HoldingEntity(
		symbol = symbol,
		quantity = quantity,
		averagePrice = avgPrice,
		close = close,
		ltp = ltp
	) 