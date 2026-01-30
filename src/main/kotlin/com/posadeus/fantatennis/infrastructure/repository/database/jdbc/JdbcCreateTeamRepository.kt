package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.app.configuration.infrastructure.OpenForSpring
import com.posadeus.fantatennis.domain.exception.FantaTeamCreationException
import com.posadeus.fantatennis.domain.infrastructure.CreateTeamRepository
import com.posadeus.fantatennis.domain.model.FantaTeam
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.*
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTournamentTeamDto
import org.springframework.transaction.annotation.Transactional

@OpenForSpring
class JdbcCreateTeamRepository(private val fantaTournamentDao: FantaTournamentDao,
                               private val fantaTeamDao: FantaTeamDao,
                               private val fantaTournamentTeamDao: FantaTournamentTeamDao) : CreateTeamRepository {

  @Transactional
  override fun create(ownerId: String, fantaTournamentId: Int): FantaTeam =
      try {

        fantaTournamentDao.retrieveBy(fantaTournamentId)
        val fantaTeamId = fantaTeamDao.persist(ownerId)
        fantaTournamentTeamDao.persist(JdbcFantaTournamentTeamDto(teamId = fantaTeamId, fantaTournamentId = fantaTournamentId))

        FantaTeam(id = fantaTeamId, ownerId = ownerId)
      }
      catch (e: Exception) {

        throw FantaTeamCreationException("Error during DB operation ${e.message}")
      }
}
