package com.posadeus.fantatennis.domain.service

import com.posadeus.fantatennis.controller.model.team.PlayersToSwapDto
import com.posadeus.fantatennis.domain.exception.InvalidPlayersSwapException
import com.posadeus.fantatennis.domain.infrastructure.SwapPlayersRepository
import com.posadeus.fantatennis.domain.model.ErrorTeam
import com.posadeus.fantatennis.domain.model.Team

class SwapPlayersTeamService(private val swapPlayersRepository: SwapPlayersRepository) {

  fun swap(teamId: Int, playersToSwap: PlayersToSwapDto): Team =
      try {

        swapPlayersRepository.swap(teamId, playersToSwap)
      }
      catch (e: InvalidPlayersSwapException) {

        ErrorTeam
      }
}
