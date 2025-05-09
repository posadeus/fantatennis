package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.*

interface TeamsRepository {

  @Deprecated("Use the new JDBC repo")
  fun addPlayers(teamId: Int, playerIds: Set<String>, startingTournamentId: Int): AddPlayers
  fun swapPlayers(swapCommand: SwapCommand): Swap
}
