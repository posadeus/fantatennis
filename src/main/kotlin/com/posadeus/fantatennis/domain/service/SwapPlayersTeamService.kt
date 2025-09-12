package com.posadeus.fantatennis.domain.service

import com.posadeus.fantatennis.controller.model.team.PlayersToSwapDto
import com.posadeus.fantatennis.domain.exception.InvalidPlayersSwapException
import com.posadeus.fantatennis.domain.infrastructure.SwapPlayersRepository
import com.posadeus.fantatennis.domain.model.Swap
import com.posadeus.fantatennis.domain.model.Swap.SwapFailed

class SwapPlayersTeamService(private val swapPlayersRepository: SwapPlayersRepository) {

  fun swap(teamId: Int, playersToSwap: PlayersToSwapDto): Swap =
      try {

        swapPlayersRepository.swap(teamId, playersToSwap)
      }
      catch (e: InvalidPlayersSwapException) {

        SwapFailed
      }
}
