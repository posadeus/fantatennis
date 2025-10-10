package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.infrastructure.RetrieveTournamentsRepository
import com.posadeus.fantatennis.domain.model.Tournament
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.TournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcTournamentDto.aJdbcTournamentDto
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test

class JdbcRetrieveTournamentsRepositoryTest {

  private val tournamentDao: TournamentDao = mockk()

  private val repository: RetrieveTournamentsRepository = JdbcRetrieveTournamentsRepository(tournamentDao)

  @Test
  fun `no results returned by the query`() {

    val expected = emptyList<Tournament>()

    every { tournamentDao.retrieveAllBy(A_YEAR) } returns emptyList()

    assertThat(repository.retrieveAllBy(A_YEAR)).isEqualTo(expected)
  }

  @Test
  fun `retrieve results successfully`() {

    val jdbcTournament1 = aJdbcTournamentDto(tournamentId = AN_ID,
                                             tennisTvId = A_TENNIS_TV_ID,
                                             points = A_POINTS,
                                             year = A_YEAR)
    val jdbcTournament2 = aJdbcTournamentDto(tournamentId = ANOTHER_ID,
                                             tennisTvId = ANOTHER_TENNIS_TV_ID,
                                             points = ANOTHER_POINTS,
                                             year = A_YEAR)
    val jdbcTournaments = listOf(jdbcTournament1, jdbcTournament2)

    val tournament1 = Tournament(id = AN_ID,
                                 tennisTvId = A_TENNIS_TV_ID,
                                 points = A_POINTS,
                                 year = A_YEAR)
    val tournament2 = Tournament(id = ANOTHER_ID,
                                 tennisTvId = ANOTHER_TENNIS_TV_ID,
                                 points = ANOTHER_POINTS,
                                 year = A_YEAR)
    val expected = listOf(tournament1, tournament2)

    every { tournamentDao.retrieveAllBy(A_YEAR) } returns jdbcTournaments

    assertThat(repository.retrieveAllBy(A_YEAR)).isEqualTo(expected)
  }

  companion object {

    private const val AN_ID = 1
    private const val ANOTHER_ID = 2
    private const val A_TENNIS_TV_ID = 12
    private const val ANOTHER_TENNIS_TV_ID = 34
    private const val A_POINTS = 1000
    private const val ANOTHER_POINTS = 500
    private const val A_YEAR = 2002
  }
}