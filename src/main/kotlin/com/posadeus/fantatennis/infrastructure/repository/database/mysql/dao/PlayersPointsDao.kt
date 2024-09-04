package com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao

import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.PlayersPointsEntity
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.PlayersPointsKeyEmbedded
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PlayersPointsDao : CrudRepository<PlayersPointsEntity, PlayersPointsKeyEmbedded>
