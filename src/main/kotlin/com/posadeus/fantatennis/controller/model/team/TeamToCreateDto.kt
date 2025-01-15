package com.posadeus.fantatennis.controller.model.team

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * 
 * @param ownerId 
 * @param tournament 
 */
data class TeamToCreateDto(

    @get:JsonProperty("ownerId", required = true) val ownerId: String = "",

    @get:JsonProperty("tournament", required = true) val tournament: TournamentCreationDto
    ) : Serializable{

    companion object {
        private const val serialVersionUID: Long = 1
    }
}

