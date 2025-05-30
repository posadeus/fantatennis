package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.infrastructure.CreateTeamRepository
import com.posadeus.fantatennis.domain.model.FantaTeamError
import com.posadeus.fantatennis.domain.model.FantaTeamOk
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTournamentDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcFantaTournamentDto.aJdbcFantaTournamentDto
import io.mockk.*
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder

class JdbcCreateTeamRepositoryTest {

  private val jdbcTemplate: JdbcTemplate = mockk()
  private val namedJdbcTemplate: NamedParameterJdbcTemplate = mockk()

  private val repository: CreateTeamRepository = JdbcCreateTeamRepository(jdbcTemplate, namedJdbcTemplate)

  @Test
  fun `create team fails due to missing fanta tournament`() {

    val queryParams = mapOf("id" to A_MISSING_FANTA_TOURNAMENT_ID)

    val expected = FantaTeamError

    every {
      namedJdbcTemplate.queryForObject(RETRIEVE_FANTA_TOURNAMENT_QUERY, queryParams, any<RowMapper<JdbcFantaTournamentDto>>())
    } throws EmptyResultDataAccessException(1)

    assertThat(repository.create(AN_OWNER_ID, A_MISSING_FANTA_TOURNAMENT_ID)).isEqualTo(expected)

    verify { jdbcTemplate wasNot called }
  }

  @Test
  fun `create team fails due to an error during fantaTeams creation`() {

    val queryParams = mapOf("id" to A_FANTA_TOURNAMENT_ID)
    val fantaTournamentDto = aJdbcFantaTournamentDto(id = A_FANTA_TOURNAMENT_ID)

    val expected = FantaTeamError

    every {
      namedJdbcTemplate.queryForObject(RETRIEVE_FANTA_TOURNAMENT_QUERY, queryParams, any<RowMapper<JdbcFantaTournamentDto>>())
    } returns fantaTournamentDto
    every {
      namedJdbcTemplate.update(eq(CREATE_QUERY_FANTA_TEAMS),
                               match { it.getValue("ownerId") == AN_OWNER_ID },
                               any<GeneratedKeyHolder>())
    } throws RuntimeException()

    assertThat(repository.create(AN_OWNER_ID, A_FANTA_TOURNAMENT_ID)).isEqualTo(expected)
  }

  @Test
  fun `create team fails due to an error during fantaTournamentsTeams creation`() {

    val queryParams = mapOf("id" to A_FANTA_TOURNAMENT_ID)
    val fantaTournamentDto = aJdbcFantaTournamentDto(id = A_FANTA_TOURNAMENT_ID)

    val expected = FantaTeamError

    val generatedKeyHolder: GeneratedKeyHolder = mockk()

    every {
      namedJdbcTemplate.queryForObject(RETRIEVE_FANTA_TOURNAMENT_QUERY, queryParams, any<RowMapper<JdbcFantaTournamentDto>>())
    } returns fantaTournamentDto
    every {
      namedJdbcTemplate.update(eq(CREATE_QUERY_FANTA_TEAMS),
                               match { it.getValue("ownerId") == AN_OWNER_ID },
                               any<GeneratedKeyHolder>())
    } returns 1
    every { generatedKeyHolder.key } returns A_TEAM_ID
    every { jdbcTemplate.update(CREATE_QUERY_FANTA_TOURNAMENTS_TEAMS, A_FANTA_TOURNAMENT_ID, A_TEAM_ID) } throws RuntimeException()

    assertThat(repository.create(AN_OWNER_ID, A_FANTA_TOURNAMENT_ID)).isEqualTo(expected)
  }

  @Test
  fun `create team not work because fanta teams insert fails`() {

    val queryParams = mapOf("id" to A_FANTA_TOURNAMENT_ID)
    val fantaTournamentDto = aJdbcFantaTournamentDto(id = A_FANTA_TOURNAMENT_ID)

    val expected = FantaTeamError

    mockkConstructor(GeneratedKeyHolder::class)

    every {
      namedJdbcTemplate.queryForObject(RETRIEVE_FANTA_TOURNAMENT_QUERY, queryParams, any<RowMapper<JdbcFantaTournamentDto>>())
    } returns fantaTournamentDto
    every {
      namedJdbcTemplate.update(eq(CREATE_QUERY_FANTA_TEAMS),
                               match { it.getValue("ownerId") == AN_OWNER_ID },
                               any<GeneratedKeyHolder>())
    } returns 0

    assertThat(repository.create(AN_OWNER_ID, A_FANTA_TOURNAMENT_ID)).isEqualTo(expected)

    verify { jdbcTemplate wasNot called }
  }

  @Test
  fun `create team works`() {

    val queryParams = mapOf("id" to 77)
    val fantaTournamentDto = aJdbcFantaTournamentDto(id = 77)

    val expected = FantaTeamOk(id = 42, ownerId = "AN_OWNER_ID")

    mockkConstructor(GeneratedKeyHolder::class)

    every {
      namedJdbcTemplate.queryForObject(RETRIEVE_FANTA_TOURNAMENT_QUERY, queryParams, any<RowMapper<JdbcFantaTournamentDto>>())
    } returns fantaTournamentDto
    every {
      namedJdbcTemplate.update(eq(CREATE_QUERY_FANTA_TEAMS),
                               match { it.getValue("ownerId") == "AN_OWNER_ID" },
                               any<GeneratedKeyHolder>())
    } returns 1
    every { anyConstructed<GeneratedKeyHolder>().key } returns 42
    every { jdbcTemplate.update(CREATE_QUERY_FANTA_TOURNAMENTS_TEAMS, 77, 42) } returns A_FANTA_TOURNAMENTS_TEAMS_ID

    assertThat(repository.create("AN_OWNER_ID", 77)).isEqualTo(expected)
  }

  companion object {

    private const val AN_OWNER_ID = "AN_OWNER_ID"
    private const val A_MISSING_FANTA_TOURNAMENT_ID = 1
    private const val A_FANTA_TOURNAMENT_ID = 1
    private const val A_TEAM_ID = 42
    private const val A_FANTA_TOURNAMENTS_TEAMS_ID = 5

    private val RETRIEVE_FANTA_TOURNAMENT_QUERY = """
      SELECT *
      FROM FANTA_TOURNAMENTS 
      WHERE FANTA_TOURNAMENT_ID = :id;
    """.trimIndent()

    private val CREATE_QUERY_FANTA_TEAMS = """
      INSERT INTO FANTA_TEAMS
      (OWNER_ID)
      VALUES(:ownerId);
    """.trimIndent()

    private val CREATE_QUERY_FANTA_TOURNAMENTS_TEAMS = """
      INSERT INTO FANTA_TOURNAMENTS_TEAMS
      (FANTA_TOURNAMENT_ID, TEAM_ID)
      VALUES(?, ?);
    """.trimIndent()
  }
}