package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.PlayerEntity
import org.springframework.data.repository.CrudRepository

interface MySqlPlayerDao : CrudRepository<PlayerEntity, String>