package com.posadeus.fantatennis.infrastructure.repository.database.mysql.model

import jakarta.persistence.*

@Entity
@Table(name = "PLAYERS")
data class PlayersEntity(@Id
                         @Column(name = "ID") val id: String,
                         @Column(name = "ATP_TOUR_ID") val atpTourId: String,
                         @Column(name = "FANTA_POINTS") val fantaPoints: Double,
                         @Column(name = "FULL_NAME") val fullName: String) {

    constructor() : this("", "", 0.00, "")
}
