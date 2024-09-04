package com.posadeus.fantatennis.controller.model.player

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * 
 * @param hand Preferred hand
 * @param backhand Backhand type
 */
data class Plays(

    @get:JsonProperty("hand", required = true) val hand: String,

    @get:JsonProperty("backhand", required = true) val backhand: String
    ) : Serializable{

    companion object {
        private const val serialVersionUID: Long = 1
    }
}

