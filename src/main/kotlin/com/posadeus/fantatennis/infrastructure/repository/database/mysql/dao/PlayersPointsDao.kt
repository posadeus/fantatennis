package com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao

import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.PlayersPointsEntity
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.PlayersPointsKeyEmbedded
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface PlayersPointsDao : CrudRepository<PlayersPointsEntity, PlayersPointsKeyEmbedded> {

  @Query("""
      SELECT new com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.TeamPlayerPointsDto(pp.id.playerId, SUM(pp.fantaPoints))
      FROM PlayersPointsEntity pp 
      WHERE pp.id.tournamentYear = :#{#filter.tournamentYear}
      AND pp.id.tournamentId BETWEEN :#{#filter.startingTournamentId} AND :#{#filter.endingTournamentId}
      AND pp.id.playerId IN (
        SELECT t.id.playerId 
        FROM TeamsEntity t 
        WHERE t.id.teamId = :#{#filter.teamId}
      )
      GROUP BY pp.id.playerId
      ORDER BY SUM(pp.fantaPoints) DESC
         """)
  fun findPlayersPointsByTeamTournamentDto(@Param("filter") teamTournamentDto: TeamTournamentDto): List<TeamPlayerPointsDto>
}

data class TeamTournamentDto(val teamId: Int,
                             val startingTournamentId: Int,
                             val endingTournamentId: Int,
                             val tournamentYear: Int)

data class TeamPlayerPointsDto(val playerId: String,
                               val totalScore: Double = 0.00)
