package com.posadeus.fantatennis.controller.model.tournament

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * 
 * @param startingTournamentId 
 * @param endingTournamentId 
 * @param tournamentYear 
 */
data class TournamentToCreateDto(

    @get:JsonProperty("startingTournamentId", required = true) val startingTournamentId: Int = 1,

    @get:JsonProperty("endingTournamentId", required = true) val endingTournamentId: Int = 1,

    @get:JsonProperty("tournamentYear", required = true) val tournamentYear: Int = 2024
    ) : Serializable{

    companion object {
        private const val serialVersionUID: Long = 1
    }
}

