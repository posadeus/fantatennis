package com.posadeus.fantatennis.infrastructure.repository.database.mysql.model

import jakarta.persistence.*
import java.io.Serializable

@Entity
@Table(name = "FANTA_TOURNAMENTS_TEAMS")
data class FantaTournamentsTeamsEntity(
    @Id
    @Column(name = "FANTA_TOURNAMENTS_TEAMS_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val fantaTournamentsTeamsId: Int = 0,

    @ManyToOne
    @JoinColumn(name = "ID", nullable = false)
    val tournament: FantaTournamentsEntity = FantaTournamentsEntity(),

    @OneToOne
    @JoinColumn(name = "TEAM_ID", nullable = false)
    val fantaTeam: FantaTeamsEntity = FantaTeamsEntity()
) : Serializable
