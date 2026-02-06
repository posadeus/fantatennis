package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.controller.model.fantatournament.FantaTournamentToCreateDto
import com.posadeus.fantatennis.domain.infrastructure.CreateFantaTournamentRepository
import com.posadeus.fantatennis.domain.model.FantaTournament.InvalidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.FantaTournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.NewJdbcFantaTournamentDto
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class JdbcCreateFantaTournamentRepositoryTest {

  private val fantaTournamentDao: FantaTournamentDao = mockk()

  private val repository: CreateFantaTournamentRepository = JdbcCreateFantaTournamentRepository(fantaTournamentDao)

  @Test
  fun `fanta tournament created successfully`() {

    val dto = FantaTournamentToCreateDto(startingTournamentId = 2,
                                         endingTournamentId = 10,
                                         tournamentYear = 2022)

    val newFantaTournamentDto = NewJdbcFantaTournamentDto(startingTournamentId = 2,
                                                          endingTournamentId = 10,
                                                          year = 2022)

    val expected = ValidFantaTournament(id = A_TOURNAMENT_ID,
                                        startingTournamentId = 2,
                                        endingTournamentId = 10,
                                        tournamentYear = 2022)

    every { fantaTournamentDao.persist(newFantaTournamentDto) } returns A_TOURNAMENT_ID

    assertThat(repository.create(dto)).isEqualTo(expected)
  }

  @Test
  fun `creation fails due to exception from jdbc`() {

    val dto = FantaTournamentToCreateDto(startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                         endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                         tournamentYear = A_TOURNAMENT_YEAR)

    val newFantaTournamentDto = NewJdbcFantaTournamentDto(startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                                          endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                                          year = A_TOURNAMENT_YEAR)

    val expected = InvalidFantaTournament

    every { fantaTournamentDao.persist(newFantaTournamentDto) } throws RuntimeException()

    assertThat(repository.create(dto)).isEqualTo(expected)
  }

  companion object {

    private const val A_TOURNAMENT_ID = 1
    private const val A_STARTING_TOURNAMENT_ID = 1
    private const val AN_ENDING_TOURNAMENT_ID = 10
    private const val A_TOURNAMENT_YEAR = 2022
  }
}