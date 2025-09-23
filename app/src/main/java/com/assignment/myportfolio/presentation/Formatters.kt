package com.assignment.myportfolio.presentation

fun formatCurrency(value: Double): String {
	return "₹ " + String.format("%,.2f", value)
}

fun formatPercent(value: Double): String {
	return String.format("%.2f%%", value)
}

fun formatQuantity(value: Double): String {
	return if (value % 1.0 == 0.0) value.toInt().toString() else String.format("%.2f", value)
} 