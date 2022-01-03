package com.kz.anreadx.repository

import com.kz.anreadx.dispatcher.Background
import com.kz.anreadx.model.LastPosition
import com.kz.anreadx.persistence.LastPositionDao
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LastPositionRepository @Inject constructor(
    private val background: Background,
    private val dao: LastPositionDao
) {
    suspend fun insert(link: String, offset: Int) {
        withContext(background) {
            dao.insert(LastPosition(link, offset))
        }
    }

    suspend fun query(): LastPosition? {
        return withContext(background) {
            dao.query()
        }
    }
}
