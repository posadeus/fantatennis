package com.posadeus.fantatennis.infrastructure.repository.database.mysql.model

import jakarta.persistence.*
import java.io.Serializable

@Entity
@Table(name = "FANTA_TEAMS")
data class FantaTeamsEntity(
    @Id
    @GeneratedValue
    @Column(name = "TEAM_ID")
    val teamId: Int = 0,

    @Column(name = "OWNER_ID")
    val ownerId: String = "",

    @OneToMany(mappedBy = "fantaTeam", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val teams: List<TeamsEntity> = emptyList()
) : Serializable
