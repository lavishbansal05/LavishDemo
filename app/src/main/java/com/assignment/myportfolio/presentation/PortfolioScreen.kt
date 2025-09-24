package com.assignment.myportfolio.presentation

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
import com.assignment.myportfolio.presentation.compose_ui.ConnectivityBanner
import com.assignment.myportfolio.presentation.compose_ui.HoldingRow
import com.assignment.myportfolio.presentation.compose_ui.BottomSummaryExpandable
import com.assignment.myportfolio.presentation.compose_ui.ErrorState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun PortfolioScreen(viewModel: PortfolioViewModel, darkModeState: MutableState<Boolean>) {
    val state by viewModel.uiState.collectAsState()

    val pullState = rememberPullRefreshState(
        refreshing = state.isLoading,
        onRefresh = { viewModel.refresh(insertDummy = true) })

    Column(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {
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

        ConnectivityBanner(isOnline = state.isOnline)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .pullRefresh(pullState)
        ) {
            if (state.error?.isNotEmpty() == true && !state.isLoading) {
                ErrorState(
                    isOnline = state.isOnline,
                    onRetry = { viewModel.refresh(insertDummy = true) },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .animateContentSize(),
                    contentPadding = PaddingValues(bottom = if (state.expanded) 220.dp else 96.dp)
                ) {
                    items(
                        items = state.holdingEntities,
                        key = { it.symbol }
                    ) { h ->
                        HoldingRow(h)
                        HorizontalDivider()
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = state.isLoading,
                state = pullState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            )
        }

        if (state.holdingEntities.isNotEmpty()) {
            BottomSummaryExpandable(
                state = state, 
                onToggle = { viewModel.toggleExpanded() }
            )
        }
    }
}
