package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.domain.infrastructure.FantaTeamsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.FantaTeamsDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.FantaTournamentsTeamsDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.*
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional

@Transactional
class MySqlFantaTeamsRepository(private val fantaTeamsDao: FantaTeamsDao,
                                private val fantaTournamentsTeamsDao: FantaTournamentsTeamsDao) : FantaTeamsRepository {

  override fun createTeam(ownerId: String, validFantaTournament: ValidFantaTournament): FantaTeam =
      try {

        ownerId
            .let(::toFantaTeamsEntity)
            .let(fantaTeamsDao::save)
            .also { fantaTeamsDao.flush() }
            .also {
              toFantaTournamentsTeamsEntity(it, validFantaTournament)
                  .let(fantaTournamentsTeamsDao::save)
            }
            .let(::toFantaTeamOk)
      }
      catch (e: Exception) {

        LOGGER.error("Error during the creation of the FantaTeam", e)
        FantaTeamError
      }

  private fun toFantaTournamentsTeamsEntity(fantaTeam: FantaTeamsEntity,
                                            fantaTournament: ValidFantaTournament): FantaTournamentsTeamsEntity =
      FantaTournamentsTeamsEntity(id = FantaTournamentsTeamsKeyEmbedded(tournamentId = fantaTournament.id,
                                                                        teamId = fantaTeam.teamId),
                                  tournament = toFantaTournamentEntity(fantaTournament),
                                  fantaTeam = fantaTeam)

  private fun toFantaTournamentEntity(fantaTournament: ValidFantaTournament): FantaTournamentsEntity =
      FantaTournamentsEntity(id = fantaTournament.id,
                             startingTournament = fantaTournament.startingTournamentId,
                             endingTournament = fantaTournament.endingTournamentId,
                             year = fantaTournament.tournamentYear)

  private fun toFantaTeamsEntity(ownerId: String) =
      FantaTeamsEntity(ownerId = ownerId)

  private fun toFantaTeamOk(entity: FantaTeamsEntity) =
      FantaTeamOk(id = entity.teamId, ownerId = entity.ownerId)

  companion object {

    private val LOGGER = LoggerFactory.getLogger(MySqlFantaTeamsRepository::class.java)
  }
}
