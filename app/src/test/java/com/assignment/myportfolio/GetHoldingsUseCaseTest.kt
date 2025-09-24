package com.assignment.myportfolio

import com.assignment.myportfolio.domain.model.HoldingEntity
import com.assignment.myportfolio.domain.repository.PortfolioRepository
import com.assignment.myportfolio.domain.usecase.GetHoldingsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetHoldingsUseCaseTest {

    private lateinit var repository: PortfolioRepository
    private lateinit var getHoldingsUseCase: GetHoldingsUseCase

    @Before
    fun setup() {
        repository = mockk()
        getHoldingsUseCase = GetHoldingsUseCase(repository)
    }

    @Test
    fun `Given repository returns success, When invoke is called, Then returns success result`() = runTest {
        // Given
        val expectedHoldings = listOf(
            HoldingEntity("AAPL", 10.0, 150.0, 145.0, 155.0),
            HoldingEntity("GOOGL", 5.0, 2800.0, 2750.0, 2850.0)
        )
        coEvery { repository.getHoldings() } returns Result.success(expectedHoldings)

        // When
        val result = getHoldingsUseCase()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedHoldings, result.getOrNull())
    }

    @Test
    fun `Given repository returns failure, When invoke is called, Then returns failure result`() = runTest {
        // Given
        val exception = RuntimeException("Network error")
        coEvery { repository.getHoldings() } returns Result.failure(exception)

        // When
        val result = getHoldingsUseCase()

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `Given repository returns empty list, When invoke is called, Then returns success with empty list`() = runTest {
        // Given
        val emptyHoldings = emptyList<HoldingEntity>()
        coEvery { repository.getHoldings() } returns Result.success(emptyHoldings)

        // When
        val result = getHoldingsUseCase()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(emptyHoldings, result.getOrNull())
    }
}
