package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.controller.model.team.TournamentCreationDto
import com.posadeus.fantatennis.domain.infrastructure.FantaTeamsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.*
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.*
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional

@Transactional
class MySqlFantaTeamsRepository(private val fantaTeamsDao: FantaTeamsDao,
                                private val fantaTournamentsTeamsDao: FantaTournamentsTeamsDao,
                                private val fantaTournamentsDao: FantaTournamentsDao) : FantaTeamsRepository {

  override fun createTeam(ownerId: String, tournamentId: Int): FantaTeam =
      try {
        ownerId
            .let(::toFantaTeamsEntity)
            .let(fantaTeamsDao::save)
            .also {
              FantaTournamentsTeamsKeyEmbedded(tournamentId = tournamentId, teamId = it.teamId)
                  .let(::FantaTournamentsTeamsEntity)
                  .let(fantaTournamentsTeamsDao::save)
            }
            .let(::toFantaTeamOk)
      }
      catch (e: Exception) {

        LOGGER.error("Error during the creation of the FantaTeam", e)
        FantaTeamError
      }

  override fun createTeamAndTournament(ownerId: String, tournamentCreationDto: TournamentCreationDto): FantaTeam =
      try {
        tournamentCreationDto
            .let(::toFantaTournamentEntity)
            .let(fantaTournamentsDao::save)
            .let { createTeam(ownerId, it.id!!) }
      }
      catch (e: Exception) {

        LOGGER.error("Error during the creation of the FantaTournament", e)
        FantaTeamError
      }

  private fun toFantaTournamentEntity(dto: TournamentCreationDto): FantaTournamentsEntity =
      FantaTournamentsEntity(startingTournament = dto.startingTournamentId!!,
                             endingTournament = dto.endingTournamentId!!,
                             year = dto.tournamentYear!!)

  private fun toFantaTeamsEntity(ownerId: String) =
      FantaTeamsEntity(ownerId = ownerId)

  private fun toFantaTeamOk(entity: FantaTeamsEntity) =
      FantaTeamOk(id = entity.teamId, ownerId = entity.ownerId)

  companion object {

    private val LOGGER = LoggerFactory.getLogger(MySqlFantaTeamsRepository::class.java)
  }
}
