package com.assignment.myportfolio

import android.util.Log
import com.assignment.myportfolio.data.PortfolioRepositoryImpl
import com.assignment.myportfolio.data.local.HoldingDBEntity
import com.assignment.myportfolio.data.local.HoldingsDao
import com.assignment.myportfolio.data.local.PortfolioDatabase
import com.assignment.myportfolio.data.remote.PortfolioService
import com.assignment.myportfolio.data.remote.dto.PortfolioData
import com.assignment.myportfolio.data.remote.dto.PortfolioResponse
import com.assignment.myportfolio.data.remote.dto.Holding
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import io.mockk.impl.annotations.MockK

class PortfolioRepositoryImplTest {

	@get:Rule
	val mainDispatcherRule = MainDispatcherRule()

	@MockK
	lateinit var service: PortfolioService
	@MockK
	lateinit var db: PortfolioDatabase
	@MockK(relaxed = true)
	lateinit var dao: HoldingsDao
	private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

	@Before
	fun setup() {
		MockKAnnotations.init(this)
	}

	@After
	fun tearDown() {
		unmockkAll()
		clearAllMocks()
	}

	@Test
	fun `Given empty cache and network success, When getHoldings(), Then data is cached and returned`() =
		runTest {
			// Given
			mockkStatic(Log::class)
			every { Log.d(any<String>(), any<String>()) } returns 0
			every { Log.w(any<String>(), any<String>()) } returns 0
			every { Log.w(any<String>(), any<Throwable>()) } returns 0

			every { db.holdingsDao() } returns dao
			coEvery { dao.clearForUser(any()) } returns Unit
			coEvery { dao.upsertAll(any()) } returns Unit
			coEvery { dao.getHoldings(any()) } returns listOf(
                HoldingDBEntity(
                    0,
                    "localUser",
                    "A",
                    1.0,
                    10.0,
                    9.0,
                    11.0,
                    System.currentTimeMillis()
                )
            )
			coEvery { service.getPortfolio() } returns PortfolioResponse(
				data = PortfolioData(
					userHolding = listOf(
						Holding(
							symbol = "A",
							quantity = 1.0,
							ltp = 10.0,
							avgPrice = 9.0,
							close = 11.0
						)
					)
				)
			)

			// When
			val repo = PortfolioRepositoryImpl(service, dao, json)
			val result = repo.getHoldings()

			// Then
			val list = result.getOrThrow()
			assertEquals(1, list.size)
		}
}
