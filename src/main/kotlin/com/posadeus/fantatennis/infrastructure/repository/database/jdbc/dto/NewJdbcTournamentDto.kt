package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto

import java.time.LocalDate

data class NewJdbcTournamentDto(val atpTourId: Int,
                                val tennisTvId: Int,
                                val name: String,
                                val points: Int,
                                val location: String,
                                val surface: String,
                                val year: Int,
                                val startDate: LocalDate,
                                val endDate: LocalDate)