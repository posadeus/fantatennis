package com.posadeus.fantatennis.controller.model.tournament

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * 
 * @param ids 
 */
data class TournamentsDto(

    @get:JsonProperty("ids") val ids: List<Int>? = null
    ) : Serializable{

    companion object {
        private const val serialVersionUID: Long = 1
    }
}

