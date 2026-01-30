package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.fantateam

import com.posadeus.fantatennis.infrastructure.assertThrowsWithMessage
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.FantaTeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTeamDto.Companion.fantaTeamRowMapper
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcFantaTeamDto.aJdbcFantaTeamDto
import com.posadeus.fantatennis.infrastructure.repository.exception.NoInsertException
import io.mockk.*
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder

class JdbcFantaTeamDaoTest {

  private val jdbcTemplate: NamedParameterJdbcTemplate = mockk()

  private val dao: FantaTeamDao = JdbcFantaTeamDao(jdbcTemplate)

  @Nested
  inner class Retrieve {

    @Test
    fun `error on repository operation`() {

      val params = mapOf("teamId" to A_TEAM_ID)

      val expectedMessage = "Ops, I need help!"

      every { jdbcTemplate.queryForObject(RETRIEVE_FANTA_TEAM_QUERY, params, fantaTeamRowMapper) } throws RuntimeException(expectedMessage)

      assertThrowsWithMessage<RuntimeException>(expectedMessage) { dao.retrieveBy(A_TEAM_ID) }
    }

    @Test
    fun `no results returned by the query`() {

      val params = mapOf("teamId" to A_TEAM_ID)

      val expectedMessage = "No fanta team found with id $A_TEAM_ID"

      every { jdbcTemplate.queryForObject(RETRIEVE_FANTA_TEAM_QUERY, params, fantaTeamRowMapper) } returns null

      assertThrowsWithMessage<EmptyResultDataAccessException>(expectedMessage) { dao.retrieveBy(A_TEAM_ID) }
    }

    @Test
    fun `results retrieved successfully`() {

      val params = mapOf("teamId" to A_TEAM_ID)

      val expected = aJdbcFantaTeamDto()

      every { jdbcTemplate.queryForObject(RETRIEVE_FANTA_TEAM_QUERY, params, fantaTeamRowMapper) } returns expected

      assertThat(dao.retrieveBy(A_TEAM_ID)).isEqualTo(expected)
    }
  }

  @Nested
  inner class Persist {

    @Test
    fun `error during persist`() {

      val generatedKeyHolder: GeneratedKeyHolder = mockk()

      val expectedMessage = "Scary error"

      every {
        jdbcTemplate.update(eq(CREATE_FANTA_TEAMS_QUERY),
                            match { it.getValue("ownerId") == AN_OWNER_ID },
                            any<GeneratedKeyHolder>(),
                            eq(arrayOf("TEAM_ID")))
      } throws RuntimeException(expectedMessage)
      every { generatedKeyHolder.key } returns A_TEAM_ID

      assertThrowsWithMessage<RuntimeException>(expectedMessage) { dao.persist(AN_OWNER_ID) }
    }

    @Test
    fun `persist fails`() {

      val expectedMessage = "Insert failed or no key generated on FANTA_TEAMS"

      mockkConstructor(GeneratedKeyHolder::class)

      every {
        jdbcTemplate.update(eq(CREATE_FANTA_TEAMS_QUERY),
                            match { it.getValue("ownerId") == AN_OWNER_ID },
                            any<GeneratedKeyHolder>(),
                            eq(arrayOf("TEAM_ID")))
      } returns 0

      assertThrowsWithMessage<NoInsertException>(expectedMessage) { dao.persist(AN_OWNER_ID) }
    }

    @Test
    fun `persist works`() {

      val expected = 42

      mockkConstructor(GeneratedKeyHolder::class)

      every {
        jdbcTemplate.update(eq(CREATE_FANTA_TEAMS_QUERY),
                            match { it.getValue("ownerId") == "AN_OWNER_ID" },
                            any<GeneratedKeyHolder>(),
                            eq(arrayOf("TEAM_ID")))
      } returns 1
      every { anyConstructed<GeneratedKeyHolder>().key } returns expected

      assertThat(dao.persist("AN_OWNER_ID")).isEqualTo(expected)
    }
  }

  companion object {

    private const val A_TEAM_ID = 123
    private const val AN_OWNER_ID = "AN_OWNER_ID"

    private val RETRIEVE_FANTA_TEAM_QUERY = """
      SELECT *
      FROM FANTA_TEAMS
      WHERE TEAM_ID = :teamId;
    """.trimIndent()

    private val CREATE_FANTA_TEAMS_QUERY = """
      INSERT INTO FANTA_TEAMS
      (OWNER_ID)
      VALUES(:ownerId);
    """.trimIndent()
  }
}