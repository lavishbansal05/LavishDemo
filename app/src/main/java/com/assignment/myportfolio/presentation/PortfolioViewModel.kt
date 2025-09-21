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
	private val getHoldings: GetHoldingsUseCase,
	private val compute: ComputePortfolioUseCase,
	private val networkMonitor: NetworkMonitor,
) : AndroidViewModel(app) {

	private val _uiState = MutableStateFlow(PortfolioUiState())
	val uiState: StateFlow<PortfolioUiState> = _uiState

	private var pollingJob: Job? = null

	init {
		refresh(true)
		if (pollingEnabled) {
			startPolling()
		}
		observeConnectivity()
	}

	fun refresh(force: Boolean, insertDummy: Boolean = false) {
		viewModelScope.launch {
			_uiState.value = _uiState.value.copy(isLoading = true, error = null)
			val result = getHoldings(force)
			result.onSuccess { list ->
				val mutable = list.toMutableList()
				if (insertDummy) {
					val dummy = HoldingEntity(
						symbol = "TEST_INSERT",
						quantity = 1.0,
						averagePrice = 100.0,
						close = 104.0,
						ltp = 105.0,
					)
					val insertIndex = if (mutable.size >= 2) 2 else mutable.size
					mutable.add(insertIndex, dummy)
				}
				val summary = compute(mutable)
				_uiState.value = _uiState.value.copy(
					isLoading = false,
					holdingEntities = mutable,
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
				val result = getHoldings(true)
				result.onSuccess { list ->
					val summary = compute(list)
					_uiState.value = _uiState.value.copy(holdingEntities = list, summary = summary)
				}
				iterations++
			}
		}
	}

	private fun observeConnectivity() {
		viewModelScope.launch {
			networkMonitor.isOnline.collectLatest { online ->
				_uiState.value = _uiState.value.copy(isOnline = online)
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
	val isLoading: Boolean = false,
	val error: String? = null,
	val holdingEntities: List<HoldingEntity> = emptyList(),
	val summary: PortfolioSummary? = null,
	val expanded: Boolean = false,
	val isOnline: Boolean = true
) 