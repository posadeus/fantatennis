package com.posadeus.fantatennis.infrastructure.repository.database.mysql.model

import jakarta.persistence.*

@Entity
@Table(name = "TEAMS")
data class TeamsEntity(@EmbeddedId val id: TeamsKeyEmbedded,
                       @Column(name = "CHOSEN") val chosen: Boolean) {

    constructor() : this(TeamsKeyEmbedded(), false)
}
