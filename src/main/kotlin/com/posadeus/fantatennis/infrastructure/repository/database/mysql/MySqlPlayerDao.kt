package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.PlayerEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MySqlPlayerDao : CrudRepository<PlayerEntity, String>