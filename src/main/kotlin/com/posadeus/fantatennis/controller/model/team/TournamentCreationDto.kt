package com.posadeus.fantatennis.controller.model.team

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * 
 * @param id 
 * @param startingTournamentId 
 * @param endingTournamentId 
 * @param tournamentYear 
 */
data class TournamentCreationDto(

    @get:JsonProperty("id", required = true) val id: Int,

    @get:JsonProperty("startingTournamentId") val startingTournamentId: Int? = null,

    @get:JsonProperty("endingTournamentId") val endingTournamentId: Int? = null,

    @get:JsonProperty("tournamentYear") val tournamentYear: Int? = null
    ) : Serializable{

    companion object {
        private const val serialVersionUID: Long = 1
    }
}

