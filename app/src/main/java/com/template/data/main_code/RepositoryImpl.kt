package com.template.data.main_code

import android.app.Application
import com.template.data.converter.Mapper
import com.template.data.db.MainDatabase
import com.template.domain.Link
import com.template.domain.Repository

class RepositoryImpl(application: Application) : Repository {

    private val database = MainDatabase.newInstance(application).getDao()
    private val converter = Mapper()

    override suspend fun getLink(): Link {
        return converter.mapModelToEntity(database.getLink())
    }

    override suspend fun openLink(link: Link) {
        TODO("Not yet implemented")
    }

    override suspend fun saveLink(link: Link) {
        database.addLink(
            converter.mapEntityToModel(link)
        )
    }

    override suspend fun clearDatabase() {
        database.clearTable()
    }

    override fun createLink(): Link {
        TODO("Not yet implemented")
    }
}