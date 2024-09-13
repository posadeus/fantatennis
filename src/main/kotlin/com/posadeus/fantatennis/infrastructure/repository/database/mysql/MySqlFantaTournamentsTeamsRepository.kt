package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.domain.infrastructure.FantaTournamentsTeamsRepository
import com.posadeus.fantatennis.domain.model.TournamentByTeam
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.FantaTournamentsTeamsDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.FantaTournamentsTeamsDto

class MySqlFantaTournamentsTeamsRepository(private val fantaTournamentsTeamsDao: FantaTournamentsTeamsDao)
  : FantaTournamentsTeamsRepository {

  override fun retrieveTournamentByTeamId(teamId: Int): TournamentByTeam =
      convert(fantaTournamentsTeamsDao.findTournamentByTeamId(teamId).get())

  private fun convert(dto: FantaTournamentsTeamsDto): TournamentByTeam =
      TournamentByTeam(teamId = dto.teamId,
                       startingTournamentId = dto.startingTournamentId,
                       endingTournamentId = dto.endingTournamentId,
                       tournamentYear = dto.tournamentYear)
}
