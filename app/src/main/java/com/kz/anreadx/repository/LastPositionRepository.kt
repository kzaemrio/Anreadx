package com.kz.anreadx.repository

import com.kz.anreadx.model.LastPosition
import com.kz.anreadx.persistence.LastPositionDao
import javax.inject.Inject

class LastPositionRepository @Inject constructor(
    private val dao: LastPositionDao
) {
    suspend fun insert(link: String, offset: Int) {
        dao.insert(LastPosition(link, offset))
    }

    suspend fun query(): LastPosition? {
        return dao.query()
    }
}
