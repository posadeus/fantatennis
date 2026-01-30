package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.fantatournamentteam

import com.posadeus.fantatennis.infrastructure.assertThrowsWithMessage
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.FantaTournamentTeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcFantaTournamentTeamDto.aJdbcFantaTournamentTeamDto
import com.posadeus.fantatennis.infrastructure.repository.exception.NoInsertException
import io.mockk.*
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class JdbcFantaTournamentTeamDaoTest {

  private val jdbcTemplate: NamedParameterJdbcTemplate = mockk()

  private val dao: FantaTournamentTeamDao = JdbcFantaTournamentTeamDao(jdbcTemplate)

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

  companion object {

    private const val A_TEAM_ID = 123
    private const val A_FANTA_TOURNAMENT_ID = 1566

    private val CREATE_FANTA_TOURNAMENTS_TEAMS_QUERY = """
      INSERT INTO FANTA_TOURNAMENTS_TEAMS
      (FANTA_TOURNAMENT_ID, TEAM_ID)
      VALUES(:fantaTournamentId, :teamId);
    """.trimIndent()
  }
}