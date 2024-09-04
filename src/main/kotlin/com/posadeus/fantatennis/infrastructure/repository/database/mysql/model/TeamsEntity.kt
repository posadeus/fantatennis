package com.posadeus.fantatennis.infrastructure.repository.database.mysql.model

import jakarta.persistence.*
import java.io.Serializable

@Entity
@Table(name = "TEAMS")
data class TeamsEntity(@EmbeddedId val id: TeamsKeyEmbedded = TeamsKeyEmbedded(),
                       @Column(name = "CHOSEN") val chosen: Boolean = false) : Serializable
