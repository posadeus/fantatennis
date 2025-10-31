package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao

import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerDto

interface PlayerDao {

  fun retrieveAll(): List<JdbcPlayerDto>
}
