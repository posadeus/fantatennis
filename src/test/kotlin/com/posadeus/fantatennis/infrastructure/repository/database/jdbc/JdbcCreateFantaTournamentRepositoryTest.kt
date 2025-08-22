package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.controller.model.tournament.TournamentToCreateDto
import com.posadeus.fantatennis.domain.infrastructure.CreateFantaTournamentRepository
import com.posadeus.fantatennis.domain.model.FantaTournament.InvalidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.JdbcTemplate

class JdbcCreateFantaTournamentRepositoryTest {

  private val jdbcTemplate: JdbcTemplate = mockk()

  private val repository: CreateFantaTournamentRepository = JdbcCreateFantaTournamentRepository(jdbcTemplate)

  @Test
  fun `fanta tournament created successfully`() {

    val dto = TournamentToCreateDto(startingTournamentId = 2,
                                    endingTournamentId = 10,
                                    tournamentYear = 2022)

    val expected = ValidFantaTournament(id = A_TOURNAMENT_ID,
                                        startingTournamentId = 2,
                                        endingTournamentId = 10,
                                        tournamentYear = 2022)

    every { jdbcTemplate.update(CREATE_FANTA_TOURNAMENT_QUERY, 2, 10, 2022) } returns A_TOURNAMENT_ID

    assertThat(repository.create(dto)).isEqualTo(expected)
  }

  @Test
  fun `creation fails due to exception from jdbc`() {

    val dto = TournamentToCreateDto(startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                    endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                    tournamentYear = A_TOURNAMENT_YEAR)

    val expected = InvalidFantaTournament

    every {
      jdbcTemplate.update(CREATE_FANTA_TOURNAMENT_QUERY, A_STARTING_TOURNAMENT_ID, AN_ENDING_TOURNAMENT_ID, A_TOURNAMENT_YEAR)
    } throws RuntimeException()

    assertThat(repository.create(dto)).isEqualTo(expected)
  }

  companion object {

    private const val A_TOURNAMENT_ID = 1
    private const val A_STARTING_TOURNAMENT_ID = 1
    private const val AN_ENDING_TOURNAMENT_ID = 10
    private const val A_TOURNAMENT_YEAR = 2022

    private val CREATE_FANTA_TOURNAMENT_QUERY = """
      INSERT INTO FANTA_TOURNAMENTS
        (STARTING_TOURNAMENT, ENDING_TOURNAMENT, TOURNAMENT_YEAR)
      VALUES(?, ?, ?);
    """.trimIndent()
  }
}