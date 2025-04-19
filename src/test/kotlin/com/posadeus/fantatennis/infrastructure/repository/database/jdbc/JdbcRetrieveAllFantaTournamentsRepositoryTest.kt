package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.infrastructure.RetrieveAllFantaTournamentsRepository
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournaments.Invalid
import com.posadeus.fantatennis.domain.model.FantaTournaments.Valid
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.FantaTournamentDto
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper

class JdbcRetrieveAllFantaTournamentsRepositoryTest {

  private val jdbcTemplate: JdbcTemplate = mockk()

  private val repository: RetrieveAllFantaTournamentsRepository = JdbcRetrieveAllFantaTournamentsRepository(jdbcTemplate)

  @Test
  fun `retrieve all fanta tournaments`() {

    val fantaTournamentDto1 = FantaTournamentDto(id = AN_ID,
                                                 startingTournamentId = A_TOURNAMENT_ID,
                                                 endingTournamentId = ANOTHER_TOURNAMENT_ID,
                                                 year = AN_YEAR)
    val fantaTournamentDto2 = FantaTournamentDto(id = ANOTHER_ID,
                                                 startingTournamentId = ANOTHER_TOURNAMENT_ID,
                                                 endingTournamentId = A_THIRD_TOURNAMENT_ID,
                                                 year = AN_YEAR)
    val fantaTournamentDto3 = FantaTournamentDto(id = A_THIRD_ID,
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

    every { jdbcTemplate.query(RETRIEVE_QUERY, any<RowMapper<FantaTournamentDto>>()) } returns fantaTournaments

    assertThat(repository.retrieve()).isEqualTo(expected)
  }

  @Test
  fun `no fanta tournaments found`() {

    val expected = Valid(emptySet())

    every { jdbcTemplate.query(RETRIEVE_QUERY, any<RowMapper<FantaTournamentDto>>()) } returns emptyList()

    assertThat(repository.retrieve()).isEqualTo(expected)
  }

  @Test
  fun `error from db`() {

    val expected = Invalid

    every { jdbcTemplate.query(RETRIEVE_QUERY, any<RowMapper<FantaTournamentDto>>()) } throws RuntimeException("Scary error!")

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

    private val RETRIEVE_QUERY = """
      SELECT *
      FROM FANTA_TOURNAMENTS
    """.trimIndent()
  }
}