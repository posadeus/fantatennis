package com.posadeus.fantatennis.controller.model.player

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * 
 * @param career 
 * @param ytd 
 */
data class Category(

    @get:JsonProperty("career", required = true) val career: Result,

    @get:JsonProperty("ytd", required = true) val ytd: Result
    ) : Serializable{

    companion object {
        private const val serialVersionUID: Long = 1
    }
}

