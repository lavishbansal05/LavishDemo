package com.assignment.myportfolio.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.assignment.myportfolio.R
import com.assignment.myportfolio.domain.model.HoldingEntity
import com.assignment.myportfolio.ui.theme.AppExtendedTheme
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun PortfolioScreen(viewModel: PortfolioViewModel, darkModeState: MutableState<Boolean>) {
    val state by viewModel.uiState.collectAsState()
    val pullState = rememberPullRefreshState(
        refreshing = state.isLoading,
        onRefresh = { viewModel.refresh(true) })

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.portfolio_title),
                    color = Color.White
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                actionIconContentColor = MaterialTheme.colorScheme.onPrimary
            ),
            actions = {
                IconButton(onClick = { darkModeState.value = !darkModeState.value }) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.btn_star_big_on),
                        contentDescription = stringResource(id = R.string.toggle_theme)
                    )
                }
            },
            modifier = Modifier.statusBarsPadding()
        )

        // Main content area
        Box(modifier = Modifier
            .fillMaxSize()
            .weight(1f)
            .pullRefresh(pullState)) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Connectivity banner
                ConnectivityBanner(isOnline = state.isOnline)

                // Holdings list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = if (state.expanded) 220.dp else 96.dp)
                ) {
                    items(state.holdingEntities) { h ->
                        HoldingRow(h)
                        HorizontalDivider()
                    }
                }
            }

            // Pull to refresh indicator
            PullRefreshIndicator(
                refreshing = state.isLoading,
                state = pullState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            )
        }

        // Bottom summary (sticky)
        BottomSummaryExpandable(state = state, onToggle = { viewModel.toggleExpanded() })
    }
}

@Composable
private fun ConnectivityBanner(isOnline: Boolean) {
    val show = remember { mutableStateOf(false) }
    LaunchedEffect(isOnline) {
        if (!isOnline) {
            show.value = true
        } else {
            show.value = true
            delay(2500)
            show.value = false
        }
    }
    AnimatedVisibility(
        visible = show.value,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { -it })
    ) {
        val ec = AppExtendedTheme.colors
        val bg = if (isOnline) ec.bannerOnlineBg else ec.bannerOfflineBg
        val dot = if (isOnline) ec.dotOnline else ec.dotOffline
        val text =
            if (isOnline) stringResource(id = R.string.connectivity_online) else stringResource(id = R.string.connectivity_offline)
        Row(
            Modifier
                .fillMaxWidth()
                .background(bg)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(dot))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun BottomSummaryExpandable(state: PortfolioUiState, onToggle: () -> Unit) {
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
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

@Composable
private fun HoldingRow(h: HoldingEntity) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = h.symbol,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(id = R.string.ltp_label) + " " + formatCurrency(h.ltp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = stringResource(id = R.string.net_qty_label) + " " + formatQuantity(h.quantity),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            val pnl = h.totalPnl
            val color =
                if (pnl >= 0) AppExtendedTheme.colors.positive else AppExtendedTheme.colors.negative
            Text(
                text = stringResource(id = R.string.pnl_label) + " " + formatCurrency(pnl),
                style = MaterialTheme.typography.bodySmall,
                color = color
            )
        }
    }
}

private fun formatCurrency(value: Double): String {
    return "₹ " + String.format("%,.2f", value)
}

private fun formatPercent(value: Double): String {
    return String.format("%.2f%%", value)
}

private fun formatQuantity(value: Double): String {
    return if (value % 1.0 == 0.0) value.toInt().toString() else String.format("%.2f", value)
}
