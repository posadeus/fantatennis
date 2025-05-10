package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.exception.InvalidAddPlayersException
import com.posadeus.fantatennis.domain.infrastructure.AddPlayersToTeamRepository
import com.posadeus.fantatennis.domain.model.AddPlayers.InvalidAddPlayers.*
import com.posadeus.fantatennis.domain.model.AddPlayers.ValidAddPlayers
import com.posadeus.fantatennis.domain.model.DomainPlayer
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.*
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcPlayerDto.aJdbcPlayerDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcTournamentDto.aJdbcTournamentDto
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class JdbcAddPlayersToTeamRepositoryTest {

  private val jdbcTemplate: NamedParameterJdbcTemplate = mockk()

  private val repository: AddPlayersToTeamRepository = JdbcAddPlayersToTeamRepository(jdbcTemplate)

  @Test
  fun `fanta team not found`() {

    val fantaTeamsQueryParams = mapOf("teamId" to 1)

    val expected = AddPlayersTeamNotFound

    every {
      jdbcTemplate.queryForObject(RETRIEVE_FANTA_TEAM_QUERY, fantaTeamsQueryParams, any<RowMapper<FantaTeamDto>>())
    } throws EmptyResultDataAccessException(1)

    assertThat(repository.add(1, setOf(A_PLAYER_ID, ANOTHER_PLAYER_ID), A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  @Test
  fun `tournament not found`() {

    val fantaTeamsQueryParams = mapOf("teamId" to A_TEAM_ID)
    val fantaTeam = FantaTeamDto(teamId = A_TEAM_ID, ownerId = AN_OWNER_ID)
    val tournamentQueryParams = mapOf("tournamentId" to 1234)

    val expected = AddPlayersTournamentNotFound

    every {
      jdbcTemplate.queryForObject(RETRIEVE_FANTA_TEAM_QUERY, fantaTeamsQueryParams, any<RowMapper<FantaTeamDto>>())
    } returns fantaTeam
    every {
      jdbcTemplate.queryForObject(RETRIEVE_TOURNAMENT_QUERY, tournamentQueryParams, any<RowMapper<JdbcTournamentDto>>())
    } throws EmptyResultDataAccessException(1)

    assertThat(repository.add(A_TEAM_ID, setOf(A_PLAYER_ID, ANOTHER_PLAYER_ID), 1234)).isEqualTo(expected)
  }

  @Test
  fun `not all players found`() {

    val playerIds = setOf("A_PLAYER_ID", "ANOTHER_PLAYER_ID")

    val fantaTeamsQueryParams = mapOf("teamId" to A_TEAM_ID)
    val fantaTeam = FantaTeamDto(teamId = A_TEAM_ID, ownerId = AN_OWNER_ID)
    val tournamentQueryParams = mapOf("tournamentId" to A_TOURNAMENT_ID)
    val tournamentDto = aJdbcTournamentDto(tournamentId = A_TOURNAMENT_ID)
    val playersQueryParams = mapOf("playerIds" to playerIds)
    val playerDto = aJdbcPlayerDto(playerId = "A_PLAYER_ID")

    val expected = PlayersNotFound(missingPlayerIds = setOf("ANOTHER_PLAYER_ID"))

    every {
      jdbcTemplate.queryForObject(RETRIEVE_FANTA_TEAM_QUERY, fantaTeamsQueryParams, any<RowMapper<FantaTeamDto>>())
    } returns fantaTeam
    every {
      jdbcTemplate.queryForObject(RETRIEVE_TOURNAMENT_QUERY, tournamentQueryParams, any<RowMapper<JdbcTournamentDto>>())
    } returns tournamentDto
    every {
      jdbcTemplate.query(RETRIEVE_PLAYERS_QUERY, playersQueryParams, any<RowMapper<JdbcPlayerDto>>())
    } returns listOf(playerDto)

    assertThat(repository.add(A_TEAM_ID, playerIds, A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  @Test
  fun `not all players have been added to the team`() {

    val playerIds = setOf(A_PLAYER_ID, ANOTHER_PLAYER_ID)

    val fantaTeamsQueryParams = mapOf("teamId" to A_TEAM_ID)
    val fantaTeam = FantaTeamDto(teamId = A_TEAM_ID, ownerId = AN_OWNER_ID)
    val tournamentQueryParams = mapOf("tournamentId" to A_TOURNAMENT_ID)
    val tournamentDto = aJdbcTournamentDto(tournamentId = A_TOURNAMENT_ID)
    val playersQueryParams = mapOf("playerIds" to playerIds)
    val aPlayerDto = aJdbcPlayerDto(playerId = A_PLAYER_ID)
    val anotherPlayerDto = aJdbcPlayerDto(playerId = ANOTHER_PLAYER_ID)

    val batchElement1 = mapOf("teamId" to A_TEAM_ID, "playerId" to A_PLAYER_ID, "startingTournamentId" to A_TOURNAMENT_ID)
    val batchElement2 = mapOf("teamId" to A_TEAM_ID, "playerId" to ANOTHER_PLAYER_ID, "startingTournamentId" to A_TOURNAMENT_ID)
    val paramSource = arrayOf(batchElement1, batchElement2)

    val expectedMessage = "Unexpected error during insert: Players [ANOTHER_PLAYER_ID] not inserted, operation reverted."

    every {
      jdbcTemplate.queryForObject(RETRIEVE_FANTA_TEAM_QUERY, fantaTeamsQueryParams, any<RowMapper<FantaTeamDto>>())
    } returns fantaTeam
    every {
      jdbcTemplate.queryForObject(RETRIEVE_TOURNAMENT_QUERY, tournamentQueryParams, any<RowMapper<JdbcTournamentDto>>())
    } returns tournamentDto
    every {
      jdbcTemplate.query(RETRIEVE_PLAYERS_QUERY, playersQueryParams, any<RowMapper<JdbcPlayerDto>>())
    } returns listOf(aPlayerDto, anotherPlayerDto)
    every { jdbcTemplate.batchUpdate(INSERT_PLAYERS_QUERY, paramSource) } returns intArrayOf(1, 0)

    assertThrowsWithMessage<InvalidAddPlayersException>(expectedMessage) { repository.add(A_TEAM_ID, playerIds, A_TOURNAMENT_ID) }
  }

  @Test
  fun `exception happens during inserts`() {

    val playerIds = setOf(A_PLAYER_ID, ANOTHER_PLAYER_ID)

    val fantaTeamsQueryParams = mapOf("teamId" to A_TEAM_ID)
    val fantaTeam = FantaTeamDto(teamId = A_TEAM_ID, ownerId = AN_OWNER_ID)
    val tournamentQueryParams = mapOf("tournamentId" to A_TOURNAMENT_ID)
    val tournamentDto = aJdbcTournamentDto(tournamentId = A_TOURNAMENT_ID)
    val playersQueryParams = mapOf("playerIds" to playerIds)
    val aPlayerDto = aJdbcPlayerDto(playerId = A_PLAYER_ID)
    val anotherPlayerDto = aJdbcPlayerDto(playerId = ANOTHER_PLAYER_ID)

    val batchElement1 = mapOf("teamId" to A_TEAM_ID, "playerId" to A_PLAYER_ID, "startingTournamentId" to A_TOURNAMENT_ID)
    val batchElement2 = mapOf("teamId" to A_TEAM_ID, "playerId" to ANOTHER_PLAYER_ID, "startingTournamentId" to A_TOURNAMENT_ID)
    val paramSource = arrayOf(batchElement1, batchElement2)

    val expectedMessage = "Unexpected error during insert: Error"

    every {
      jdbcTemplate.queryForObject(RETRIEVE_FANTA_TEAM_QUERY, fantaTeamsQueryParams, any<RowMapper<FantaTeamDto>>())
    } returns fantaTeam
    every {
      jdbcTemplate.queryForObject(RETRIEVE_TOURNAMENT_QUERY, tournamentQueryParams, any<RowMapper<JdbcTournamentDto>>())
    } returns tournamentDto
    every {
      jdbcTemplate.query(RETRIEVE_PLAYERS_QUERY, playersQueryParams, any<RowMapper<JdbcPlayerDto>>())
    } returns listOf(aPlayerDto, anotherPlayerDto)
    every { jdbcTemplate.batchUpdate(INSERT_PLAYERS_QUERY, paramSource) } throws RuntimeException("Error")

    assertThrowsWithMessage<InvalidAddPlayersException>(expectedMessage) { repository.add(A_TEAM_ID, playerIds, A_TOURNAMENT_ID) }
  }

  @Test
  fun `players are all found and added to the team`() {

    val playerIds = setOf(A_PLAYER_ID, ANOTHER_PLAYER_ID)

    val fantaTeamsQueryParams = mapOf("teamId" to A_TEAM_ID)
    val fantaTeam = FantaTeamDto(teamId = A_TEAM_ID, ownerId = AN_OWNER_ID)
    val tournamentQueryParams = mapOf("tournamentId" to A_TOURNAMENT_ID)
    val tournamentDto = aJdbcTournamentDto(tournamentId = A_TOURNAMENT_ID)
    val playersQueryParams = mapOf("playerIds" to playerIds)
    val aPlayerDto = aJdbcPlayerDto(playerId = A_PLAYER_ID,
                                    atpTourId = AN_ATP_PLAYER_ID,
                                    fullName = A_PLAYER_FULL_NAME)
    val anotherPlayerDto = aJdbcPlayerDto(playerId = ANOTHER_PLAYER_ID,
                                          atpTourId = ANOTHER_ATP_PLAYER_ID,
                                          fullName = ANOTHER_PLAYER_FULL_NAME)

    val batchElement1 = mapOf("teamId" to A_TEAM_ID, "playerId" to A_PLAYER_ID, "startingTournamentId" to A_TOURNAMENT_ID)
    val batchElement2 = mapOf("teamId" to A_TEAM_ID, "playerId" to ANOTHER_PLAYER_ID, "startingTournamentId" to A_TOURNAMENT_ID)
    val paramSource = arrayOf(batchElement1, batchElement2)

    val aDomainPlayer = DomainPlayer(id = A_PLAYER_ID,
                                     atpId = AN_ATP_PLAYER_ID,
                                     fullName = A_PLAYER_FULL_NAME)
    val anotherDomainPlayer = DomainPlayer(id = ANOTHER_PLAYER_ID,
                                           atpId = ANOTHER_ATP_PLAYER_ID,
                                           fullName = ANOTHER_PLAYER_FULL_NAME)
    val expected = ValidAddPlayers(players = setOf(aDomainPlayer, anotherDomainPlayer))

    every {
      jdbcTemplate.queryForObject(RETRIEVE_FANTA_TEAM_QUERY, fantaTeamsQueryParams, any<RowMapper<FantaTeamDto>>())
    } returns fantaTeam
    every {
      jdbcTemplate.queryForObject(RETRIEVE_TOURNAMENT_QUERY, tournamentQueryParams, any<RowMapper<JdbcTournamentDto>>())
    } returns tournamentDto
    every {
      jdbcTemplate.query(RETRIEVE_PLAYERS_QUERY, playersQueryParams, any<RowMapper<JdbcPlayerDto>>())
    } returns listOf(aPlayerDto, anotherPlayerDto)
    every { jdbcTemplate.batchUpdate(INSERT_PLAYERS_QUERY, paramSource) } returns intArrayOf(1, 1)

    assertThat(repository.add(A_TEAM_ID, playerIds, A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  private inline fun <reified T : Throwable> assertThrowsWithMessage(expectedMessage: String, block: () -> Unit) {

    val exception = assertThrows<T> { block() }

    assertThat(exception.message).isEqualTo(expectedMessage)
  }

  companion object {

    private const val A_PLAYER_ID = "A_PLAYER_ID"
    private const val ANOTHER_PLAYER_ID = "ANOTHER_PLAYER_ID"
    private const val AN_OWNER_ID = "AN_OWNER_ID"
    private const val AN_ATP_PLAYER_ID = "AN_ATP_PLAYER_ID"
    private const val ANOTHER_ATP_PLAYER_ID = "ANOTHER_ATP_PLAYER_ID"
    private const val A_PLAYER_FULL_NAME = "A_PLAYER_FULL_NAME"
    private const val ANOTHER_PLAYER_FULL_NAME = "ANOTHER_PLAYER_FULL_NAME"
    private const val A_TEAM_ID = 1
    private const val A_TOURNAMENT_ID = 1234

    private val RETRIEVE_FANTA_TEAM_QUERY = """
      SELECT *
      FROM FANTA_TEAMS
      WHERE TEAM_ID = :teamId;
    """.trimIndent()

    private val RETRIEVE_TOURNAMENT_QUERY = """
      SELECT *
      FROM TOURNAMENTS 
      WHERE TOURNAMENT_ID = :tournamentId;
    """.trimIndent()

    private val RETRIEVE_PLAYERS_QUERY = """
      SELECT *
      FROM PLAYERS 
      WHERE PLAYER_ID IN :playerIds;
    """.trimIndent()

    private val INSERT_PLAYERS_QUERY = """
      INSERT INTO TEAMS
      (TEAM_ID, PLAYER_ID, STARTING_TOURNAMENT)
      VALUES(:teamId, :playerId, :startingTournamentId);
    """.trimIndent()
  }
}