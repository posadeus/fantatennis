package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.it

import com.posadeus.fantatennis.app.configuration.infrastructure.jdbc.AddPlayersToTeamRepositoryConfiguration
import com.posadeus.fantatennis.domain.exception.InvalidAddPlayersException
import com.posadeus.fantatennis.domain.infrastructure.AddPlayersToTeamRepository
import com.posadeus.fantatennis.domain.model.AddPlayers.InvalidAddPlayers.*
import com.posadeus.fantatennis.domain.model.AddPlayers.ValidAddPlayers
import com.posadeus.fantatennis.domain.model.DomainPlayer
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTeamDto
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
@Import(IntegrationTestConfiguration::class, AddPlayersToTeamRepositoryConfiguration::class)
class JdbcAddPlayersToTeamRepositoryIT {

  @Autowired
  private lateinit var namedParameterJdbcTemplate: NamedParameterJdbcTemplate

  @Autowired
  private lateinit var repository: AddPlayersToTeamRepository

  @Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD)
  @Test
  fun `fanta team not found`() {

    val expected = AddPlayersTeamNotFound

    assertThat(repository.add(1, setOf(A_PLAYER_ID, ANOTHER_PLAYER_ID), A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  @SqlGroup(
      Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD),
      Sql(scripts = ["/test-containers/populate-database.sql"], executionPhase = BEFORE_TEST_METHOD)
  )
  @Test
  fun `tournament not found`() {

    val expected = AddPlayersTournamentNotFound

    assertThat(repository.add(A_TEAM_ID, setOf(A_PLAYER_ID, ANOTHER_PLAYER_ID), 1234)).isEqualTo(expected)
  }

  @SqlGroup(
      Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD),
      Sql(scripts = ["/test-containers/populate-database.sql"], executionPhase = BEFORE_TEST_METHOD)
  )
  @Test
  fun `not all players found`() {

    val expected = PlayersNotFound(missingPlayerIds = setOf("A_MISSING_PLAYER_ID"))

    assertThat(repository.add(A_TEAM_ID, setOf("A_MISSING_PLAYER_ID"), A_TOURNAMENT_ID)).isEqualTo(expected)
  }

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
      VALUES(?, ?, ?);]; Duplicate entry '1-A0B1-1' for key 'TEAMS.PRIMARY'
    """.trimIndent()

    assertThrowsWithMessage<InvalidAddPlayersException>(expectedMessage) { repository.add(A_TEAM_ID, playerIds, A_TOURNAMENT_ID) }

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

    val aDomainPlayer = DomainPlayer(id = "E2F8", atpId = "E2F8", fullName = "EEE FFF")
    val anotherDomainPlayer = DomainPlayer(id = "C0D1", atpId = "C0D1", fullName = "CCC DDD")

    val expected = ValidAddPlayers(players = setOf(aDomainPlayer, anotherDomainPlayer))

    assertThat(repository.add(A_TEAM_ID, playerIds, A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  private inline fun <reified T : Throwable> assertThrowsWithMessage(expectedMessage: String, block: () -> Unit) {

    val exception = assertThrows<T> { block() }

    assertThat(exception.message).isEqualTo(expectedMessage)
  }

  private val teamRowMapper = RowMapper { rs, _ ->
    JdbcTeamDto(teamId = rs.getInt("TEAM_ID"),
                playerId = rs.getString("PLAYER_ID"),
                startingTournamentId = rs.getInt("STARTING_TOURNAMENT"),
                endingTournamentId = rs.getInt("ENDING_TOURNAMENT"))
  }

  companion object {

    private const val A_PLAYER_ID = "A_PLAYER_ID"
    private const val ANOTHER_PLAYER_ID = "ANOTHER_PLAYER_ID"
    private const val A_TEAM_ID = 1
    private const val A_TOURNAMENT_ID = 1
  }
}