package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.*

interface TeamsRepository {

  fun addPlayers(teamId: Int, playerIds: Set<String>, startingTournamentId: Int): AddPlayers
  fun swapPlayers(swapCommand: SwapCommand): Swap
}
