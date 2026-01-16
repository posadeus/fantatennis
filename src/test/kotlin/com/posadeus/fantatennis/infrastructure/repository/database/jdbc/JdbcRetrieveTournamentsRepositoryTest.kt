package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.infrastructure.RetrieveTournamentsRepository
import com.posadeus.fantatennis.domain.model.TestTournament.aTournament
import com.posadeus.fantatennis.domain.model.Tournament.*
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.TournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcTournamentDto.aJdbcTournamentDto
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.dao.EmptyResultDataAccessException

class JdbcRetrieveTournamentsRepositoryTest {

  private val tournamentDao: TournamentDao = mockk()

  private val repository: RetrieveTournamentsRepository = JdbcRetrieveTournamentsRepository(tournamentDao)

  @Nested
  inner class RetrieveAllTournaments {

    @Test
    fun `error on repository operation`() {

      val expected = emptyList<FoundTournament>()

      every { tournamentDao.retrieveAllBy(A_YEAR) } throws RuntimeException()

      assertThat(repository.retrieveAllBy(A_YEAR)).isEqualTo(expected)
    }

    @Test
    fun `no results returned by the query`() {

      val expected = emptyList<FoundTournament>()

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

      val tournament1 = aTournament(id = AN_ID,
                                    tennisTvId = A_TENNIS_TV_ID,
                                    points = A_POINTS,
                                    year = A_YEAR)
      val tournament2 = aTournament(id = ANOTHER_ID,
                                    tennisTvId = ANOTHER_TENNIS_TV_ID,
                                    points = ANOTHER_POINTS,
                                    year = A_YEAR)
      val expected = listOf(tournament1, tournament2)

      every { tournamentDao.retrieveAllBy(A_YEAR) } returns jdbcTournaments

      assertThat(repository.retrieveAllBy(A_YEAR)).isEqualTo(expected)
    }
  }

  @Nested
  inner class RetrieveSingleTournament {

    @Test
    fun `error returned by the query`() {

      val expected = InternalErrorTournament

      every { tournamentDao.retrieveBy(A_TOURNAMENT_ID) } throws RuntimeException()

      assertThat(repository.retrieveBy(A_TOURNAMENT_ID)).isEqualTo(expected)
    }

    @Test
    fun `no results returned by the query`() {

      val expected = NotFoundTournament

      every { tournamentDao.retrieveBy(A_TOURNAMENT_ID) } throws EmptyResultDataAccessException(1)

      assertThat(repository.retrieveBy(A_TOURNAMENT_ID)).isEqualTo(expected)
    }

    @Test
    fun `tournament retrieved successfully`() {

      val tournamentDto = aJdbcTournamentDto(tournamentId = A_TOURNAMENT_ID,
                                             tennisTvId = A_TENNIS_TV_ID,
                                             name = A_NAME,
                                             points = A_POINTS,
                                             year = A_YEAR)

      val expected = FoundTournament(id = A_TOURNAMENT_ID,
                                     tennisTvId = A_TENNIS_TV_ID,
                                     name = A_NAME,
                                     points = A_POINTS,
                                     year = A_YEAR)

      every { tournamentDao.retrieveBy(A_TOURNAMENT_ID) } returns tournamentDto

      assertThat(repository.retrieveBy(A_TOURNAMENT_ID)).isEqualTo(expected)
    }
  }

  companion object {

    private const val AN_ID = 1
    private const val ANOTHER_ID = 2
    private const val A_TOURNAMENT_ID = 1234
    private const val A_TENNIS_TV_ID = 12
    private const val ANOTHER_TENNIS_TV_ID = 34
    private const val A_POINTS = 1000
    private const val ANOTHER_POINTS = 500
    private const val A_YEAR = 2002
    private const val A_NAME = "A_NAME"
  }
}