package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto

data class JdbcPlayerPointsDto(val tournamentYear: Int,
                               val tournamentId: Int,
                               val playerId: String,
                               val fantaPoints: Double)
