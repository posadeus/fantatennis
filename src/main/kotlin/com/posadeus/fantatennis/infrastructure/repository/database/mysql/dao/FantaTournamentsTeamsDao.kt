package com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao

import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.FantaTournamentsTeamsEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface FantaTournamentsTeamsDao : CrudRepository<FantaTournamentsTeamsEntity, Long> {

  @Query("""
      SELECT new com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.FantaTournamentsTeamsDto(ftt.fantaTeam.teamId, ft.startingTournament, ft.endingTournament, ft.year)
      FROM FantaTournamentsTeamsEntity ftt
      JOIN FantaTournamentsEntity ft ON ftt.tournament.id = ft.id
      WHERE ftt.fantaTeam.teamId = :teamId
         """)
  fun findTournamentByTeamId(@Param("teamId") teamId: Int): Optional<FantaTournamentsTeamsDto>
}

data class FantaTournamentsTeamsDto(val teamId: Int,
                                    val startingTournamentId: Int,
                                    val endingTournamentId: Int,
                                    val tournamentYear: Int)