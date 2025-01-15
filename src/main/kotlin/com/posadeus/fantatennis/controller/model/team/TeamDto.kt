package com.posadeus.fantatennis.controller.model.team

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * 
 * @param owner The owner of the team
 * @param players 
 * @param totalScore Total team's score
 */
data class TeamDto(

    @get:JsonProperty("owner", required = true) val owner: String = "",

    @get:JsonProperty("players", required = true) val players: List<TeamPlayerDto>,

    @get:JsonProperty("totalScore", required = true) val totalScore: Double
    ) : Serializable{

    companion object {
        private const val serialVersionUID: Long = 1
    }
}

