package com.posadeus.fantatennis.infrastructure.repository.database.mysql.model

import jakarta.persistence.*
import java.io.Serializable

@Embeddable
data class TeamsKeyEmbedded(
    @Column(name = "TEAM_ID")
    val teamId: Int = 0,

    @Column(name = "PLAYER_ID")
    val playerId: String = ""
) : Serializable

@Entity
@Table(name = "TEAMS")
data class TeamsEntity(
    @EmbeddedId
    val id: TeamsKeyEmbedded = TeamsKeyEmbedded(),

    @ManyToOne
    @JoinColumn(name = "PLAYER_ID", insertable = false, updatable = false)
    val player: PlayersEntity = PlayersEntity(),

    @ManyToOne
    @JoinColumn(name = "TEAM_ID", insertable = false, updatable = false)
    val fantaTeam: FantaTeamsEntity = FantaTeamsEntity()
) : Serializable
