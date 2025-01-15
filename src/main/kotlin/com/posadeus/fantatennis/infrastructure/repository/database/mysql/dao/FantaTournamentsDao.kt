package com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao

import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.FantaTournamentsEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface FantaTournamentsDao : JpaRepository<FantaTournamentsEntity, Int> {

  @Query(value = """
        SELECT 
          ft.FANTA_TOURNAMENT_ID as tournamentId, 
          ft2.TEAM_ID as teamId, 
          ft2.OWNER_ID as ownerId,
          p.PLAYER_ID as playerId, 
          p.FULL_NAME as playerFullName, 
          pps.playerTotalScore
        FROM 
          FANTA_TOURNAMENTS ft,
          FANTA_TOURNAMENTS_TEAMS ftt, 
          FANTA_TEAMS ft2,
          TEAMS t,
          PLAYERS p,
          (	
            SELECT 
              pp.PLAYER_ID AS playerId, 
              SUM(pp.FANTA_POINTS) AS playerTotalScore
            FROM 
              PLAYERS_POINTS pp, 
              (
                SELECT 
                  ft3.FANTA_TOURNAMENT_ID AS fantaTournamentId,
                  ft3.STARTING_TOURNAMENT AS fantaTournamentStartingTournament,
                  ft3.ENDING_TOURNAMENT AS fantaTournamentEndingTournament,
                  ft3.TOURNAMENT_YEAR AS fantaTournamentYear
                FROM FANTA_TOURNAMENTS ft3 
                WHERE ft3.FANTA_TOURNAMENT_ID = :tournamentId
              ) fttt
            WHERE 
              pp.TOURNAMENT_YEAR = fttt.fantaTournamentYear
              AND pp.TOURNAMENT_ID BETWEEN fttt.fantaTournamentStartingTournament AND fttt.fantaTournamentEndingTournament
            GROUP BY pp.PLAYER_ID 
            ORDER BY playerTotalScore DESC
          ) pps 
        WHERE 
          ft.FANTA_TOURNAMENT_ID = ftt.FANTA_TOURNAMENT_ID
          AND ftt.TEAM_ID = ft2.TEAM_ID
          AND ft2.TEAM_ID = t.TEAM_ID
          AND t.PLAYER_ID = p.PLAYER_ID
          AND pps.playerId = p.PLAYER_ID
          AND ft.FANTA_TOURNAMENT_ID = :tournamentId
        ORDER BY t.TEAM_ID, pps.playerTotalScore DESC
        """,
         nativeQuery = true
  )
  fun findTournamentResultsByTournamentId(@Param("tournamentId") tournamentId: Int): List<TournamentResultsDto>
}

interface TournamentResultsDto {

  fun getTournamentId(): Int
  fun getTeamId(): Int
  fun getOwnerId(): String
  fun getPlayerId(): String
  fun getPlayerFullName(): String
  fun getPlayerTotalScore(): Double
}