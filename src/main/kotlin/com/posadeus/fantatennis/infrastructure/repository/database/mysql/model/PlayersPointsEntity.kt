package com.posadeus.fantatennis.infrastructure.repository.database.mysql.model

import jakarta.persistence.*
import java.io.Serializable

@Entity
@Table(name = "PLAYERS_POINTS")
data class PlayersPointsEntity(@EmbeddedId val id: PlayersPointsKeyEmbedded = PlayersPointsKeyEmbedded(),
                               @Column(name = "FANTA_POINTS") val fantaPoints: Double = 0.0) : Serializable
