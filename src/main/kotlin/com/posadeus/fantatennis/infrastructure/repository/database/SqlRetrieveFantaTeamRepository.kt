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

          toTeam(fantaTeam, fantaTournamentTeam, teams)
        }
      }
      catch (_: EmptyResultDataAccessException) {

        NotFoundDomainTeam(id)
      }

  override fun retrieveByFantaTournamentId(id: TournamentId): Teams {

    return try {

      val fantaTournamentTeams = fantaTournamentTeamDao.retrieveByFantaTournamentId(id).associateBy { it.teamId }

      if (fantaTournamentTeams.isEmpty())
        return Teams(emptyList())

      val fantaTeams = fantaTournamentTeams.keys.associateWith { fantaTeamDao.retrieveBy(it) }

      if (fantaTournamentTeams.keys.size != fantaTeams.keys.size)
        return Teams(emptyList())

      val teams = fantaTournamentTeams.let { teamDao.retrieveBy(it.keys) }.groupBy { it.teamId }

      val foundTeams = teams.map { toTeam(fantaTeams[it.key]!!, fantaTournamentTeams[it.key]!!, it.value) }

      val xor = (teams.keys - fantaTournamentTeams.keys) + (fantaTournamentTeams.keys - teams.keys)

      val notFoundTeams = xor.map { NotFoundDomainTeam(it) }

      return (foundTeams + notFoundTeams).let(::Teams)
    }
    catch (_: EmptyResultDataAccessException) {

      Teams(emptyList())
    }
  }

  private fun toTeam(fantaTeam: JdbcFantaTeamDto,
                     fantaTournamentTeam: JdbcFantaTournamentTeamDto,
                     teams: List<JdbcTeamDto>): FoundDomainTeam =
      FoundDomainTeam(teamId = fantaTeam.teamId,
                      ownerId = fantaTeam.ownerId,
                      fantaTournamentId = fantaTournamentTeam.fantaTournamentId,
                      players = teams.associate {
                        it.playerId to TournamentRange(start = it.startingTournamentId, end = it.endingTournamentId)
                      })
}
