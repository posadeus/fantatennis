package com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao

import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.FantaTournamentsTeamsDto
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.FantaTournamentsTeamsEntity
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.FantaTournamentsTeamsKeyEmbedded
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

// TODO: Remove from the codebase once the jpa is removed in favor of jdbc
@Repository
interface FantaTournamentsTeamsDao : CrudRepository<FantaTournamentsTeamsEntity, FantaTournamentsTeamsKeyEmbedded> {

  @Query("""
      SELECT new com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.FantaTournamentsTeamsDto(
        ftt.fantaTeam.teamId, 
        ftt.tournament.startingTournament, 
        ftt.tournament.endingTournament, 
        ftt.tournament.year
        )
      FROM FantaTournamentsTeamsEntity ftt
      WHERE ftt.fantaTeam.teamId = :teamId
         """)
  fun findTournamentByTeamId(@Param("teamId") teamId: Int): Optional<FantaTournamentsTeamsDto>
}

