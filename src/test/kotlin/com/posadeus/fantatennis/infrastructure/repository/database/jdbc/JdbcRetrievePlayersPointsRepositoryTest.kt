package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.infrastructure.RetrievePlayersPointsRepository
import com.posadeus.fantatennis.domain.model.PlayersPoints.FoundPlayersPoints
import com.posadeus.fantatennis.domain.model.PlayersPoints.InternalErrorPlayersPoints
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerPointsDao
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test

class JdbcRetrievePlayersPointsRepositoryTest {

  private val playerPointsDao: PlayerPointsDao = mockk()

  private val repository: RetrievePlayersPointsRepository = JdbcRetrievePlayersPointsRepository(playerPointsDao)

  @Test
  fun `no players points found for tournament`() {

    val expected = FoundPlayersPoints(playersPoints = emptyList())

    every { playerPointsDao.retrieveByTournamentId(A_TOURNAMENT_ID) } returns emptyList()

    assertThat(repository.retrieveBy(A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  @Test
  fun `exception from playerPointsDao`() {

    val expected = InternalErrorPlayersPoints

    every { playerPointsDao.retrieveByTournamentId(A_TOURNAMENT_ID) } throws RuntimeException()

    assertThat(repository.retrieveBy(A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  companion object {

    private const val A_TOURNAMENT_ID = 1234
  }
}