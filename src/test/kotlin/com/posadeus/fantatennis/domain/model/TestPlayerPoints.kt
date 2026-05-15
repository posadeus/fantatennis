package com.posadeus.fantatennis.domain.model

import com.posadeus.fantatennis.domain.model.PlayersPoints.FoundPlayersPoints.PlayerPoints

object TestPlayerPoints {

  fun aPlayerPoints(playerId: String = "A_PLAYER_ID",
                    playerName: String = "A_PLAYER_NAME",
                    pointsByTournament: Map<Int, Double> = emptyMap(),
                    totalPoints: Double = 0.0) =
      PlayerPoints(playerId = playerId,
                   playerName = playerName,
                   pointsByTournament = pointsByTournament,
                   totalPoints = totalPoints)
}