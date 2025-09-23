package com.assignment.myportfolio.presentation.compose_ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.assignment.myportfolio.domain.model.HoldingEntity
import com.assignment.myportfolio.presentation.formatCurrency
import com.assignment.myportfolio.presentation.formatQuantity
import com.assignment.myportfolio.ui.theme.AppExtendedTheme
import com.assignment.myportfolio.ui.theme.AppTheme

@Composable
fun HoldingRow(h: HoldingEntity) {
	Column(
		Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 12.dp)
	) {
		Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
			Text(
				text = h.symbol,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.SemiBold,
				color = MaterialTheme.colorScheme.onSurface
			)
			Text(
				text = "LTP " + formatCurrency(h.ltp),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurface
			)
		}
		Spacer(Modifier.height(6.dp))
		Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
			Text(
				text = "Net Qty " + formatQuantity(h.quantity),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
			val pnl = h.totalPnl
			val color =
				if (pnl >= 0) AppExtendedTheme.colors.positive else AppExtendedTheme.colors.negative
			Text(
				text = "P&L " + formatCurrency(pnl),
				style = MaterialTheme.typography.bodySmall,
				color = color
			)
		}
	}
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun HoldingRowLightPreview() {
	AppTheme {
		HoldingRow(HoldingEntity("ABC", 12.0, 100.0, 98.0, 105.0))
	}
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HoldingRowDarkPreview() {
	AppTheme {
		HoldingRow(HoldingEntity("ABC", 12.0, 100.0, 98.0, 105.0))
	}
} 