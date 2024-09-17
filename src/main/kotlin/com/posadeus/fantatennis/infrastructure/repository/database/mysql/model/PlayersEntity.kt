package com.posadeus.fantatennis.infrastructure.repository.database.mysql.model

import jakarta.persistence.*
import java.io.Serializable

@Entity
@Table(name = "PLAYERS")
data class PlayersEntity(
    @Id
    @Column(name = "PLAYER_ID")
    val id: String = "",

    @Column(name = "ATP_TOUR_ID")
    val atpTourId: String = "",

    @Column(name = "FULL_NAME")
    val fullName: String = "",

    @OneToMany(mappedBy = "player", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val teams: List<TeamsEntity> = emptyList()
) : Serializable
