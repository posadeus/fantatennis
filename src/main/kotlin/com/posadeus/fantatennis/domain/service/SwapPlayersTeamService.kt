package com.posadeus.fantatennis.domain.service

import com.posadeus.fantatennis.controller.model.team.PlayersToSwapDto
import com.posadeus.fantatennis.domain.exception.InvalidPlayersSwapException
import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTeamRepository
import com.posadeus.fantatennis.domain.infrastructure.SwapPlayersRepository
import com.posadeus.fantatennis.domain.model.DomainTeam.FoundDomainTeam
import com.posadeus.fantatennis.domain.model.DomainTeam.NotFoundDomainTeam
import com.posadeus.fantatennis.domain.model.Swap
import com.posadeus.fantatennis.domain.model.Swap.SwapFailed
import org.slf4j.LoggerFactory

class SwapPlayersTeamService(private val swapPlayersRepository: SwapPlayersRepository,
                             private val retrieveFantaTeamRepository: RetrieveFantaTeamRepository) {

  fun swap(teamId: Int, playersToSwap: PlayersToSwapDto): Swap =
      when (retrieveFantaTeamRepository.retrieveByTeamId(teamId)) {

        is NotFoundDomainTeam -> SwapFailed.also { LOGGER.error("Team not found: $teamId.") }

        is FoundDomainTeam ->
          try {

            swapPlayersRepository.swap(teamId, playersToSwap)
          }
          catch (e: InvalidPlayersSwapException) {

            SwapFailed
          }
      }

  companion object {

    private val LOGGER = LoggerFactory.getLogger(SwapPlayersTeamService::class.java)
  }
}
