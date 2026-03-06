package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.fantatournamentteam

import com.posadeus.fantatennis.infrastructure.assertThrowsWithMessage
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.FantaTournamentTeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTournamentTeamDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTournamentTeamDto.Companion.fantaTournamentTeamRowMapper
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcFantaTournamentTeamDto.aJdbcFantaTournamentTeamDto
import com.posadeus.fantatennis.infrastructure.repository.exception.NoInsertException
import io.mockk.*
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.*
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class JdbcFantaTournamentTeamDaoTest {

  private val jdbcTemplate: NamedParameterJdbcTemplate = mockk()

  private val dao: FantaTournamentTeamDao = JdbcFantaTournamentTeamDao(jdbcTemplate)

  @Nested
  inner class Persist {

    @Test
    fun `persist has an error`() {

      val fantaTournamentTeam = aJdbcFantaTournamentTeamDto(teamId = A_TEAM_ID, fantaTournamentId = A_FANTA_TOURNAMENT_ID)

      val params = mapOf("teamId" to A_TEAM_ID, "fantaTournamentId" to A_FANTA_TOURNAMENT_ID)

      val expectedMessage = "An error! It happens."

      every { jdbcTemplate.update(CREATE_FANTA_TOURNAMENTS_TEAMS_QUERY, params) } throws RuntimeException(expectedMessage)

      assertThrowsWithMessage<RuntimeException>(expectedMessage) { dao.persist(fantaTournamentTeam) }
    }

    @Test
    fun `persist fails`() {

      val fantaTournamentTeam = aJdbcFantaTournamentTeamDto(teamId = A_TEAM_ID, fantaTournamentId = A_FANTA_TOURNAMENT_ID)

      val params = mapOf("teamId" to A_TEAM_ID, "fantaTournamentId" to A_FANTA_TOURNAMENT_ID)

      val expectedMessage = "Insert failed on FANTA_TOURNAMENTS_TEAMS"

      every { jdbcTemplate.update(CREATE_FANTA_TOURNAMENTS_TEAMS_QUERY, params) } returns 0

      assertThrowsWithMessage<NoInsertException>(expectedMessage) { dao.persist(fantaTournamentTeam) }
    }

    @Test
    fun `persist works`() {

      val fantaTournamentTeam = aJdbcFantaTournamentTeamDto(teamId = A_TEAM_ID, fantaTournamentId = A_FANTA_TOURNAMENT_ID)

      val params = mapOf("teamId" to A_TEAM_ID, "fantaTournamentId" to A_FANTA_TOURNAMENT_ID)

      every { jdbcTemplate.update(CREATE_FANTA_TOURNAMENTS_TEAMS_QUERY, params) } returns 1

      dao.persist(fantaTournamentTeam)

      verify(exactly = 1) { jdbcTemplate.update(CREATE_FANTA_TOURNAMENTS_TEAMS_QUERY, params) }
    }
  }

  @Nested
  inner class RetrieveByFantaTournamentId {

    @Test
    fun `no results`() {

      val params = mapOf("fantaTournamentId" to A_FANTA_TOURNAMENT_ID)

      val expected = emptyList<JdbcFantaTournamentTeamDto>()

      every {
        jdbcTemplate.query(RETRIEVE_FANTA_TOURNAMENTS_TEAMS_BY_TOURNAMENT_ID_QUERY, params, fantaTournamentTeamRowMapper)
      } returns expected

      assertThat(dao.retrieveBy(A_FANTA_TOURNAMENT_ID)).isEqualTo(expected)
    }

    @Test
    fun `tournament's teams returned`() {

      val params = mapOf("fantaTournamentId" to A_FANTA_TOURNAMENT_ID)

      val fantaTournamentTeam1 = aJdbcFantaTournamentTeamDto(fantaTournamentId = A_FANTA_TOURNAMENT_ID, teamId = A_TEAM_ID)
      val fantaTournamentTeam2 = aJdbcFantaTournamentTeamDto(fantaTournamentId = A_FANTA_TOURNAMENT_ID, teamId = ANOTHER_TEAM_ID)
      val expected = listOf(fantaTournamentTeam1, fantaTournamentTeam2)

      every {
        jdbcTemplate.query(RETRIEVE_FANTA_TOURNAMENTS_TEAMS_BY_TOURNAMENT_ID_QUERY, params, fantaTournamentTeamRowMapper)
      } returns expected

      assertThat(dao.retrieveBy(A_FANTA_TOURNAMENT_ID)).isEqualTo(expected)
    }
  }

  @Nested
  inner class RetrieveByFantaTeamId {

    @Test
    fun `no results throws EmptyResultDataAccessException`() {

      val params = mapOf("teamId" to A_TEAM_ID)

      every {
        jdbcTemplate.queryForObject(RETRIEVE_FANTA_TOURNAMENTS_TEAMS_BY_TEAM_ID_QUERY, params, fantaTournamentTeamRowMapper)
      } throws EmptyResultDataAccessException(1)

      assertThrows<EmptyResultDataAccessException> { dao.retrieveByTeamId(A_TEAM_ID) }
    }

    @Test
    fun `tournament returned`() {

      val params = mapOf("teamId" to A_TEAM_ID)

      val expected = aJdbcFantaTournamentTeamDto(teamId = A_TEAM_ID)

      every { jdbcTemplate.queryForObject(RETRIEVE_FANTA_TOURNAMENTS_TEAMS_BY_TEAM_ID_QUERY, params, fantaTournamentTeamRowMapper) } returns expected

      assertThat(dao.retrieveByTeamId(A_TEAM_ID)).isEqualTo(expected)
    }
  }

  companion object {

    private const val A_TEAM_ID = 123
    private const val ANOTHER_TEAM_ID = 123
    private const val A_FANTA_TOURNAMENT_ID = 1566

    private val CREATE_FANTA_TOURNAMENTS_TEAMS_QUERY = """
      INSERT INTO FANTA_TOURNAMENTS_TEAMS
      (FANTA_TOURNAMENT_ID, TEAM_ID)
      VALUES(:fantaTournamentId, :teamId);
    """.trimIndent()

    private val RETRIEVE_FANTA_TOURNAMENTS_TEAMS_BY_TOURNAMENT_ID_QUERY = """
      SELECT *
      FROM FANTA_TOURNAMENTS_TEAMS
      WHERE FANTA_TOURNAMENT_ID = :fantaTournamentId
    """.trimIndent()

    private val RETRIEVE_FANTA_TOURNAMENTS_TEAMS_BY_TEAM_ID_QUERY = """
      SELECT *
      FROM FANTA_TOURNAMENTS_TEAMS
      WHERE TEAM_ID = :teamId
    """.trimIndent()
  }
}