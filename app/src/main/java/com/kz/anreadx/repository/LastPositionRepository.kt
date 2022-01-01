package com.kz.anreadx.repository

import com.kz.anreadx.dispatcher.DB
import com.kz.anreadx.model.LastPosition
import com.kz.anreadx.persistence.LastPositionDao
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LastPositionRepository @Inject constructor(
    private val db: DB,
    private val dao: LastPositionDao
) {
    suspend fun insert(link: String, offset: Int) {
        withContext(db) {
            dao.insert(LastPosition(link, offset))
        }
    }

    suspend fun query(): LastPosition? {
        return withContext(db) {
            dao.query()
        }
    }
}
