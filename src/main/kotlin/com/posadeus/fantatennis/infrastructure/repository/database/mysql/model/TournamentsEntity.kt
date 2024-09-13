package com.posadeus.fantatennis.infrastructure.repository.database.mysql.model

import jakarta.persistence.*
import java.io.Serializable

@Entity
@Table(name = "TOURNAMENTS")
data class TournamentsEntity(
    @Id
    @Column(name = "ID")
    val id: Int = 0,

    @Column(name = "ATP_TOUR_ID")
    val atpTourId: Int = 0,

    @Column(name = "TENNIS_TV_ID")
    val tennisTvId: Int = 0,

    @Column(name = "NAME")
    val name: String = "",

    @Column(name = "POINTS")
    val points: Int = 0,

    @Column(name = "LOCATION")
    val location: String = "",

    @Column(name = "SURFACE")
    val surface: String = ""
) : Serializable
