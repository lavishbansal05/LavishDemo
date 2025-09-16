package com.assignment.myportfolio

import com.assignment.myportfolio.domain.model.HoldingEntity
import com.assignment.myportfolio.domain.usecase.ComputePortfolioUseCase
import org.junit.Assert.assertEquals
import org.junit.Test

class ComputePortfolioUseCaseTest {

    @Test
    fun `Given multiple holdings, When compute is invoked, Then totals are calculated correctly`() {
        val useCase = ComputePortfolioUseCase()
        val holdings = listOf(
            HoldingEntity(symbol = "A", quantity = 10.0, averagePrice = 100.0, close = 105.0, ltp = 110.0),
            HoldingEntity(symbol = "B", quantity = 5.0, averagePrice = 200.0, close = 190.0, ltp = 180.0)
        )
        val summary = useCase(holdings)
        val expectedCurrent = 10.0*110.0 + 5.0*180.0
        val expectedInvestment = 10.0*100.0 + 5.0*200.0
        val expectedToday = (105.0-110.0)*10.0 + (190.0-180.0)*5.0
        assertEquals(expectedCurrent, summary.currentValue, 0.0001)
        assertEquals(expectedInvestment, summary.totalInvestment, 0.0001)
        assertEquals(expectedCurrent-expectedInvestment, summary.totalPnl, 0.0001)
        assertEquals(expectedToday, summary.todaysPnl, 0.0001)
    }

    @Test
    fun `Given zero investment, When compute is invoked, Then percent stays zero`() {
        val useCase = ComputePortfolioUseCase()
        val holdings = listOf(
            HoldingEntity(symbol = "Z", quantity = 0.0, averagePrice = 0.0, close = 0.0, ltp = 0.0)
        )
        val summary = useCase(holdings)
        assertEquals(0.0, summary.currentValue, 0.0)
        assertEquals(0.0, summary.totalInvestment, 0.0)
        assertEquals(0.0, summary.totalPnl, 0.0)
        assertEquals(0.0, summary.todaysPnl, 0.0)
    }
}
