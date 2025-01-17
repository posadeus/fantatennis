package com.posadeus.fantatennis.controller.model.team

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * 
 * @param ownerId 
 * @param tournamentId 
 */
data class TeamToCreateDto(

    @get:JsonProperty("ownerId", required = true) val ownerId: String = "",

    @get:JsonProperty("tournamentId", required = true) val tournamentId: Int = 0
    ) : Serializable{

    companion object {
        private const val serialVersionUID: Long = 1
    }
}

