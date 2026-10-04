package com.example.premiumapp.data.repository

import com.example.premiumapp.data.local.dao.RecentSearchDao
import com.example.premiumapp.data.local.entities.RecentSearchEntity
import com.example.premiumapp.domain.repository.RecentSearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecentSearchRepositoryImpl(
    private val recentSearchDao: RecentSearchDao
) : RecentSearchRepository {

    override fun getRecentSearches(): Flow<List<String>> {
        return recentSearchDao.getRecentSearches().map { list ->
            list.map { it.query }
        }
    }

    override suspend fun addSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.isNotEmpty()) {
            recentSearchDao.deleteByQuery(trimmed)
            recentSearchDao.insert(
                RecentSearchEntity(
                    query = trimmed,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    override suspend fun removeSearch(query: String) {
        recentSearchDao.deleteByQuery(query)
    }

    override suspend fun clearSearches() {
        recentSearchDao.clearAll()
    }
}
