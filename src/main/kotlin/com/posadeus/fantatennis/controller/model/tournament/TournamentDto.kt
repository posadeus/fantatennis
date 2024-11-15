package com.posadeus.fantatennis.controller.model.tournament

import com.fasterxml.jackson.annotation.JsonProperty
import com.posadeus.fantatennis.controller.model.team.TeamDto
import java.io.Serializable

/**
 * 
 * @param teams 
 */
data class TournamentDto(

    @get:JsonProperty("teams", required = true) val teams: List<TeamDto>
    ) : Serializable{

    companion object {
        private const val serialVersionUID: Long = 1
    }
}

