package com.posadeus.fantatennis.infrastructure.repository.database.mysql.model

import jakarta.persistence.*
import java.io.Serializable

@Embeddable
data class FantaTournamentsTeamsKeyEmbedded(
    @Column(name = "FANTA_TOURNAMENT_ID")
    val tournamentId: Int = 0,

    @Column(name = "TEAM_ID")
    val teamId: Int = 0
) : Serializable

@Entity
@Table(name = "FANTA_TOURNAMENTS_TEAMS")
data class FantaTournamentsTeamsEntity(
    @EmbeddedId
    val id: FantaTournamentsTeamsKeyEmbedded = FantaTournamentsTeamsKeyEmbedded(),

    @ManyToOne
    @JoinColumn(name = "FANTA_TOURNAMENT_ID", insertable = false, updatable = false)
    val tournament: FantaTournamentsEntity = FantaTournamentsEntity(),

    @OneToOne
    @JoinColumn(name = "TEAM_ID", insertable = false, updatable = false)
    val fantaTeam: FantaTeamsEntity = FantaTeamsEntity()
) : Serializable
