package com.posadeus.fantatennis.infrastructure.repository.database.mysql.model

import jakarta.persistence.*

@Entity
@Table(name = "PLAYERS_POINTS")
data class PlayersPointsEntity(@EmbeddedId val id: PlayersPointsKeyEmbedded,
                               @Column(name = "FANTA_POINTS") val fantaPoints: Double) {

    constructor() : this(PlayersPointsKeyEmbedded(), 0.0)
}
