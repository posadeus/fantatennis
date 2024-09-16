package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.*

interface PlayerPointsRepository {

  fun save(players: Set<DomainPlayer>)
  fun retrieve(tournamentByTeam: TournamentByTeam): TeamOrderedPlayerPoints
}
