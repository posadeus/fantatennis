package com.posadeus.fantatennis.infrastructure.repository.database

import com.posadeus.fantatennis.domain.infrastructure.RetrieveAllFantaTournamentsRepository
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournaments.Invalid
import com.posadeus.fantatennis.domain.model.FantaTournaments.Valid
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.FantaTournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTournamentDto
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.Test

class SqlRetrieveAllFantaTournamentsRepositoryTest {

  private val fantaTournamentDao: FantaTournamentDao = mockk()

  private val repository: RetrieveAllFantaTournamentsRepository = SqlRetrieveAllFantaTournamentsRepository(fantaTournamentDao)

  @Test
  fun `retrieve all fanta tournaments`() {

    val fantaTournamentDto1 = JdbcFantaTournamentDto(id = AN_ID,
                                                     startingTournamentId = A_TOURNAMENT_ID,
                                                     endingTournamentId = ANOTHER_TOURNAMENT_ID,
                                                     year = AN_YEAR)
    val fantaTournamentDto2 = JdbcFantaTournamentDto(id = ANOTHER_ID,
                                                     startingTournamentId = ANOTHER_TOURNAMENT_ID,
                                                     endingTournamentId = A_THIRD_TOURNAMENT_ID,
                                                     year = AN_YEAR)
    val fantaTournamentDto3 = JdbcFantaTournamentDto(id = A_THIRD_ID,
                                                     startingTournamentId = A_TOURNAMENT_ID,
                                                     endingTournamentId = A_THIRD_TOURNAMENT_ID,
                                                     year = ANOTHER_YEAR)
    val fantaTournaments = listOf(fantaTournamentDto1, fantaTournamentDto2, fantaTournamentDto3)

    val validFantaTournament1 = ValidFantaTournament(id = AN_ID,
                                                     startingTournamentId = A_TOURNAMENT_ID,
                                                     endingTournamentId = ANOTHER_TOURNAMENT_ID,
                                                     tournamentYear = AN_YEAR)
    val validFantaTournament2 = ValidFantaTournament(id = ANOTHER_ID,
                                                     startingTournamentId = ANOTHER_TOURNAMENT_ID,
                                                     endingTournamentId = A_THIRD_TOURNAMENT_ID,
                                                     tournamentYear = AN_YEAR)
    val validFantaTournament3 = ValidFantaTournament(id = A_THIRD_ID,
                                                     startingTournamentId = A_TOURNAMENT_ID,
                                                     endingTournamentId = A_THIRD_TOURNAMENT_ID,
                                                     tournamentYear = ANOTHER_YEAR)

    val expected = Valid(setOf(validFantaTournament1, validFantaTournament2, validFantaTournament3))

    every { fantaTournamentDao.retrieveAll() } returns fantaTournaments

    assertThat(repository.retrieve()).isEqualTo(expected)
  }

  @Test
  fun `no fanta tournaments found`() {

    val expected = Valid(emptySet())

    every { fantaTournamentDao.retrieveAll() } returns emptyList()

    assertThat(repository.retrieve()).isEqualTo(expected)
  }

  @Test
  fun `error from dao`() {

    val expected = Invalid

    every { fantaTournamentDao.retrieveAll() } throws RuntimeException("Scary error!")

    assertThat(repository.retrieve()).isEqualTo(expected)
  }

  companion object {

    private const val AN_ID = 1
    private const val ANOTHER_ID = 2
    private const val A_THIRD_ID = 3
    private const val A_TOURNAMENT_ID = 123
    private const val ANOTHER_TOURNAMENT_ID = 456
    private const val A_THIRD_TOURNAMENT_ID = 789
    private const val AN_YEAR = 2000
    private const val ANOTHER_YEAR = 2001
  }
}