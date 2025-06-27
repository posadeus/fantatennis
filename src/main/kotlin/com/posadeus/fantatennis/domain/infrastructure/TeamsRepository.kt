package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.AddPlayers

interface TeamsRepository {

  @Deprecated("Use the new JDBC repo")
  fun addPlayers(teamId: Int, playerIds: Set<String>, startingTournamentId: Int): AddPlayers
}
