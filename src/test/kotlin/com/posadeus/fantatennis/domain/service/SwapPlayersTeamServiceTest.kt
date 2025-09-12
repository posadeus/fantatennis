package com.posadeus.fantatennis.domain.service

import com.posadeus.fantatennis.controller.model.team.*
import com.posadeus.fantatennis.domain.exception.InvalidPlayersSwapException
import com.posadeus.fantatennis.domain.infrastructure.SwapPlayersRepository
import com.posadeus.fantatennis.domain.model.Swap.SwapCompleted
import com.posadeus.fantatennis.domain.model.Swap.SwapFailed
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.Test

class SwapPlayersTeamServiceTest {

  private val swapPlayersRepository: SwapPlayersRepository = mockk()

  private val service = SwapPlayersTeamService(swapPlayersRepository)

  @Test
  fun `swap fails due to generic error`() {

     val expected = SwapFailed

    every { swapPlayersRepository.swap(ANY_TEAM_ID, ANY_PLAYER_TO_SWAP) } returns SwapFailed

    assertThat(service.swap(ANY_TEAM_ID, ANY_PLAYER_TO_SWAP)).isEqualTo(expected)
  }

  @Test
  fun `swap throws InvalidPlayersSwapException`() {

     val expected = SwapFailed

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

    every { swapPlayersRepository.swap(A_TEAM_ID, playersToSwap) } returns SwapCompleted

    assertThat(service.swap(A_TEAM_ID, playersToSwap)).isEqualTo(SwapCompleted)
  }

  companion object {

    private const val AN_OLD_PLAYER_ID = "AN_OLD_PLAYER_ID"
    private const val ANOTHER_OLD_PLAYER_ID = "ANOTHER_OLD_PLAYER_ID"
    private const val A_NEW_PLAYER_ID = "A_NEW_PLAYER_ID"
    private const val ANOTHER_NEW_PLAYER_ID = "ANOTHER_NEW_PLAYER_ID"
    private const val A_TEAM_ID = 1
    private const val ANY_TEAM_ID = 1
    private const val A_TOURNAMENT_ID = 1
    private const val ANOTHER_TOURNAMENT_ID = 2

    private val ANY_PLAYER_TO_SWAP = PlayersToSwapDto()
  }
}