package com.assignment.myportfolio.presentation.compose_ui

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.assignment.myportfolio.R
import com.assignment.myportfolio.presentation.PortfolioUiState
import com.assignment.myportfolio.presentation.formatCurrency
import com.assignment.myportfolio.presentation.formatPercent
import com.assignment.myportfolio.ui.theme.AppExtendedTheme
import com.assignment.myportfolio.ui.theme.AppTheme

@Composable
fun BottomSummaryExpandable(state: PortfolioUiState, onToggle: () -> Unit) {
	Surface(tonalElevation = 3.dp) {
		Column(
			Modifier
				.fillMaxWidth()
				.clickable { onToggle() }
				.navigationBarsPadding()
				.animateContentSize()
				.padding(horizontal = 16.dp, vertical = 10.dp)
		) {
			if (state.expanded) {
				ExpandedSummaryPanel(state = state)
				Spacer(Modifier.height(4.dp))
			}
			Row(
				Modifier
					.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween
			) {
				Text(
					text = stringResource(id = R.string.profit_loss_label),
					style = MaterialTheme.typography.labelLarge,
					fontWeight = FontWeight.SemiBold,
					color = MaterialTheme.colorScheme.onSurface
				)
				val pnl = state.summary?.totalPnl ?: 0.0
				val pnlPercent = state.summary?.totalPnlPercent ?: 0.0
				val pnlColor =
					if (pnl >= 0) AppExtendedTheme.colors.positive else AppExtendedTheme.colors.negative
				Text(
					text = formatCurrency(pnl) + " (" + formatPercent(pnlPercent) + ")",
					style = MaterialTheme.typography.titleSmall,
					color = pnlColor
				)
			}
		}
	}
}

@Composable
private fun ExpandedSummaryPanel(state: PortfolioUiState) {
	Column(Modifier.fillMaxWidth()) {
		Text(
			text = stringResource(id = R.string.portfolio_summary_title),
			style = MaterialTheme.typography.titleMedium,
			fontWeight = FontWeight.SemiBold,
			color = MaterialTheme.colorScheme.onSurface
		)
		Spacer(Modifier.height(8.dp))
		LabeledValue(
			stringResource(id = R.string.current_value_label),
			formatCurrency(state.summary?.currentValue ?: 0.0)
		)
		LabeledValue(
			stringResource(id = R.string.total_investment_label),
			formatCurrency(state.summary?.totalInvestment ?: 0.0)
		)
		LabeledValue(
			stringResource(id = R.string.todays_pnl_label),
			formatCurrency(state.summary?.todaysPnl ?: 0.0)
		)
	}
}

@Composable
private fun LabeledValue(label: String, value: String) {
	Row(
		Modifier
			.fillMaxWidth()
			.padding(vertical = 6.dp),
		horizontalArrangement = Arrangement.SpaceBetween
	) {
		Text(
			text = label,
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant
		)
		Text(
			text = value,
			style = MaterialTheme.typography.bodyMedium,
			fontWeight = FontWeight.Medium,
			color = MaterialTheme.colorScheme.onSurface
		)
	}
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun BottomSummaryLightPreview() {
	AppTheme {
		BottomSummaryExpandable(
			state = PortfolioUiState(false, null, emptyList(), null, expanded = false, isOnline = true),
			onToggle = {}
		)
	}
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun BottomSummaryDarkPreview() {
	AppTheme {
		BottomSummaryExpandable(
			state = PortfolioUiState(false, null, emptyList(), null, expanded = true, isOnline = true),
			onToggle = {}
		)
	}
} 