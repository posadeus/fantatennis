package com.posadeus.fantatennis.domain.model

data class TournamentRegistry(val atpTourId: Int,
                              val tennisTvId: Int,
                              val name: String,
                              val startDate: String,
                              val endDate: String,
                              val year: Int,
                              val points: Int,
                              val surface: Surface,
                              val location: String)