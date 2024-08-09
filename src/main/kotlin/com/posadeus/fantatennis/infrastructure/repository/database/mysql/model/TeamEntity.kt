package com.posadeus.fantatennis.infrastructure.repository.database.mysql.model

import jakarta.persistence.*

@Entity
@Table(name = "TEAMS")
data class TeamEntity(@EmbeddedId val id: TeamKeyEmbedded,
                      @Column(name = "CHOSEN") val chosen: Boolean) {

    constructor() : this(TeamKeyEmbedded(), false)
}
