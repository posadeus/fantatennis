package com.posadeus.fantatennis.controller.model.team

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * 
 * @param remove 
 * @param add 
 */
data class PlayersToSwapDto(

    @get:JsonProperty("remove", required = true) val remove: PlayersToRemoveDto = PlayersToRemoveDto(),

    @get:JsonProperty("add", required = true) val add: PlayersToAddDto = PlayersToAddDto()
    ) : Serializable{

    companion object {
        private const val serialVersionUID: Long = 1
    }
}

