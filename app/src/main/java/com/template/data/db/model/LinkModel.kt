package com.template.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.template.data.db.model.LinkModel.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class LinkModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val link: String
) {
    companion object {
        const val TABLE_NAME = "links_table"
    }
}