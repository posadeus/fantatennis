package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class JdbcTeamDto(@JsonProperty("TEAM_ID")val teamId: Int,
                       @JsonProperty("PLAYER_ID")val playerId: String,
                       @JsonProperty("STARTING_TOURNAMENT")val startingTournamentId: Int,
                       @JsonProperty("ENDING_TOURNAMENT")val endingTournamentId: Int?)
