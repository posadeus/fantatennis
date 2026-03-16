package com.posadeus.fantatennis.domain.model

sealed interface PlayersPoints {

  data class FoundPlayersPoints(val playersPoints: List<PlayerPoints>) : PlayersPoints {

    data class PlayerPoints(val playerId: String,
                            val playerName: String,
                            val pointsByTournament: Map<Int, Double> = emptyMap(),
                            val totalPoints: Double)
  }

  data object InternalErrorPlayersPoints : PlayersPoints
}
