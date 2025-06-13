package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.controller.model.team.*
import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTeamRepository
import com.posadeus.fantatennis.domain.infrastructure.SwapPlayersRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.*
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcPlayerDto.aJdbcPlayerDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcTeamDto.aJdbcTeamDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcTournamentDto.aJdbcTournamentDto
import io.mockk.*
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class JdbcSwapPlayersRepositoryTest {

  private val retrieveFantaTeamRepository: RetrieveFantaTeamRepository = mockk()
  private val jdbcTemplate: JdbcTemplate = mockk()
  private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate = mockk()

  private val repository: SwapPlayersRepository = JdbcSwapPlayersRepository(retrieveFantaTeamRepository,
                                                                            jdbcTemplate,
                                                                            namedParameterJdbcTemplate)

  @Test
  fun `swap fails due to team not found`() {

    val expected = TeamIdNotFoundTeam

    every { retrieveFantaTeamRepository.retrieve(A_NOT_EXISTING_TEAM_ID) } returns TeamIdNotFoundTeam

    assertThat(repository.swap(A_NOT_EXISTING_TEAM_ID, ANY_SWAP_PLAYERS)).isEqualTo(expected)

    verify { jdbcTemplate wasNot called }
  }

  @Test
  fun `swap fails due to players not found, empty set returned`() {

    val playerToRemoveIds = setOf(AN_OLD_PLAYER_ID, ANOTHER_OLD_PLAYER_ID)
    val playersToRemoveDto = PlayersToRemoveDto(playerIds = playerToRemoveIds, endingTournamentId = A_TOURNAMENT_ID)
    val playerToAddIds = setOf(A_NEW_PLAYER_ID, ANOTHER_NEW_PLAYER_ID)
    val playersToAddDto = PlayersToAddDto(playerIds = playerToAddIds, startingTournamentId = ANOTHER_TOURNAMENT_ID)
    val playersToSwap = PlayersToSwapDto(remove = playersToRemoveDto, add = playersToAddDto)

    val oldTeamDto = TeamDto(players = listOf(TeamPlayerDto(fullName = A_PLAYER_FULL_NAME, fantaPoints = 22.0),
                                              TeamPlayerDto(fullName = AN_OLD_PLAYER_FULL_NAME, fantaPoints = 10.0),
                                              TeamPlayerDto(fullName = ANOTHER_OLD_PLAYER_FULL_NAME, fantaPoints = 8.0)),
                             totalScore = 40.0,
                             owner = AN_OWNER)

    val expected = ErrorTeam

    every { retrieveFantaTeamRepository.retrieve(A_TEAM_ID) } returns FoundTeam(oldTeamDto)
    every { jdbcTemplate.query(RETRIEVE_ALL_PLAYERS_QUERY, any<RowMapper<JdbcPlayerDto>>()) } returns emptyList()

    assertThat(repository.swap(A_TEAM_ID, playersToSwap)).isEqualTo(expected)
  }

  @Test
  fun `swap fails due to one or more players not found`() {

    val playerToRemoveIds = setOf(AN_OLD_PLAYER_ID, ANOTHER_OLD_PLAYER_ID)
    val playersToRemoveDto = PlayersToRemoveDto(playerIds = playerToRemoveIds, endingTournamentId = A_TOURNAMENT_ID)
    val playerToAddIds = setOf(A_NEW_PLAYER_ID, ANOTHER_NEW_PLAYER_ID)
    val playersToAddDto = PlayersToAddDto(playerIds = playerToAddIds, startingTournamentId = ANOTHER_TOURNAMENT_ID)
    val playersToSwap = PlayersToSwapDto(remove = playersToRemoveDto, add = playersToAddDto)

    val oldTeamDto = TeamDto(players = listOf(TeamPlayerDto(fullName = A_PLAYER_FULL_NAME, fantaPoints = 22.0),
                                              TeamPlayerDto(fullName = AN_OLD_PLAYER_FULL_NAME, fantaPoints = 10.0),
                                              TeamPlayerDto(fullName = ANOTHER_OLD_PLAYER_FULL_NAME, fantaPoints = 8.0)),
                             totalScore = 40.0,
                             owner = AN_OWNER)
    val anOldPlayer = aJdbcPlayerDto(playerId = AN_OLD_PLAYER_ID, fullName = AN_OLD_PLAYER_FULL_NAME)
    val anotherOldPlayer = aJdbcPlayerDto(playerId = ANOTHER_OLD_PLAYER_ID, fullName = ANOTHER_OLD_PLAYER_FULL_NAME)
    val aNewPlayer = aJdbcPlayerDto(playerId = A_NEW_PLAYER_ID, fullName = A_NEW_PLAYER_FULL_NAME)
    val notAllPlayersFound = listOf(A_PLAYER, anOldPlayer, anotherOldPlayer, aNewPlayer)

    val expected = ErrorTeam

    every { retrieveFantaTeamRepository.retrieve(A_TEAM_ID) } returns FoundTeam(oldTeamDto)
    every { jdbcTemplate.query(RETRIEVE_ALL_PLAYERS_QUERY, any<RowMapper<JdbcPlayerDto>>()) } returns notAllPlayersFound

    assertThat(repository.swap(A_TEAM_ID, playersToSwap)).isEqualTo(expected)
  }

  @Test
  fun `swap fails due to one or more tournaments not found`() {

    val playerToRemoveIds = setOf(AN_OLD_PLAYER_ID)
    val playersToRemoveDto = PlayersToRemoveDto(playerIds = playerToRemoveIds, endingTournamentId = A_TOURNAMENT_ID)
    val playerToAddIds = setOf(A_NEW_PLAYER_ID)
    val playersToAddDto = PlayersToAddDto(playerIds = playerToAddIds, startingTournamentId = ANOTHER_TOURNAMENT_ID)
    val playersToSwap = PlayersToSwapDto(remove = playersToRemoveDto, add = playersToAddDto)

    val oldTeamDto = TeamDto(players = listOf(TeamPlayerDto(fullName = A_PLAYER_FULL_NAME, fantaPoints = 22.0),
                                              TeamPlayerDto(fullName = AN_OLD_PLAYER_FULL_NAME, fantaPoints = 10.0)),
                             totalScore = 40.0,
                             owner = AN_OWNER)
    val aPlayer = aJdbcPlayerDto(playerId = A_PLAYER_ID, fullName = A_PLAYER_FULL_NAME)
    val anOldPlayer = aJdbcPlayerDto(playerId = AN_OLD_PLAYER_ID, fullName = AN_OLD_PLAYER_FULL_NAME)
    val aNewPlayer = aJdbcPlayerDto(playerId = A_NEW_PLAYER_ID, fullName = A_NEW_PLAYER_FULL_NAME)
    val allPlayers = listOf(aPlayer, anOldPlayer, aNewPlayer)
    val endingTournament = aJdbcTournamentDto(tournamentId = A_TOURNAMENT_ID)
    val notAllTournamentsFound = listOf(endingTournament, A_TOURNAMENT)

    val expected = ErrorTeam

    every { retrieveFantaTeamRepository.retrieve(A_TEAM_ID) } returns FoundTeam(oldTeamDto)
    every { jdbcTemplate.query(RETRIEVE_ALL_PLAYERS_QUERY, any<RowMapper<JdbcPlayerDto>>()) } returns allPlayers
    every { jdbcTemplate.query(RETRIEVE_ALL_TOURNAMENTS_QUERY, any<RowMapper<JdbcTournamentDto>>()) } returns notAllTournamentsFound

    assertThat(repository.swap(A_TEAM_ID, playersToSwap)).isEqualTo(expected)
  }

  @Test
  fun `swap fails due to fanta team not found`() {

    val playerToRemoveIds = setOf(AN_OLD_PLAYER_ID, ANOTHER_OLD_PLAYER_ID)
    val playersToRemoveDto = PlayersToRemoveDto(playerIds = playerToRemoveIds, endingTournamentId = A_TOURNAMENT_ID)
    val playerToAddIds = setOf(A_NEW_PLAYER_ID, ANOTHER_NEW_PLAYER_ID)
    val playersToAddDto = PlayersToAddDto(playerIds = playerToAddIds, startingTournamentId = ANOTHER_TOURNAMENT_ID)
    val playersToSwap = PlayersToSwapDto(remove = playersToRemoveDto, add = playersToAddDto)

    val oldTeamDto = TeamDto(players = listOf(TeamPlayerDto(fullName = A_PLAYER_FULL_NAME, fantaPoints = 22.0),
                                              TeamPlayerDto(fullName = AN_OLD_PLAYER_FULL_NAME, fantaPoints = 10.0)),
                             totalScore = 32.0,
                             owner = AN_OWNER)
    val aPlayer = aJdbcPlayerDto(playerId = A_PLAYER_ID, fullName = A_PLAYER_FULL_NAME)
    val anOldPlayer = aJdbcPlayerDto(playerId = AN_OLD_PLAYER_ID, fullName = AN_OLD_PLAYER_FULL_NAME)
    val aNewPlayer = aJdbcPlayerDto(playerId = A_NEW_PLAYER_ID, fullName = A_NEW_PLAYER_FULL_NAME)
    val anotherOldPlayer = aJdbcPlayerDto(playerId = ANOTHER_OLD_PLAYER_ID, fullName = ANOTHER_OLD_PLAYER_FULL_NAME)
    val anotherNewPlayer = aJdbcPlayerDto(playerId = ANOTHER_NEW_PLAYER_ID, fullName = ANOTHER_NEW_PLAYER_FULL_NAME)
    val allPlayers = listOf(aPlayer, anOldPlayer, aNewPlayer, anotherOldPlayer, anotherNewPlayer)
    val endingTournament = aJdbcTournamentDto(tournamentId = A_TOURNAMENT_ID)
    val startingTournament = aJdbcTournamentDto(tournamentId = ANOTHER_TOURNAMENT_ID)
    val allTournaments = listOf(endingTournament, startingTournament)
    val teamQueryParams = mapOf("teamId" to A_TEAM_ID, "playerIds" to playerToRemoveIds)
    val teamDto = aJdbcTeamDto(teamId = A_TEAM_ID, playerId = AN_OLD_PLAYER_ID)
    val notAllTeamsFound = listOf(teamDto)

    val expected = ErrorTeam

    every { retrieveFantaTeamRepository.retrieve(A_TEAM_ID) } returns FoundTeam(oldTeamDto)
    every { jdbcTemplate.query(RETRIEVE_ALL_PLAYERS_QUERY, any<RowMapper<JdbcPlayerDto>>()) } returns allPlayers
    every { jdbcTemplate.query(RETRIEVE_ALL_TOURNAMENTS_QUERY, any<RowMapper<JdbcTournamentDto>>()) } returns allTournaments
    every {
      namedParameterJdbcTemplate.query(RETRIEVE_TEAM_BY_PK_QUERY, teamQueryParams, any<RowMapper<JdbcTeamDto>>())
    } returns notAllTeamsFound

    assertThat(repository.swap(A_TEAM_ID, playersToSwap)).isEqualTo(expected)
  }

  companion object {

    private const val A_NOT_EXISTING_TEAM_ID = 1
    private const val A_TEAM_ID = 1
    private const val A_TOURNAMENT_ID = 1
    private const val ANOTHER_TOURNAMENT_ID = 2
    private const val A_THIRD_TOURNAMENT_ID = 3
    private const val AN_OWNER = "AN_OWNER"
    private const val A_PLAYER_ID = "A_PLAYER_ID"
    private const val AN_OLD_PLAYER_ID = "AN_OLD_PLAYER_ID"
    private const val ANOTHER_OLD_PLAYER_ID = "ANOTHER_OLD_PLAYER_ID"
    private const val A_NEW_PLAYER_ID = "A_NEW_PLAYER_ID"
    private const val ANOTHER_NEW_PLAYER_ID = "ANOTHER_NEW_PLAYER_ID"
    private const val A_PLAYER_FULL_NAME = "A_PLAYER_FULL_NAME"
    private const val AN_OLD_PLAYER_FULL_NAME = "AN_OLD_PLAYER_FULL_NAME"
    private const val ANOTHER_OLD_PLAYER_FULL_NAME = "ANOTHER_OLD_PLAYER_FULL_NAME"
    private const val A_NEW_PLAYER_FULL_NAME = "A_NEW_PLAYER_FULL_NAME"
    private const val ANOTHER_NEW_PLAYER_FULL_NAME = "ANOTHER_NEW_PLAYER_FULL_NAME"

    private val ANY_SWAP_PLAYERS = PlayersToSwapDto()
    private val A_PLAYER = aJdbcPlayerDto(playerId = A_PLAYER_ID, fullName = A_PLAYER_FULL_NAME)
    private val A_TOURNAMENT = aJdbcTournamentDto(tournamentId = A_THIRD_TOURNAMENT_ID)

    private val RETRIEVE_ALL_PLAYERS_QUERY = """
      SELECT *
      FROM PLAYERS p;
    """.trimIndent()

    private val RETRIEVE_ALL_TOURNAMENTS_QUERY = """
      SELECT *
      FROM TOURNAMENTS t;
    """.trimIndent()

    private val RETRIEVE_TEAM_BY_PK_QUERY = """
      SELECT *
      FROM TEAMS t
      WHERE t.TEAM_ID = :teamId
      AND t.PLAYER_ID IN (:playerIds);
    """.trimIndent()
  }
}