package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.exception.FantaTeamCreationException
import com.posadeus.fantatennis.domain.infrastructure.CreateTeamRepository
import com.posadeus.fantatennis.domain.model.FantaTeam
import com.posadeus.fantatennis.infrastructure.assertThrowsWithMessage
import io.mockk.*
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder

class JdbcCreateTeamRepositoryTest {

  private val jdbcTemplate: JdbcTemplate = mockk()
  private val namedJdbcTemplate: NamedParameterJdbcTemplate = mockk()

  private val repository: CreateTeamRepository = JdbcCreateTeamRepository(jdbcTemplate, namedJdbcTemplate)

  @Test
  fun `create team fails due to missing fanta tournament`() {

    val queryParams = mapOf("id" to A_MISSING_FANTA_TOURNAMENT_ID)

    val errorMessage = "Fanta Tournament $A_MISSING_FANTA_TOURNAMENT_ID not found"
    val expectedMessage = "Error during DB operation $errorMessage"

    every { namedJdbcTemplate.queryForObject(COUNT_FANTA_TOURNAMENT_QUERY, queryParams, Long::class.java) } returns null

    assertThrowsWithMessage<FantaTeamCreationException>(expectedMessage) { repository.create(AN_OWNER_ID, A_MISSING_FANTA_TOURNAMENT_ID) }

    verify { jdbcTemplate wasNot called }
  }

  @Test
  fun `create team fails due to an error during fantaTeams creation`() {

    val queryParams = mapOf("id" to A_FANTA_TOURNAMENT_ID)

    val errorMessage = "I'm an error"
    val expectedMessage = "Error during DB operation $errorMessage"

    every { namedJdbcTemplate.queryForObject(COUNT_FANTA_TOURNAMENT_QUERY, queryParams, Long::class.java) } returns 1
    every {
      namedJdbcTemplate.update(eq(CREATE_FANTA_TEAMS_QUERY),
                               match { it.getValue("ownerId") == AN_OWNER_ID },
                               any<GeneratedKeyHolder>(),
                               eq(arrayOf("TEAM_ID")))
    } throws RuntimeException(errorMessage)

    assertThrowsWithMessage<FantaTeamCreationException>(expectedMessage) { repository.create(AN_OWNER_ID, A_FANTA_TOURNAMENT_ID) }
  }

  @Test
  fun `create team fails due to an error during fantaTournamentsTeams creation`() {

    val queryParams = mapOf("id" to A_FANTA_TOURNAMENT_ID)

    val generatedKeyHolder: GeneratedKeyHolder = mockk()

    val errorMessage = "Scary error"
    val expectedMessage = "Error during DB operation $errorMessage"

    every { namedJdbcTemplate.queryForObject(COUNT_FANTA_TOURNAMENT_QUERY, queryParams, Long::class.java) } returns 1
    every {
      namedJdbcTemplate.update(eq(CREATE_FANTA_TEAMS_QUERY),
                               match { it.getValue("ownerId") == AN_OWNER_ID },
                               any<GeneratedKeyHolder>(),
                               eq(arrayOf("TEAM_ID")))
    } returns 1
    every { generatedKeyHolder.key } returns A_TEAM_ID
    every {
      jdbcTemplate.update(CREATE_FANTA_TOURNAMENTS_TEAMS_QUERY, A_FANTA_TOURNAMENT_ID, A_TEAM_ID)
    } throws RuntimeException(errorMessage)

    assertThrowsWithMessage<FantaTeamCreationException>(expectedMessage) { repository.create(AN_OWNER_ID, A_FANTA_TOURNAMENT_ID) }
  }

  @Test
  fun `create team not work because fanta teams insert fails`() {

    val queryParams = mapOf("id" to A_FANTA_TOURNAMENT_ID)

    val expectedMessage = "Error during DB operation Insert failed or no key generated on FANTA_TEAMS"

    mockkConstructor(GeneratedKeyHolder::class)

    every { namedJdbcTemplate.queryForObject(COUNT_FANTA_TOURNAMENT_QUERY, queryParams, Long::class.java) } returns 1
    every {
      namedJdbcTemplate.update(eq(CREATE_FANTA_TEAMS_QUERY),
                               match { it.getValue("ownerId") == AN_OWNER_ID },
                               any<GeneratedKeyHolder>(),
                               eq(arrayOf("TEAM_ID")))
    } returns 0

    assertThrowsWithMessage<FantaTeamCreationException>(expectedMessage) { repository.create(AN_OWNER_ID, A_FANTA_TOURNAMENT_ID) }

    verify { jdbcTemplate wasNot called }
  }

  @Test
  fun `create team works`() {

    val queryParams = mapOf("id" to 77)

    val expected = FantaTeam(id = 42, ownerId = "AN_OWNER_ID")

    mockkConstructor(GeneratedKeyHolder::class)

    every { namedJdbcTemplate.queryForObject(COUNT_FANTA_TOURNAMENT_QUERY, queryParams, Long::class.java) } returns 1
    every {
      namedJdbcTemplate.update(eq(CREATE_FANTA_TEAMS_QUERY),
                               match { it.getValue("ownerId") == "AN_OWNER_ID" },
                               any<GeneratedKeyHolder>(),
                               eq(arrayOf("TEAM_ID")))
    } returns 1
    every { anyConstructed<GeneratedKeyHolder>().key } returns 42
    every { jdbcTemplate.update(CREATE_FANTA_TOURNAMENTS_TEAMS_QUERY, 77, 42) } returns A_FANTA_TOURNAMENTS_TEAMS_ID

    assertThat(repository.create("AN_OWNER_ID", 77)).isEqualTo(expected)
  }

  companion object {

    private const val AN_OWNER_ID = "AN_OWNER_ID"
    private const val A_MISSING_FANTA_TOURNAMENT_ID = 1
    private const val A_FANTA_TOURNAMENT_ID = 1
    private const val A_TEAM_ID = 42
    private const val A_FANTA_TOURNAMENTS_TEAMS_ID = 5

    private val COUNT_FANTA_TOURNAMENT_QUERY = """
      SELECT COUNT(*)
      FROM FANTA_TOURNAMENTS 
      WHERE FANTA_TOURNAMENT_ID = :id;
    """.trimIndent()

    private val CREATE_FANTA_TEAMS_QUERY = """
      INSERT INTO FANTA_TEAMS
      (OWNER_ID)
      VALUES(:ownerId);
    """.trimIndent()

    private val CREATE_FANTA_TOURNAMENTS_TEAMS_QUERY = """
      INSERT INTO FANTA_TOURNAMENTS_TEAMS
      (FANTA_TOURNAMENT_ID, TEAM_ID)
      VALUES(?, ?);
    """.trimIndent()
  }
}