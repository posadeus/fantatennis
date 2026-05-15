package com.posadeus.fantatennis.infrastructure.repository.database.it

import com.posadeus.fantatennis.app.configuration.infrastructure.PersistTeamPlayersRepositoryConfiguration
import com.posadeus.fantatennis.app.configuration.infrastructure.jdbc.dao.TeamDaoConfiguration
import com.posadeus.fantatennis.domain.exception.InvalidAddPlayersException
import com.posadeus.fantatennis.domain.infrastructure.PersistTeamPlayersRepository
import com.posadeus.fantatennis.infrastructure.assertThrowsWithMessage
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTeamDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.it.IntegrationTestConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(IntegrationTestConfiguration::class, PersistTeamPlayersRepositoryConfiguration::class, TeamDaoConfiguration::class)
@TestPropertySource(properties = [
  "caches.caffeine.team-cache.expire-after-write-duration=10080",
  "caches.caffeine.team-cache.maximum-size=1000"
])
class SqlPersistTeamPlayersRepositoryIT {

  @Autowired
  private lateinit var namedParameterJdbcTemplate: NamedParameterJdbcTemplate

  @Autowired
  private lateinit var repository: PersistTeamPlayersRepository

  @SqlGroup(
      Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD),
      Sql(scripts = ["/test-containers/populate-database.sql"], executionPhase = BEFORE_TEST_METHOD)
  )
  @Test
  fun `exception happens during inserts, transaction reverted`() {

    val playerIds = setOf("C0D1", "A0B1")

    val query = """
      SELECT *
      FROM TEAMS
      WHERE TEAM_ID = :teamId
      AND PLAYER_ID = :playerId;
    """.trimIndent()

    val expectedMessage = """
      Unexpected error during insert: PreparedStatementCallback; SQL [INSERT INTO TEAMS
      (TEAM_ID, PLAYER_ID, STARTING_TOURNAMENT)
      VALUES(?, ?, ?);]; Duplicate entry '1-A0B1-1' for key 'TEAMS.PRIMARY' - Operation reverted.
    """.trimIndent()

    assertThrowsWithMessage<InvalidAddPlayersException>(expectedMessage) { repository.persist(A_TEAM_ID, playerIds, A_TOURNAMENT_ID) }

    val queryParams = mapOf("teamId" to 1, "playerId" to "C0D1")

    assertThat(namedParameterJdbcTemplate.query(query, queryParams, teamRowMapper).size).isEqualTo(0)
  }

  @SqlGroup(
      Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD),
      Sql(scripts = ["/test-containers/populate-database.sql"], executionPhase = BEFORE_TEST_METHOD)
  )
  @Test
  fun `players are all found and added to the team`() {

    val playerIds = setOf("E2F8", "C0D1")

    assertThat(repository.persist(A_TEAM_ID, playerIds, A_TOURNAMENT_ID)).isEqualTo(Unit)
  }

  private val teamRowMapper = RowMapper { rs, _ ->
    JdbcTeamDto(teamId = rs.getInt("TEAM_ID"),
                playerId = rs.getString("PLAYER_ID"),
                startingTournamentId = rs.getInt("STARTING_TOURNAMENT"),
                endingTournamentId = rs.getInt("ENDING_TOURNAMENT"))
  }

  companion object {

    private const val A_TEAM_ID = 1
    private const val A_TOURNAMENT_ID = 1
  }
}