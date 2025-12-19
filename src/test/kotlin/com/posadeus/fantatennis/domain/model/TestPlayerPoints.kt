package com.posadeus.fantatennis.domain.model

object TestPlayerPoints {

  fun aPlayerPoints(playerId: String = "A_PLAYER_ID",
                    playerName: String = "A_PLAYER_NAME",
                    totalPoints: Double = 0.0) =
      PlayerPoints(playerId = playerId,
                   playerName = playerName,
                   totalPoints = totalPoints)
}