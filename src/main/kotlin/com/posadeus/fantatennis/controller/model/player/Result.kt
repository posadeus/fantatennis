package com.posadeus.fantatennis.controller.model.player

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * 
 * @param bestRank Best ranking
 * @param wonGames Won games
 * @param lostGames Lost games
 * @param titles Won titles
 * @param rank Player's ranking
 * @param points Points
 * @param prizeMoney Won money
 */
data class Result(

    @get:JsonProperty("bestRank", required = true) val bestRank: Int,

    @get:JsonProperty("wonGames", required = true) val wonGames: Int = 0,

    @get:JsonProperty("lostGames", required = true) val lostGames: Int = 0,

    @get:JsonProperty("titles", required = true) val titles: Int = 0,

    @get:JsonProperty("rank") val rank: Int? = null,

    @get:JsonProperty("points") val points: Int? = 0,

    @get:JsonProperty("prizeMoney") val prizeMoney: String? = null
    ) : Serializable{

    companion object {
        private const val serialVersionUID: Long = 1
    }
}

