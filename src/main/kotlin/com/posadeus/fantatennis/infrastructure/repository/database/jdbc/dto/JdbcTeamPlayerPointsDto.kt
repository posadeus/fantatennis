package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto

data class JdbcTeamPlayerPointsDto(val playerId: String,
                                   val playerName: String,
                                   val tournamentId: Int,
                                   val tournamentScore: Double = 0.00)