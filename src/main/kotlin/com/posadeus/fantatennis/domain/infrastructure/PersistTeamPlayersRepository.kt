package com.posadeus.fantatennis.domain.infrastructure

interface PersistTeamPlayersRepository {

  fun persist(teamId: Int, playerIds: Set<String>, startingTournamentId: Int)
}
