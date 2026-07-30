package com.posadeus.fantatennis.infrastructure.repository.database

import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTeamRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.DomainTeam.FoundDomainTeam
import com.posadeus.fantatennis.domain.model.DomainTeam.NotFoundDomainTeam
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.*
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.*
import org.springframework.dao.EmptyResultDataAccessException

class SqlRetrieveFantaTeamRepository(private val teamDao: TeamDao,
                                     private val fantaTeamDao: FantaTeamDao,
                                     private val fantaTournamentTeamDao: FantaTournamentTeamDao) : RetrieveFantaTeamRepository {

  override fun retrieveByTeamId(id: TeamId): DomainTeam =
      try {

        val fantaTeam = fantaTeamDao.retrieveBy(id)
        val fantaTournamentTeam = fantaTournamentTeamDao.retrieveByTeamId(id)
        val teams = teamDao.retrieveBy(setOf(id))

        toFoundTeam(fantaTeam, fantaTournamentTeam, teams)
      }
      catch (_: EmptyResultDataAccessException) {

        NotFoundDomainTeam(id)
      }

  override fun retrieveByFantaTournamentId(id: TournamentId): Teams {

    val fantaTournamentTeams =
        fantaTournamentTeamDao.retrieveByFantaTournamentId(id)
            .associateBy { it.teamId }
            .takeIf { it.isNotEmpty() }
        ?: return Teams(emptyList())

      val fantaTeams =
          try {

            fantaTournamentTeams.keys.associateWith { fantaTeamDao.retrieveBy(it) }
          }
          catch (_: EmptyResultDataAccessException) {

            return Teams(emptyList())
          }

      return if (fantaTournamentTeams.keys.size != fantaTeams.keys.size) {

        Teams(emptyList())
      }
      else {

        fantaTournamentTeams
            .let { teamDao.retrieveBy(it.keys) }
            .groupBy { it.teamId }
            .let { teamsDto -> toTeams(teamsDto, fantaTeams, fantaTournamentTeams) }
      }
  }

  private fun toTeams(teams: Map<Int, List<JdbcTeamDto>>,
                      fantaTeams: Map<Int, JdbcFantaTeamDto>,
                      fantaTournamentTeams: Map<Int, JdbcFantaTournamentTeamDto>): Teams =
      fantaTournamentTeams.keys
          .map { teamId -> toFoundTeam(fantaTeams[teamId]!!, fantaTournamentTeams[teamId]!!, teams[teamId].orEmpty()) }
          .let(::Teams)

  private fun toFoundTeam(fantaTeam: JdbcFantaTeamDto,
                          fantaTournamentTeam: JdbcFantaTournamentTeamDto,
                          teams: List<JdbcTeamDto>): FoundDomainTeam =
      FoundDomainTeam(teamId = fantaTeam.teamId,
                      ownerId = fantaTeam.ownerId,
                      fantaTournamentId = fantaTournamentTeam.fantaTournamentId,
                      players = teams
                          .groupBy { it.playerId }
                          .mapValues { (_, rows) ->
                            rows.map { TournamentRange(start = it.startingTournamentId, end = it.endingTournamentId) }.toSet()
                          })
}
