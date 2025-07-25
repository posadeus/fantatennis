package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.it

import com.posadeus.fantatennis.app.configuration.infrastructure.jdbc.PersistPlayersRepositoryConfiguration
import com.posadeus.fantatennis.domain.exception.InvalidPlayerException
import com.posadeus.fantatennis.domain.infrastructure.PersistPlayersRepository
import com.posadeus.fantatennis.domain.model.DomainPlayer
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerDto
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
@Import(IntegrationTestConfiguration::class, PersistPlayersRepositoryConfiguration::class)
class JdbcPersistPlayersRepositoryIT {

  @Autowired
  private lateinit var namedParameterJdbcTemplate: NamedParameterJdbcTemplate

  @Autowired
  private lateinit var repository: PersistPlayersRepository

  @SqlGroup(
      Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD),
      Sql(scripts = ["/test-containers/populate-database.sql"], executionPhase = BEFORE_TEST_METHOD)
  )
  @Test
  fun `exception happens during inserts, transaction reverted`() {

    val player1 = DomainPlayer(id = "AAAA", atpId = "AAAA", fullName = A_FULL_NAME)
    val player2 = DomainPlayer(id = "A0B1", atpId = "A0B1", fullName = ANOTHER_FULL_NAME)
    val players = setOf(player1, player2)

    val expectedMessage = """
      Unexpected error during insert: PreparedStatementCallback; SQL [INSERT INTO PLAYERS
      (PLAYER_ID, ATP_TOUR_ID, FULL_NAME)
      VALUES(?, ?, ?);]; Duplicate entry 'A0B1' for key 'PLAYERS.PRIMARY'
    """.trimIndent()

    assertThrowsWithMessage<InvalidPlayerException>(expectedMessage) { repository.persistAll(players) }

    val query = """
      SELECT *
      FROM PLAYERS
      WHERE PLAYER_ID IN (:playerId);
    """.trimIndent()

    val queryParams = mapOf("playerId" to listOf(A_PLAYER_ID))

    assertThat(namedParameterJdbcTemplate.query(query, queryParams, playerRowMapper).size).isEqualTo(0)
  }

  @SqlGroup(
      Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD),
      Sql(scripts = ["/test-containers/populate-database.sql"], executionPhase = BEFORE_TEST_METHOD)
  )
  @Test
  fun `persist success for all inputs`() {

    val player1 = DomainPlayer(id = A_PLAYER_ID, atpId = AN_ATP_PLAYER_ID, fullName = A_FULL_NAME)
    val player2 = DomainPlayer(id = ANOTHER_PLAYER_ID, atpId = ANOTHER_ATP_PLAYER_ID, fullName = ANOTHER_FULL_NAME)
    val players = setOf(player1, player2)

    repository.persistAll(players)

    val query = """
      SELECT *
      FROM PLAYERS
      WHERE PLAYER_ID IN (:playerId);
    """.trimIndent()

    val queryParams = mapOf("playerId" to listOf(A_PLAYER_ID, ANOTHER_PLAYER_ID))

    assertThat(namedParameterJdbcTemplate.query(query, queryParams, playerRowMapper).size).isEqualTo(2)
  }

  private val playerRowMapper = RowMapper { rs, _ ->
    JdbcPlayerDto(playerId = rs.getString("PLAYER_ID"),
                  atpTourId = rs.getString("ATP_TOUR_ID"),
                  fullName = rs.getString("FULL_NAME"))
  }

  private inline fun <reified T : Throwable> assertThrowsWithMessage(expectedMessage: String, block: () -> Unit) {

    val exception = assertThrows<T> { block() }

    assertThat(exception.message).isEqualTo(expectedMessage)
  }

  companion object {

    private const val A_PLAYER_ID = "AAAA"
    private const val AN_ATP_PLAYER_ID = "AAAA"
    private const val A_FULL_NAME = "A_FULL_NAME"
    private const val ANOTHER_PLAYER_ID = "BBBB"
    private const val ANOTHER_ATP_PLAYER_ID = "BBBB"
    private const val ANOTHER_FULL_NAME = "ANOTHER_FULL_NAME"
  }
}