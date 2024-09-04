package com.posadeus.fantatennis.controller.model.player

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * 
 * @param name Origin country's name
 * @param flag Country's flag link
 */
data class Country(

    @get:JsonProperty("name", required = true) val name: String,

    @get:JsonProperty("flag", required = true) val flag: String
    ) : Serializable{

    companion object {
        private const val serialVersionUID: Long = 1
    }
}

