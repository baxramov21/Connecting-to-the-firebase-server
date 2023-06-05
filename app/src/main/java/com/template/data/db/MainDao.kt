package com.template.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.template.data.db.model.LinkModel
import com.template.data.db.model.LinkModel.Companion.TABLE_NAME

@Dao
interface MainDao {

    @Query("SELECT * FROM $TABLE_NAME LIMIT 1")
    suspend fun getLink(): LinkModel

    @Upsert
    suspend fun addLink(model: LinkModel)

    @Query("DELETE FROM $TABLE_NAME")
    suspend fun clearTable()
}