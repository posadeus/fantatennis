package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto

data class JdbcTournamentResultsDto(val tournamentId: Int,
                                    val teamId: Int,
                                    val ownerId: String,
                                    val playerId: String,
                                    val playerFullName: String,
                                    val playerTotalScore: Double)