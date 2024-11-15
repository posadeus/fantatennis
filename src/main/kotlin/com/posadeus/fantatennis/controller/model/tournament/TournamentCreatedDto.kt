package com.posadeus.fantatennis.controller.model.tournament

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * 
 * @param id 
 * @param startingTournamentId 
 * @param endingTournamentId 
 * @param tournamentYear 
 */
data class TournamentCreatedDto(

    @get:JsonProperty("id", required = true) val id: Int,

    @get:JsonProperty("startingTournamentId", required = true) val startingTournamentId: Int,

    @get:JsonProperty("endingTournamentId", required = true) val endingTournamentId: Int,

    @get:JsonProperty("tournamentYear", required = true) val tournamentYear: Int
    ) : Serializable{

    companion object {
        private const val serialVersionUID: Long = 1
    }
}

