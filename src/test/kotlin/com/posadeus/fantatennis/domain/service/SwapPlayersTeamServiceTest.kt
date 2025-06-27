package com.posadeus.fantatennis.domain.service

import com.posadeus.fantatennis.controller.model.team.*
import com.posadeus.fantatennis.domain.exception.InvalidPlayersSwapException
import com.posadeus.fantatennis.domain.infrastructure.SwapPlayersRepository
import com.posadeus.fantatennis.domain.model.*
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.Test

class SwapPlayersTeamServiceTest {

  private val swapPlayersRepository: SwapPlayersRepository = mockk()

  private val service = SwapPlayersTeamService(swapPlayersRepository)

  @Test
  fun `swap fails due to team not found`() {

     val expected = TeamIdNotFoundTeam

    every { swapPlayersRepository.swap(123, ANY_PLAYER_TO_SWAP) } returns TeamIdNotFoundTeam

    assertThat(service.swap(123, ANY_PLAYER_TO_SWAP)).isEqualTo(expected)
  }

  @Test
  fun `swap fails due to generic error`() {

     val expected = ErrorTeam

    every { swapPlayersRepository.swap(ANY_TEAM_ID, ANY_PLAYER_TO_SWAP) } returns ErrorTeam

    assertThat(service.swap(ANY_TEAM_ID, ANY_PLAYER_TO_SWAP)).isEqualTo(expected)
  }

  @Test
  fun `swap throws InvalidPlayersSwapException`() {

     val expected = ErrorTeam

    every { swapPlayersRepository.swap(ANY_TEAM_ID, ANY_PLAYER_TO_SWAP) } throws InvalidPlayersSwapException("OMG an error")

    assertThat(service.swap(ANY_TEAM_ID, ANY_PLAYER_TO_SWAP)).isEqualTo(expected)
  }

  @Test
  fun `swap completed, with new players fantaPoints to ZERO, with removed and already present players still present in team response`() {

    val playerToRemoveIds = setOf(AN_OLD_PLAYER_ID, ANOTHER_OLD_PLAYER_ID)
    val playersToRemoveDto = PlayersToRemoveDto(playerIds = playerToRemoveIds, endingTournamentId = A_TOURNAMENT_ID)
    val playerToAddIds = setOf(A_NEW_PLAYER_ID, ANOTHER_NEW_PLAYER_ID)
    val playersToAddDto = PlayersToAddDto(playerIds = playerToAddIds, startingTournamentId = ANOTHER_TOURNAMENT_ID)
    val playersToSwap = PlayersToSwapDto(remove = playersToRemoveDto, add = playersToAddDto)

    val newTeamDto = TeamDto(players = listOf(TeamPlayerDto(fullName = A_PLAYER_FULL_NAME, fantaPoints = 22.0),
                                              TeamPlayerDto(fullName = AN_OLD_PLAYER_FULL_NAME, fantaPoints = 10.0),
                                              TeamPlayerDto(fullName = ANOTHER_OLD_PLAYER_FULL_NAME, fantaPoints = 8.0),
                                              TeamPlayerDto(fullName = A_NEW_PLAYER_FULL_NAME, fantaPoints = 0.0),
                                              TeamPlayerDto(fullName = ANOTHER_NEW_PLAYER_FULL_NAME, fantaPoints = 0.0)),
                             totalScore = 40.0,
                             owner = AN_OWNER)
    val expected = FoundTeam(team = newTeamDto)

    every { swapPlayersRepository.swap(A_TEAM_ID, playersToSwap) } returns expected

    assertThat(service.swap(A_TEAM_ID, playersToSwap)).isEqualTo(expected)
  }

  companion object {

    private const val AN_OLD_PLAYER_ID = "AN_OLD_PLAYER_ID"
    private const val ANOTHER_OLD_PLAYER_ID = "ANOTHER_OLD_PLAYER_ID"
    private const val A_NEW_PLAYER_ID = "A_NEW_PLAYER_ID"
    private const val ANOTHER_NEW_PLAYER_ID = "ANOTHER_NEW_PLAYER_ID"
    private const val A_PLAYER_FULL_NAME = "A_PLAYER_FULL_NAME"
    private const val AN_OLD_PLAYER_FULL_NAME = "AN_OLD_PLAYER_FULL_NAME"
    private const val ANOTHER_OLD_PLAYER_FULL_NAME = "ANOTHER_OLD_PLAYER_FULL_NAME"
    private const val A_NEW_PLAYER_FULL_NAME = "A_NEW_PLAYER_FULL_NAME"
    private const val ANOTHER_NEW_PLAYER_FULL_NAME = "ANOTHER_NEW_PLAYER_FULL_NAME"
    private const val AN_OWNER = "AN_OWNER"
    private const val A_TEAM_ID = 1
    private const val ANY_TEAM_ID = 1
    private const val A_TOURNAMENT_ID = 1
    private const val ANOTHER_TOURNAMENT_ID = 2

    private val ANY_PLAYER_TO_SWAP = PlayersToSwapDto()
  }
}