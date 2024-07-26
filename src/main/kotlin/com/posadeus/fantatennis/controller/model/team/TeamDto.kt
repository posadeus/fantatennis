package com.posadeus.fantatennis.controller.model.team

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 * @param players 
 * @param totalScore Total team's score
 * @param completed True if the team has the correct number of players
 */
data class TeamDto(

    @get:JsonProperty("players", required = true) val players: List<TeamPlayerDto>,

    @get:JsonProperty("totalScore", required = true) val totalScore: Double,

    @get:JsonProperty("completed", required = true) val completed: Boolean = false
)

