package com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao

import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.PlayerEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PlayersDao : CrudRepository<PlayerEntity, String>