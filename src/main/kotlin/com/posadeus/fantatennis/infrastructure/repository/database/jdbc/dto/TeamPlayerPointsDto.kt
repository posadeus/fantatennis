package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto

data class TeamPlayerPointsDto(val playerId: String,
                               val playerName: String,
                               val totalScore: Double = 0.00)