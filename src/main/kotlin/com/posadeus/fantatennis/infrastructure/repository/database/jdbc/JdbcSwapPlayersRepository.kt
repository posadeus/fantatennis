package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.controller.model.team.PlayersToSwapDto
import com.posadeus.fantatennis.domain.infrastructure.SwapPlayersRepository
import com.posadeus.fantatennis.domain.model.Team

class JdbcSwapPlayersRepository : SwapPlayersRepository {

  override fun swap(teamId: Int, playersToSwap: PlayersToSwapDto): Team {
    TODO("Not yet implemented")
  }
}
