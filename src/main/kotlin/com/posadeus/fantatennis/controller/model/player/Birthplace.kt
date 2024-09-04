package com.posadeus.fantatennis.controller.model.player

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * 
 * @param city Born town's name
 * @param country Born country's name
 */
data class Birthplace(

    @get:JsonProperty("city", required = true) val city: String,

    @get:JsonProperty("country", required = true) val country: String
    ) : Serializable{

    companion object {
        private const val serialVersionUID: Long = 1
    }
}

