package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto

data class JdbcFantaTournamentDto(val id: Int,
                                  val startingTournamentId: Int,
                                  val endingTournamentId: Int,
                                  val year: Int)
