package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.controller.model.team.*
import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTeamRepository
import com.posadeus.fantatennis.domain.infrastructure.SwapPlayersRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerDto
import io.mockk.*
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper

class JdbcSwapPlayersRepositoryTest {

  private val retrieveFantaTeamRepository: RetrieveFantaTeamRepository = mockk()
  private val jdbcTemplate: JdbcTemplate = mockk()

  private val repository: SwapPlayersRepository = JdbcSwapPlayersRepository(retrieveFantaTeamRepository, jdbcTemplate)

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

  companion object {

    private const val A_NOT_EXISTING_TEAM_ID = 1
    private const val A_TEAM_ID = 1
    private const val A_TOURNAMENT_ID = 1
    private const val ANOTHER_TOURNAMENT_ID = 2
    private const val AN_OWNER = "AN_OWNER"
    private const val AN_OLD_PLAYER_ID = "AN_OLD_PLAYER_ID"
    private const val ANOTHER_OLD_PLAYER_ID = "ANOTHER_OLD_PLAYER_ID"
    private const val A_NEW_PLAYER_ID = "A_NEW_PLAYER_ID"
    private const val ANOTHER_NEW_PLAYER_ID = "ANOTHER_NEW_PLAYER_ID"
    private const val A_PLAYER_FULL_NAME = "A_PLAYER_FULL_NAME"
    private const val AN_OLD_PLAYER_FULL_NAME = "AN_OLD_PLAYER_FULL_NAME"
    private const val ANOTHER_OLD_PLAYER_FULL_NAME = "ANOTHER_OLD_PLAYER_FULL_NAME"

    private val ANY_SWAP_PLAYERS = PlayersToSwapDto()

    private val RETRIEVE_ALL_PLAYERS_QUERY = """
      SELECT *
      FROM PLAYERS p;
    """.trimIndent()
  }
}