package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.it

import com.posadeus.fantatennis.app.configuration.infrastructure.jdbc.PersistPlayersPointsRepositoryConfiguration
import com.posadeus.fantatennis.domain.exception.InvalidPlayerPointsException
import com.posadeus.fantatennis.domain.infrastructure.PersistPlayersPointsRepository
import com.posadeus.fantatennis.domain.model.AtpPlayer
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerPointsDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(IntegrationTestConfiguration::class, PersistPlayersPointsRepositoryConfiguration::class)
class JdbcPersistPlayersPointsRepositoryIT {

  @Autowired
  private lateinit var namedParameterJdbcTemplate: NamedParameterJdbcTemplate

  @Autowired
  private lateinit var repository: PersistPlayersPointsRepository

  @SqlGroup(
      Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD),
      Sql(scripts = ["/test-containers/populate-database.sql"], executionPhase = BEFORE_TEST_METHOD)
  )
  @Test
  fun `exception happens during inserts, transaction reverted`() {

    val player1 = AtpPlayer(id = "C0D1", mapOf(2026 to mapOf(2 to 10.00)))
    val player2 = AtpPlayer(id = "A0B1", mapOf(2026 to mapOf(1 to 1.00, 2 to 20.00)))
    val players = setOf(player1, player2)

    val expectedMessage = """
      Unexpected error during insert: PreparedStatementCallback; SQL [INSERT INTO PLAYERS_POINTS
      (TOURNAMENT_YEAR, TOURNAMENT_ID, PLAYER_ID, FANTA_POINTS)
      VALUES(?, ?, ?, ?);]; Duplicate entry '2026-1-A0B1' for key 'PLAYERS_POINTS.PRIMARY'
    """.trimIndent()

    assertThrowsWithMessage<InvalidPlayerPointsException>(expectedMessage) { repository.persistAll(players) }

    val query = """
      SELECT *
      FROM PLAYERS_POINTS
      WHERE PLAYER_ID IN (:playerId)
      AND TOURNAMENT_YEAR = 2026;
    """.trimIndent()

    val queryParams = mapOf("playerId" to listOf("C0D1", "A0B1"))

    assertThat(namedParameterJdbcTemplate.query(query, queryParams, playerPointsRowMapper).size).isEqualTo(2)
  }

  @SqlGroup(
      Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD),
      Sql(scripts = ["/test-containers/populate-database.sql"], executionPhase = BEFORE_TEST_METHOD)
  )
  @Test
  fun `persist success for all inputs`() {

    val player1 = AtpPlayer(id = "QR43", mapOf(2025 to mapOf(4 to 0.00), 2026 to mapOf(2 to 10.00)))
    val player2 = AtpPlayer(id = "A0B1", mapOf(2026 to mapOf(3 to 1.00, 2 to 20.00)))
    val players = setOf(player1, player2)

    repository.persistAll(players)

    val query = """
      SELECT *
      FROM PLAYERS_POINTS
      WHERE PLAYER_ID IN (:playerId);
    """.trimIndent()

    val queryParams = mapOf("playerId" to listOf("QR43", "A0B1"))

    assertThat(namedParameterJdbcTemplate.query(query, queryParams, playerPointsRowMapper).size).isEqualTo(13)
  }

  private val playerPointsRowMapper = RowMapper { rs, _ ->
    JdbcPlayerPointsDto(tournamentYear = rs.getInt("TOURNAMENT_YEAR"),
                        tournamentId = rs.getInt("TOURNAMENT_ID"),
                        playerId = rs.getString("PLAYER_ID"),
                        fantaPoints = rs.getDouble("FANTA_POINTS"))
  }

  private inline fun <reified T : Throwable> assertThrowsWithMessage(expectedMessage: String, block: () -> Unit) {

    val exception = assertThrows<T> { block() }

    assertThat(exception.message).isEqualTo(expectedMessage)
  }
}