package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.infrastructure.RetrievePlayersPointsRepository
import com.posadeus.fantatennis.domain.model.PlayersPoints.FoundPlayersPoints
import com.posadeus.fantatennis.domain.model.PlayersPoints.InternalErrorPlayersPoints
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerPointsDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcPlayerPointsDto.aJdbcPlayerPointsDto
import io.mockk.*
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test

class JdbcRetrievePlayersPointsRepositoryTest {

  private val playerPointsDao: PlayerPointsDao = mockk()
  private val playerDao: PlayerDao = mockk()

  private val repository: RetrievePlayersPointsRepository = JdbcRetrievePlayersPointsRepository(playerPointsDao, playerDao)

  @Test
  fun `no players points found for tournament`() {

    val expected = FoundPlayersPoints(playersPoints = emptyList())

    every { playerPointsDao.retrieveByTournamentId(A_TOURNAMENT_ID) } returns emptyList()

    assertThat(repository.retrieveBy(A_TOURNAMENT_ID)).isEqualTo(expected)

    verify { playerDao wasNot called }
  }

  @Test
  fun `exception from playerPointsDao`() {

    val expected = InternalErrorPlayersPoints

    every { playerPointsDao.retrieveByTournamentId(A_TOURNAMENT_ID) } throws RuntimeException()

    assertThat(repository.retrieveBy(A_TOURNAMENT_ID)).isEqualTo(expected)

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

    assertThat(repository.retrieveBy(A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  companion object {

    private const val A_TOURNAMENT_ID = 1234
  }
}