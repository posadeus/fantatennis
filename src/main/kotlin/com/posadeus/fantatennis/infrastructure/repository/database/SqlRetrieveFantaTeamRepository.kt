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

  override fun retrieve(teamId: Int): Team {
    TODO("Not yet implemented")
  }

  override fun retrieveByTeamId(id: TeamId): DomainTeam =
      try {

        val fantaTeam = fantaTeamDao.retrieveBy(id)
        val teams = teamDao.retrieveBy(setOf(id))

        if (teams.isEmpty()) {

          NotFoundDomainTeam(id)
        }
        else {

          val fantaTournamentTeam = fantaTournamentTeamDao.retrieveByTeamId(id)

          toFoundTeam(fantaTeam, fantaTournamentTeam, teams)
        }
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
      (teams.map {
        toFoundTeam(fantaTeams[it.key]!!, fantaTournamentTeams[it.key]!!, it.value)
      } to toNotFoundTeams(teams.keys, fantaTournamentTeams.keys))
          .let { (foundTeams, notFoundTeams) ->
            (foundTeams + notFoundTeams)
                .let(::Teams)
          }

  private fun toNotFoundTeams(teamIds: Set<Int>, fantaTournamentTeamIds: Set<Int>): List<NotFoundDomainTeam> =
      xor(teamIds, fantaTournamentTeamIds).map { NotFoundDomainTeam(it) }

  private fun xor(teamIds: Set<Int>, fantaTournamentTeamIds: Set<Int>): Set<Int> =
      ((teamIds - fantaTournamentTeamIds) + (fantaTournamentTeamIds - teamIds))

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
