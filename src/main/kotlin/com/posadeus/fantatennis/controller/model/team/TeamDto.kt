package com.posadeus.fantatennis.controller.model.team

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 * @param players 
 * @param totalScore Total team's score
 */
data class TeamDto(

    @get:JsonProperty("players", required = true) val players: List<TeamPlayerDto>,

    @get:JsonProperty("totalScore", required = true) val totalScore: Int = 0
)

