package com.posadeus.fantatennis.domain.model

object TestAtpPlayer {

  fun anAtpPlayer(id: AtpPlayerId = "",
                  tournamentPoints: Map<Year, Map<TournamentId, Double>> = emptyMap()): AtpPlayer =
      AtpPlayer(id = id,
                tournamentPoints = tournamentPoints)
}