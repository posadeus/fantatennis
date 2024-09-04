package com.posadeus.fantatennis.infrastructure.repository.database.mysql.model

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class TeamsKeyEmbedded(@Column(name = "TEAM_ID") val teamId: String,
                            @Column(name = "PLAYER_ID")val playerId: String) : Serializable {

    constructor() : this("", "")
}
