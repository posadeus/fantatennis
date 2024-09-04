package com.posadeus.fantatennis.controller.model.player

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * 
 * @param kg Player's weight in Kg
 * @param lb Player's weight in lb
 */
data class Weight(

    @get:JsonProperty("kg", required = true) val kg: Int,

    @get:JsonProperty("lb", required = true) val lb: Int
    ) : Serializable{

    companion object {
        private const val serialVersionUID: Long = 1
    }
}

