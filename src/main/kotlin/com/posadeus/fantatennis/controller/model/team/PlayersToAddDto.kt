package com.posadeus.fantatennis.controller.model.team

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * 
 * @param playerIds 
 */
data class PlayersToAddDto(

    @get:JsonProperty("playerIds", required = true) val playerIds: Set<String> = setOf()
    ) : Serializable{

    companion object {
        private const val serialVersionUID: Long = 1
    }
}

