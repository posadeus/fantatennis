package com.posadeus.fantatennis.infrastructure.repository.database.mysql.model

import jakarta.persistence.*
import java.io.Serializable

@Embeddable
data class PlayersPointsKeyEmbedded(
    @Column(name = "TOURNAMENT_YEAR")
    val tournamentYear: Int = 0,

    @Column(name = "TOURNAMENT_ID")
    val tournamentId: Int = 0,

    @Column(name = "PLAYER_ID")
    val playerId: String = ""
) : Serializable

@Entity
@Table(name = "PLAYERS_POINTS")
data class PlayersPointsEntity(
    @EmbeddedId
    val id: PlayersPointsKeyEmbedded = PlayersPointsKeyEmbedded(),

    @Column(name = "FANTA_POINTS")
    val fantaPoints: Double = 0.0,

    @ManyToOne
    @JoinColumn(name = "ID", insertable = false, updatable = false)
    val player: PlayersEntity = PlayersEntity(),

    @ManyToOne
    @JoinColumn(name = "ID", insertable = false, updatable = false)
    val tournament: TournamentsEntity = TournamentsEntity()
) : Serializable
