package com.posadeus.fantatennis.controller.model.team

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 * @param fullName Player's full name
 * @param fantaPoints Player's fanta-points
 * @param chosen TRUE if player has been chosen for the tournament
 * @param playing Player's actual tournament played
 */
data class TeamPlayerDto(

    @get:JsonProperty("fullName", required = true) val fullName: String = "",

    @get:JsonProperty("fantaPoints", required = true) val fantaPoints: Int = 0,

    @get:JsonProperty("chosen", required = true) val chosen: Boolean = false,

    @get:JsonProperty("playing") val playing: String? = null
)

