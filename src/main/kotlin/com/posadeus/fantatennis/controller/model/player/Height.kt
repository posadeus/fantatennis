package com.posadeus.fantatennis.controller.model.player

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * 
 * @param cm Player's height in cm
 * @param feet 
 */
data class Height(

    @get:JsonProperty("cm", required = true) val cm: Int,

    @get:JsonProperty("feet", required = true) val feet: Feet
    ) : Serializable{

    companion object {
        private const val serialVersionUID: Long = 1
    }
}

