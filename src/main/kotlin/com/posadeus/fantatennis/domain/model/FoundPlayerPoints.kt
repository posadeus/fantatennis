package com.posadeus.fantatennis.domain.model

sealed interface PlayersPoints {

  data class FoundPlayersPoints(val playersPoints: List<PlayerPoints>) : PlayersPoints {

    data class PlayerPoints(val playerId: String,
                            val playerName: String,
                            val totalPoints: Double)
  }

  data object InternalErrorPlayersPoints : PlayersPoints
}
