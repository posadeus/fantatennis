package com.posadeus.fantatennis.infrastructure.repository.database.mysql.model

import jakarta.persistence.*

@Entity
@Table(name = "PLAYERS_POINTS")
data class PlayersPointsEntity(@EmbeddedId val id: PlayersPointsEmbedded,
                               @Column(name = "FANTA_POINTS") val fantaPoints: Double) {

    constructor() : this(PlayersPointsEmbedded(), 0.0)
}
