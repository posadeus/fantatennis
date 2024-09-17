package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.domain.infrastructure.PlayerPointsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.*
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.*
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MySqlPlayerPointsRepositoryTest {

  private val playersPointsDao: PlayersPointsDao = mockk()

  private val repository: PlayerPointsRepository = MySqlPlayerPointsRepository(playersPointsDao)

  @Nested
  inner class SavePlayerPointsTest {

    @Test
    fun `save players`() {

      val players = setOf(DomainPlayer(id = "AN_ID", tournamentPoints = mapOf(2222 to mapOf(1234 to 10.0))),
                          DomainPlayer(id = "ANOTHER_ID", tournamentPoints = mapOf(2222 to mapOf(1234 to 13.0))))

      val playersPointsKeyEmbedded1 = PlayersPointsKeyEmbedded(2222, 1234, "AN_ID")
      val playersPointsKeyEmbedded2 = PlayersPointsKeyEmbedded(2222, 1234, "ANOTHER_ID")
      val player1 = PlayersEntity(id = "AN_ID")
      val player2 = PlayersEntity(id = "ANOTHER_ID")
      val tournament = TournamentsEntity(1234)
      val entity1 = PlayersPointsEntity(playersPointsKeyEmbedded1, 10.0, player1, tournament)
      val entity2 = PlayersPointsEntity(playersPointsKeyEmbedded2, 13.0, player2, tournament)
      val playersPointsEntities = setOf(entity1, entity2)

      every { playersPointsDao.saveAll(playersPointsEntities) } returns playersPointsEntities

      repository.save(players)

      verify { playersPointsDao.saveAll(playersPointsEntities) }
    }
  }

  @Nested
  inner class RetrievePlayerPointsTest {

    @Test
    fun `retrieve players points`() {

      val tournamentByTeam = TournamentByTeam(teamId = 123,
                                              startingTournamentId = 1,
                                              endingTournamentId = 28,
                                              tournamentYear = 2020)
      val teamTournamentDto = TeamTournamentDto(teamId = 123,
                                                startingTournamentId = 1,
                                                endingTournamentId = 28,
                                                tournamentYear = 2020)
      val fantaTeamPlayersPointsDto = listOf(TeamPlayerPointsDto(playerId = "A_PLAYER_ID",
                                                                 playerName = "A_FULL_NAME_1",
                                                                 totalScore = 20.0),
                                             TeamPlayerPointsDto(playerId = "ANOTHER_PLAYER_ID",
                                                                 playerName = "A_FULL_NAME_2",
                                                                 totalScore = 17.0))

      val expected = TeamOrderedPlayerPoints(listOf(PlayerPoints(playerId = "A_PLAYER_ID",
                                                                 playerName = "A_FULL_NAME_1",
                                                                 totalPoints = 20.0),
                                                    PlayerPoints(playerId = "ANOTHER_PLAYER_ID",
                                                                 playerName = "A_FULL_NAME_2",
                                                                 totalPoints = 17.0)))

      every { playersPointsDao.findPlayersPointsByTeamTournamentDto(teamTournamentDto) } returns fantaTeamPlayersPointsDto

      assertThat(repository.retrieve(tournamentByTeam)).isEqualTo(expected)
    }

    @Test
    fun `retrieve empty list of players`() {

      val tournamentByTeam = TournamentByTeam(teamId = 123,
                                              startingTournamentId = 1,
                                              endingTournamentId = 28,
                                              tournamentYear = 2020)
      val teamTournamentDto = TeamTournamentDto(teamId = 123,
                                                startingTournamentId = 1,
                                                endingTournamentId = 28,
                                                tournamentYear = 2020)

      val expected = TeamOrderedPlayerPoints(emptyList())

      every { playersPointsDao.findPlayersPointsByTeamTournamentDto(teamTournamentDto) } returns emptyList()

      assertThat(repository.retrieve(tournamentByTeam)).isEqualTo(expected)
    }
  }
}