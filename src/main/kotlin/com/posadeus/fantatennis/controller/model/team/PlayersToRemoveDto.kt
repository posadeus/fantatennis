package com.posadeus.fantatennis.controller.model.team

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * 
 * @param playerIds 
 * @param endingTournamentId 
 */
data class PlayersToRemoveDto(

    @get:JsonProperty("playerIds", required = true) val playerIds: Set<String> = setOf(),

    @get:JsonProperty("endingTournamentId", required = true) val endingTournamentId: Int = 0
    ) : Serializable{

    companion object {
        private const val serialVersionUID: Long = 1
    }
}

