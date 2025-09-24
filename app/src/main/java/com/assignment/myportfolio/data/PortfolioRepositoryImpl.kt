package com.assignment.myportfolio.data

import android.util.Log
import com.assignment.myportfolio.data.local.HoldingsDao
import com.assignment.myportfolio.data.remote.PortfolioService
import com.assignment.myportfolio.domain.model.HoldingEntity
import com.assignment.myportfolio.domain.repository.PortfolioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import com.assignment.myportfolio.data.mapper.toDomain as dbToDomain
import com.assignment.myportfolio.data.mapper.toDbEntity as dtoToDb

class PortfolioRepositoryImpl @Inject constructor(
	private val portfolioService: PortfolioService,
	private val dao: HoldingsDao,
	private val json: Json
) : PortfolioRepository {

    private val defaultUserId = "localUser"

    override suspend fun getHoldings(): Result<List<HoldingEntity>> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                fetchAndCache()
                val cached = dao.getHoldings(defaultUserId).map { it.dbToDomain() }
                Log.d(TAG, "Serving ${cached.size} holdings from DB cache for user=$defaultUserId")
                Result.success(cached)
            } catch (t: Throwable) {
                val cached = dao.getHoldings(defaultUserId).map { it.dbToDomain() }
                return@withContext if (cached.isNotEmpty()) {
                    Log.w(TAG, "Network failed: ${t.message}. Falling back to DB cache (${cached.size} items)")
                    Result.success(cached)
                } else {
                    Result.failure(t)
                }
            }
        }

    suspend fun fetchAndCache() {
        Log.d(TAG, "Fetching portfolio from network…")
        val response = portfolioService.getPortfolio()
        Log.d(TAG, "API response:\n${json.encodeToString(response)}")
        val now = System.currentTimeMillis()
        val entities = response.data.userHolding.mapNotNull { dto ->
            dto.dtoToDb(defaultUserId, now)
        }
        dao.clearForUser(defaultUserId)
        dao.upsertAll(entities)
        Log.d(TAG, "Cached ${entities.size} holdings in DB for user=$defaultUserId")
    }

    private companion object {
        private const val TAG = "PortfolioRepository"
    }
}
