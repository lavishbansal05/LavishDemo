package com.assignment.myportfolio

import android.app.Application
import app.cash.turbine.test
import com.assignment.myportfolio.core.NetworkMonitor
import com.assignment.myportfolio.core.PollingConfig
import com.assignment.myportfolio.domain.model.HoldingEntity
import com.assignment.myportfolio.domain.usecase.ComputePortfolioUseCase
import com.assignment.myportfolio.domain.usecase.GetHoldingsUseCase
import com.assignment.myportfolio.presentation.PortfolioViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PortfolioViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var app: Application
    private lateinit var getHoldings: GetHoldingsUseCase
    private lateinit var compute: ComputePortfolioUseCase
    private lateinit var network: NetworkMonitor

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        // default disable polling for tests unless a test enables it explicitly
        PortfolioViewModel.pollingEnabled = false
        PortfolioViewModel.maxPollIterations = Int.MAX_VALUE

        app = mockk(relaxed = true)
        getHoldings = mockk()
        compute = ComputePortfolioUseCase()
        network = mockk()
    }

    @After
    fun tearDown() {
        unmockkAll()
        clearAllMocks()
        PortfolioViewModel.pollingEnabled = true
        PortfolioViewModel.maxPollIterations = Int.MAX_VALUE
    }

    @Test
    fun `Given network online and repo success, When refresh is called, Then state goes loading then success`() =
        runTest {
            // Given
            every { network.isOnline } returns MutableStateFlow(true)
            coEvery { getHoldings.invoke(any()) } returns Result.success(
                listOf(HoldingEntity("A", 1.0, 10.0, 9.0, 11.0, false))
            )
            val vm = PortfolioViewModel(app, getHoldings, compute, network)

            // When + Then
            vm.uiState.test {
                awaitItem() // initial
                // init emits loading then success
                val loading1 = awaitItem()
                assertTrue(loading1.isLoading)
                val success1 = awaitItem()
                assertFalse(success1.isLoading)

                vm.refresh(true)
                val loading = awaitItem()
                assertTrue(loading.isLoading)
                val success = awaitItem()
                assertFalse(success.isLoading)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `Given polling enabled, When time advances by interval, Then holdings are refreshed by polling`() =
        runTest {
            // Given
            PortfolioViewModel.pollingEnabled = true
            PortfolioViewModel.maxPollIterations = 1
            every { network.isOnline } returns MutableStateFlow(true)
            val first = listOf(HoldingEntity("A", 1.0, 10.0, 9.0, 11.0, false))
            val second = listOf(HoldingEntity("B", 2.0, 20.0, 19.0, 21.0, false))
            coEvery { getHoldings.invoke(any()) } returnsMany listOf(
                Result.success(first), // init refresh success
                Result.success(second) // first polling tick success
            )
            val vm = PortfolioViewModel(app, getHoldings, compute, network)

            // When + Then
            vm.uiState.test {
                awaitItem() // initial
                // init emits loading then success
                awaitItem() // loading
                val afterInit = awaitItem() // success
                assertEquals(first, afterInit.holdingEntities)

                // Advance time to trigger polling tick
                advanceTimeBy(PollingConfig.HOLDINGS_POLL_INTERVAL_MS)
                advanceUntilIdle()

                val afterPoll = awaitItem()
                assertEquals(second, afterPoll.holdingEntities)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `Given online status flow changes, When observeConnectivity is active, Then isOnline updates in state`() =
        runTest {
            // Given
            val onlineFlow = MutableStateFlow(true)
            every { network.isOnline } returns onlineFlow
            coEvery { getHoldings.invoke(any()) } returns Result.success(emptyList())
            val vm = PortfolioViewModel(app, getHoldings, compute, network)

            // When + Then
            vm.uiState.test {
                awaitItem() // initial
                // init emits loading then success
                awaitItem() // loading
                awaitItem() // success

                // go offline
                onlineFlow.value = false
                var state = awaitItem()
                while (state.isOnline) {
                    state = awaitItem()
                }
                assertFalse(state.isOnline)

                // back online
                onlineFlow.value = true
                state = awaitItem()
                while (!state.isOnline) {
                    state = awaitItem()
                }
                assertTrue(state.isOnline)
                cancelAndConsumeRemainingEvents()
            }
        }
}
