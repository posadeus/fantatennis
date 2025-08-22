package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.AddPlayers

interface AddPlayersToTeamRepository {

  fun add(teamId: Int, playerIds: Set<String>, startingTournamentId: Int): AddPlayers
}
