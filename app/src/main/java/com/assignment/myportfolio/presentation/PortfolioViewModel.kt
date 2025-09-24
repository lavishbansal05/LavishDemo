package com.assignment.myportfolio.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.assignment.myportfolio.core.NetworkMonitor
import com.assignment.myportfolio.core.PollingConfig
import com.assignment.myportfolio.domain.model.HoldingEntity
import com.assignment.myportfolio.domain.model.PortfolioSummary
import com.assignment.myportfolio.domain.usecase.ComputePortfolioUseCase
import com.assignment.myportfolio.domain.usecase.GetHoldingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PortfolioViewModel @Inject constructor(
	app: Application,
	private val getHoldingsUseCase: GetHoldingsUseCase,
	private val computePortfolioUseCase: ComputePortfolioUseCase,
	private val networkMonitor: NetworkMonitor,
) : AndroidViewModel(app) {

	private val _uiState = MutableStateFlow(
		PortfolioUiState(
			isLoading = false,
			error = null,
			holdingEntities = emptyList(),
			summary = null,
			expanded = false,
			isOnline = networkMonitor.isOnline.value
		)
	)
	val uiState: StateFlow<PortfolioUiState> = _uiState

	private var pollingJob: Job? = null

	init {
		refresh(insertDummy = false)
		if (pollingEnabled) {
			startPolling()
		}
		observeConnectivity()
	}

	fun refresh(insertDummy: Boolean = false) {
		viewModelScope.launch {
			_uiState.value = _uiState.value.copy(isLoading = true, error = null)
			val result = getHoldingsUseCase()
			result.onSuccess { list ->
				val mutableHoldingsList = list.toMutableList()
				if (insertDummy) {
					val dummy = HoldingEntity(
						symbol = "Upstox",
						quantity = 1.0,
						averagePrice = 100.0,
						close = 104.0,
						ltp = 105.0,
					)
					val insertIndex = if (mutableHoldingsList.size >= 2) 2 else mutableHoldingsList.size
					mutableHoldingsList.add(insertIndex, dummy)
				}
				val summary = computePortfolioUseCase(mutableHoldingsList)
				_uiState.value = _uiState.value.copy(
					isLoading = false,
					holdingEntities = mutableHoldingsList,
					summary = summary
				)
			}.onFailure { t ->
				_uiState.value = _uiState.value.copy(isLoading = false, error = t.message ?: "Unknown error")
			}
		}
	}

	private fun startPolling() {
		pollingJob?.cancel()
		pollingJob = viewModelScope.launch {
			var iterations = 0
			while (iterations < maxPollIterations) {
				delay(PollingConfig.HOLDINGS_POLL_INTERVAL_MS)
				val result = getHoldingsUseCase()
				result.onSuccess { list ->
					val summary = computePortfolioUseCase(list)
					_uiState.value = _uiState.value.copy(holdingEntities = list, summary = summary)
				}
				iterations++
			}
		}
	}

	private fun observeConnectivity() {
		viewModelScope.launch {
			networkMonitor.isOnline.collectLatest { online ->
				val previousOnline = _uiState.value.isOnline
				_uiState.value = _uiState.value.copy(isOnline = online)
				
				// Auto-refresh when internet is restored
				if (!previousOnline && online) {
					refresh(insertDummy = false)
				}
			}
		}
	}

	fun toggleExpanded() {
		_uiState.value = _uiState.value.copy(expanded = !_uiState.value.expanded)
	}

	companion object {
		@JvmStatic var pollingEnabled: Boolean = true
		@JvmStatic var maxPollIterations: Int = Int.MAX_VALUE
	}
}


data class PortfolioUiState(
	val isLoading: Boolean,
	val error: String?,
	val holdingEntities: List<HoldingEntity>,
	val summary: PortfolioSummary?,
	val expanded: Boolean,
	val isOnline: Boolean
)
