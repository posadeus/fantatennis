package com.posadeus.fantatennis.controller.model.team

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * 
 * @param ownerId 
 */
data class TeamToCreateDto(

    @get:JsonProperty("ownerId", required = true) val ownerId: String = ""
    ) : Serializable{

    companion object {
        private const val serialVersionUID: Long = 1
    }
}

