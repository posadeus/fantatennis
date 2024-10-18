package com.posadeus.fantatennis.controller.model.team

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * 
 * @param id Team ID
 * @param ownerId Team's Owner ID
 */
data class TeamCreatedDto(

    @get:JsonProperty("id", required = true) val id: Int = 0,

    @get:JsonProperty("ownerId", required = true) val ownerId: String = ""
    ) : Serializable{

    companion object {
        private const val serialVersionUID: Long = 1
    }
}

