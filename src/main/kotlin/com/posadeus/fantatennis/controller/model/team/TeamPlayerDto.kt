package com.posadeus.fantatennis.controller.model.team

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * 
 * @param fullName Player's full name
 * @param fantaPoints Player's fanta-points
 */
data class TeamPlayerDto(

    @get:JsonProperty("fullName", required = true) val fullName: String = "",

    @get:JsonProperty("fantaPoints", required = true) val fantaPoints: Double
    ) : Serializable{

    companion object {
        private const val serialVersionUID: Long = 1
    }
}

