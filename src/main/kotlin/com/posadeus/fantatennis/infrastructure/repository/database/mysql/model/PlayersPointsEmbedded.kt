package com.posadeus.fantatennis.infrastructure.repository.database.mysql.model

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class PlayersPointsEmbedded(@Column(name = "TOURNAMENT_YEAR") val tournamentYear: Int = 0,
                                 @Column(name = "TOURNAMENT_ID") val tournamentId: String = "",
                                 @Column(name = "PLAYER_ID") val playerId: String = "") : Serializable
