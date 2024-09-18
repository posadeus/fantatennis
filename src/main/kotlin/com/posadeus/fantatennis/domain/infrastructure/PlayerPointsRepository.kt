package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.*

interface PlayerPointsRepository {

  fun save(players: Set<AtpPlayer>)
  fun retrieve(tournamentByTeam: FoundTournamentByTeam): TeamOrderedPlayerPoints
}
