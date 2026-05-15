package com.posadeus.fantatennis.infrastructure.repository.database

import com.posadeus.fantatennis.domain.infrastructure.RetrievePlayersPointsRepository
import com.posadeus.fantatennis.domain.model.PlayersPoints.FoundPlayersPoints
import com.posadeus.fantatennis.domain.model.PlayersPoints.FoundPlayersPoints.PlayerPoints
import com.posadeus.fantatennis.domain.model.PlayersPoints.InternalErrorPlayersPoints
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerPointsDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcPlayerDto.aJdbcPlayerDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcPlayerPointsDto.aJdbcPlayerPointsDto
import io.mockk.*
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SqlRetrievePlayersPointsRepositoryTest {

  private val playerPointsDao: PlayerPointsDao = mockk()
  private val playerDao: PlayerDao = mockk()

  private val repository: RetrievePlayersPointsRepository = SqlRetrievePlayersPointsRepository(playerPointsDao, playerDao)

  @Nested
  inner class RetrieveByTournamentId {

    @Test
    fun `no players points found for tournament`() {

      val player = aJdbcPlayerDto(playerId = A_PLAYER_ID, fullName = A_PLAYER_NAME)

      val players = listOf(player)

      val playerPoints = PlayerPoints(playerId = A_PLAYER_ID, playerName = A_PLAYER_NAME, pointsByTournament = emptyMap(), totalPoints = 0.0)
      val expected = FoundPlayersPoints(listOf(playerPoints))

      every { playerPointsDao.retrieveByTournamentId(A_TOURNAMENT_ID) } returns emptyList()
      every { playerDao.retrieveAll() } returns players

      assertThat(repository.retrieveByTournamentId(A_TOURNAMENT_ID)).isEqualTo(expected)
    }

    @Test
    fun `exception from playerPointsDao`() {

      val expected = InternalErrorPlayersPoints

      every { playerPointsDao.retrieveByTournamentId(A_TOURNAMENT_ID) } throws RuntimeException()

      assertThat(repository.retrieveByTournamentId(A_TOURNAMENT_ID)).isEqualTo(expected)

      verify { playerDao wasNot called }
    }

    @Test
    fun `players points found from dao but error from playerDao`() {

      val playerPointsDto1 = aJdbcPlayerPointsDto()
      val playerPointsDto2 = aJdbcPlayerPointsDto()
      val playersPointsDto = listOf(playerPointsDto1, playerPointsDto2)

      val expected = InternalErrorPlayersPoints

      every { playerPointsDao.retrieveByTournamentId(A_TOURNAMENT_ID) } returns playersPointsDto
      every { playerDao.retrieveAll() } throws RuntimeException()

      assertThat(repository.retrieveByTournamentId(A_TOURNAMENT_ID)).isEqualTo(expected)
    }

    @Test
    fun `player points for unknown player are ignored`() {

      val playerPointsDto1 = aJdbcPlayerPointsDto(tournamentId = A_TOURNAMENT_ID, playerId = A_PLAYER_ID, fantaPoints = 10.0)
      val playerPointsDto2 = aJdbcPlayerPointsDto(tournamentId = A_TOURNAMENT_ID, playerId = A_THIRD_PLAYER_ID)
      val playersPointsDto = listOf(playerPointsDto1, playerPointsDto2)
      val player1 = aJdbcPlayerDto(playerId = A_PLAYER_ID, fullName = A_PLAYER_NAME)
      val player2 = aJdbcPlayerDto(playerId = ANOTHER_PLAYER_ID, fullName = ANOTHER_PLAYER_NAME)
      val players = listOf(player1, player2)

      val playerPoints1 = PlayerPoints(playerId = A_PLAYER_ID,
                                       playerName = A_PLAYER_NAME,
                                       pointsByTournament = mapOf(A_TOURNAMENT_ID to 10.0),
                                       totalPoints = 10.0)
      val playerPoints2 = PlayerPoints(playerId = ANOTHER_PLAYER_ID,
                                       playerName = ANOTHER_PLAYER_NAME,
                                       pointsByTournament = emptyMap(),
                                       totalPoints = 0.0)
      val expected = FoundPlayersPoints(playersPoints = listOf(playerPoints1, playerPoints2))

      every { playerPointsDao.retrieveByTournamentId(A_TOURNAMENT_ID) } returns playersPointsDto
      every { playerDao.retrieveAll() } returns players

      assertThat(repository.retrieveByTournamentId(A_TOURNAMENT_ID)).isEqualTo(expected)
    }

    @Test
    fun `players points found for tournament`() {

      val playerPointsDto1 = aJdbcPlayerPointsDto(tournamentId = A_TOURNAMENT_ID, playerId = A_PLAYER_ID, fantaPoints = 10.0)
      val playerPointsDto2 = aJdbcPlayerPointsDto(tournamentId = A_TOURNAMENT_ID, playerId = A_THIRD_PLAYER_ID, fantaPoints = 12.0)
      val playersPointsDto = listOf(playerPointsDto1, playerPointsDto2)
      val player1 = aJdbcPlayerDto(playerId = A_PLAYER_ID, fullName = A_PLAYER_NAME)
      val player2 = aJdbcPlayerDto(playerId = ANOTHER_PLAYER_ID, fullName = ANOTHER_PLAYER_NAME)
      val player3 = aJdbcPlayerDto(playerId = A_THIRD_PLAYER_ID, fullName = A_THIRD_PLAYER_NAME)
      val players = listOf(player1, player2, player3)

      val playerPoints1 = PlayerPoints(playerId = A_PLAYER_ID,
                                       playerName = A_PLAYER_NAME,
                                       pointsByTournament = mapOf(A_TOURNAMENT_ID to 10.0),
                                       totalPoints = 10.0)
      val playerPoints2 = PlayerPoints(playerId = ANOTHER_PLAYER_ID,
                                       playerName = ANOTHER_PLAYER_NAME,
                                       pointsByTournament = emptyMap(),
                                       totalPoints = 0.0)
      val playerPoints3 = PlayerPoints(playerId = A_THIRD_PLAYER_ID,
                                       playerName = A_THIRD_PLAYER_NAME,
                                       pointsByTournament = mapOf(A_TOURNAMENT_ID to 12.0),
                                       totalPoints = 12.0)
      val expected = FoundPlayersPoints(playersPoints = listOf(playerPoints1, playerPoints2, playerPoints3))

      every { playerPointsDao.retrieveByTournamentId(A_TOURNAMENT_ID) } returns playersPointsDto
      every { playerDao.retrieveAll() } returns players

      assertThat(repository.retrieveByTournamentId(A_TOURNAMENT_ID)).isEqualTo(expected)
    }
  }

  @Nested
  inner class RetrieveByYear {

    @Test
    fun `exception from playerPointsDao`() {

      every { playerPointsDao.retrieveByTournamentYear(A_YEAR) } throws RuntimeException()

      assertThat(repository.retrieveByYear(A_YEAR)).isEqualTo(InternalErrorPlayersPoints)

      verify { playerDao wasNot called }
    }

    @Test
    fun `no player points found for year`() {

      val player = aJdbcPlayerDto(playerId = A_PLAYER_ID, fullName = A_PLAYER_NAME)
      val players = listOf(player)

      val playerPoints = PlayerPoints(playerId = A_PLAYER_ID, playerName = A_PLAYER_NAME, pointsByTournament = emptyMap(), totalPoints = 0.0)
      val expected = FoundPlayersPoints(listOf(playerPoints))

      every { playerPointsDao.retrieveByTournamentYear(A_YEAR) } returns emptyList()
      every { playerDao.retrieveAll() } returns players

      assertThat(repository.retrieveByYear(A_YEAR)).isEqualTo(expected)
    }

    @Test
    fun `player points for unknown player are ignored`() {

      val pointsDto1 = aJdbcPlayerPointsDto(tournamentYear = A_YEAR,
                                            tournamentId = A_TOURNAMENT_ID,
                                            playerId = A_PLAYER_ID,
                                            fantaPoints = 10.0)
      val pointsDto2 = aJdbcPlayerPointsDto(tournamentYear = A_YEAR,
                                            tournamentId = ANOTHER_TOURNAMENT_ID,
                                            playerId = ANOTHER_PLAYER_ID,
                                            fantaPoints = 5.0)
      val playersPoints = listOf(pointsDto1, pointsDto2)
      val player = aJdbcPlayerDto(playerId = A_PLAYER_ID, fullName = A_PLAYER_NAME)
      val players = listOf(player)

      val playerPoints = PlayerPoints(playerId = A_PLAYER_ID,
                                      playerName = A_PLAYER_NAME,
                                      pointsByTournament = mapOf(A_TOURNAMENT_ID to 10.0),
                                      totalPoints = 10.0)
      val expected = FoundPlayersPoints(listOf(playerPoints))

      every { playerPointsDao.retrieveByTournamentYear(A_YEAR) } returns playersPoints
      every { playerDao.retrieveAll() } returns players

      assertThat(repository.retrieveByYear(A_YEAR)).isEqualTo(expected)
    }

    @Test
    fun `player points found for year`() {

      val pointsDto1 = aJdbcPlayerPointsDto(tournamentYear = A_YEAR,
                                            tournamentId = A_TOURNAMENT_ID,
                                            playerId = A_PLAYER_ID,
                                            fantaPoints = 10.0)
      val pointsDto2 = aJdbcPlayerPointsDto(tournamentYear = A_YEAR,
                                            tournamentId = ANOTHER_TOURNAMENT_ID,
                                            playerId = A_PLAYER_ID,
                                            fantaPoints = 5.0)
      val pointsDto3 = aJdbcPlayerPointsDto(tournamentYear = A_YEAR,
                                            tournamentId = A_TOURNAMENT_ID,
                                            playerId = ANOTHER_PLAYER_ID,
                                            fantaPoints = 3.0)
      val playersPoints = listOf(pointsDto1, pointsDto2, pointsDto3)
      val player1 = aJdbcPlayerDto(playerId = A_PLAYER_ID, fullName = A_PLAYER_NAME)
      val player2 = aJdbcPlayerDto(playerId = ANOTHER_PLAYER_ID, fullName = ANOTHER_PLAYER_NAME)
      val player3 = aJdbcPlayerDto(playerId = A_THIRD_PLAYER_ID, fullName = A_THIRD_PLAYER_NAME)
      val players = listOf(player1, player2, player3)

      val playerPoints1 = PlayerPoints(playerId = A_PLAYER_ID,
                                       playerName = A_PLAYER_NAME,
                                       pointsByTournament = mapOf(A_TOURNAMENT_ID to 10.0, ANOTHER_TOURNAMENT_ID to 5.0),
                                       totalPoints = 15.0)
      val playerPoints2 = PlayerPoints(playerId = ANOTHER_PLAYER_ID,
                                       playerName = ANOTHER_PLAYER_NAME,
                                       pointsByTournament = mapOf(A_TOURNAMENT_ID to 3.0),
                                       totalPoints = 3.0)
      val playerPoints3 = PlayerPoints(playerId = A_THIRD_PLAYER_ID,
                                       playerName = A_THIRD_PLAYER_NAME,
                                       pointsByTournament = emptyMap(),
                                       totalPoints = 0.0)
      val expected = FoundPlayersPoints(listOf(playerPoints1, playerPoints2, playerPoints3))

      every { playerPointsDao.retrieveByTournamentYear(A_YEAR) } returns playersPoints
      every { playerDao.retrieveAll() } returns players

      assertThat(repository.retrieveByYear(A_YEAR)).isEqualTo(expected)
    }
  }

  companion object {

    private const val A_TOURNAMENT_ID = 1234
    private const val ANOTHER_TOURNAMENT_ID = 5678
    private const val A_YEAR = 2025
    private const val A_PLAYER_ID = "A_PLAYER_ID"
    private const val ANOTHER_PLAYER_ID = "ANOTHER_PLAYER_ID"
    private const val A_THIRD_PLAYER_ID = "A_THIRD_PLAYER_ID"
    private const val A_PLAYER_NAME = "A_PLAYER_NAME"
    private const val ANOTHER_PLAYER_NAME = "ANOTHER_PLAYER_NAME"
    private const val A_THIRD_PLAYER_NAME = "A_THIRD_PLAYER_NAME"
  }
}
