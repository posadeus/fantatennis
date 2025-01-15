package com.posadeus.fantatennis.infrastructure.repository.database.mysql.model

import jakarta.persistence.*
import java.io.Serializable

@Entity
@Table(name = "FANTA_TOURNAMENTS")
data class FantaTournamentsEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FANTA_TOURNAMENT_ID")
    val id: Int = 0,

    @Column(name = "STARTING_TOURNAMENT")
    val startingTournament: Int = 0,

    @Column(name = "ENDING_TOURNAMENT")
    val endingTournament: Int = 0,

    @Column(name = "TOURNAMENT_YEAR")
    val year: Int = 0,

    @OneToMany(mappedBy = "tournament", cascade = [CascadeType.ALL], orphanRemoval = true)
    val tournaments: MutableList<FantaTournamentsTeamsEntity> = mutableListOf()

) : Serializable