package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao

import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTournamentTeamDto

interface FantaTournamentTeamDao {

  fun persist(fantaTournamentTeam: JdbcFantaTournamentTeamDto)
}
