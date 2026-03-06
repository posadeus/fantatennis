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

  override fun retrieveByTeamId(teamId: Int): DomainTeam =
      try {

        val fantaTeam = fantaTeamDao.retrieveBy(teamId)
        val teams = teamDao.retrieveBy(setOf(teamId))

        if (teams.isEmpty()) {

          NotFoundDomainTeam
        }
        else {

          val fantaTournamentTeam = fantaTournamentTeamDao.retrieveByTeamId(teamId)

          toTeam(fantaTeam, fantaTournamentTeam, teams)
        }
      }
      catch (_: EmptyResultDataAccessException) {

        NotFoundDomainTeam
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
