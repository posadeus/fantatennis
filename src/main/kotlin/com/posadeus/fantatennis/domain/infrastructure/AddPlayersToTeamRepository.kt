package com.posadeus.fantatennis.domain.infrastructure

interface AddPlayersToTeamRepository {

  fun add(teamId: Int, playerIds: Set<String>, startingTournamentId: Int)
}
