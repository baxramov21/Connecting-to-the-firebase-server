package com.template.data.converter

import com.template.data.db.model.LinkModel
import com.template.domain.Link

class Mapper {
    fun mapModelToEntity(model: LinkModel): Link? {
        return Link(
            model.link
        )
    }

    fun mapEntityToModel(entity: Link): LinkModel {
        return LinkModel(
            link = entity.link
        )
    }
}