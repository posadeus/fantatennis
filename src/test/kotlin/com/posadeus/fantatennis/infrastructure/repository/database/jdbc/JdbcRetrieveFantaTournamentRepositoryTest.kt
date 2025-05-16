package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTournamentRepository
import com.posadeus.fantatennis.domain.model.FantaTournament.InvalidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTournamentDto
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class JdbcRetrieveFantaTournamentRepositoryTest {

  private val jdbcTemplate: NamedParameterJdbcTemplate = mockk()

  private val repository: RetrieveFantaTournamentRepository = JdbcRetrieveFantaTournamentRepository(jdbcTemplate)

  @Test
  fun `retrieve operation founds a result`() {

    val fantaTournamentDto = JdbcFantaTournamentDto(id = 1,
                                                    startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                                    endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                                    year = A_TOURNAMENT_YEAR)
    val queryParams = mapOf("id" to 1)

    val expected = ValidFantaTournament(id = 1,
                                        startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                        endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                        tournamentYear = A_TOURNAMENT_YEAR)

    every { jdbcTemplate.queryForObject(RETRIEVE_QUERY, queryParams, any<RowMapper<JdbcFantaTournamentDto>>()) } returns fantaTournamentDto

    assertThat(repository.retrieve(1)).isEqualTo(expected)
  }

  @Test
  fun `retrieve operation founds no results`() {

    val queryParams = mapOf("id" to A_MISSING_TOURNAMENT_ID)

    val expected = InvalidFantaTournament

    every {
      jdbcTemplate.queryForObject(RETRIEVE_QUERY, queryParams, any<RowMapper<JdbcFantaTournamentDto>>())
    } throws EmptyResultDataAccessException(1)

    assertThat(repository.retrieve(A_MISSING_TOURNAMENT_ID)).isEqualTo(expected)
  }

  @Test
  fun `error during retrieve operation`() {

    val queryParams = mapOf("id" to A_MISSING_TOURNAMENT_ID)

    val expected = InvalidFantaTournament

    every { jdbcTemplate.queryForObject(RETRIEVE_QUERY, queryParams, any<RowMapper<JdbcFantaTournamentDto>>()) } throws Exception()

    assertThat(repository.retrieve(A_MISSING_TOURNAMENT_ID)).isEqualTo(expected)
  }

  companion object {

    private const val A_MISSING_TOURNAMENT_ID = 1
    private const val A_STARTING_TOURNAMENT_ID = 1
    private const val AN_ENDING_TOURNAMENT_ID = 10
    private const val A_TOURNAMENT_YEAR = 2022

    private val RETRIEVE_QUERY = """
      SELECT *
      FROM FANTA_TOURNAMENTS 
      WHERE FANTA_TOURNAMENT_ID = :id;
    """.trimIndent()
  }
}