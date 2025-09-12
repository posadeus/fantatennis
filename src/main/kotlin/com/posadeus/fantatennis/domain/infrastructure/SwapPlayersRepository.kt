package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.controller.model.team.PlayersToSwapDto
import com.posadeus.fantatennis.domain.model.Swap

interface SwapPlayersRepository {

  fun swap(teamId: Int, playersToSwap: PlayersToSwapDto): Swap
}
