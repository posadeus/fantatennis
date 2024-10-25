package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.controller.model.team.TournamentCreationDto
import com.posadeus.fantatennis.domain.infrastructure.FantaTeamsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.FantaTeamsDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.FantaTeamsEntity
import org.slf4j.LoggerFactory

class MySqlFantaTeamsRepository(private val fantaTeamsDao: FantaTeamsDao) : FantaTeamsRepository {

  override fun createTeam(ownerId: String, tournamentId: Int?): FantaTeam =
      try {
        ownerId
            .let(::toFantaTeamsEntity)
            .let { fantaTeamsDao.save(it) }
            .let(::toFantaTeamOk)
      }
      catch (e: Exception) {

        LOGGER.error("Error during the creation of the FantaTeam", e)
        FantaTeamError
      }

  override fun createTeamAndTournament(ownerId: String, tournamentCreationDto: TournamentCreationDto): FantaTeam {
    TODO("Not yet implemented")
  }

  private fun toFantaTeamsEntity(ownerId: String) =
      FantaTeamsEntity(ownerId = ownerId)

  private fun toFantaTeamOk(it: FantaTeamsEntity) =
      FantaTeamOk(id = it.teamId, ownerId = it.ownerId)

  companion object {

    private val LOGGER = LoggerFactory.getLogger(MySqlFantaTeamsRepository::class.java)
  }
}
