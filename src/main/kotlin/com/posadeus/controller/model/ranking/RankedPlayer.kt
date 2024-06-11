package com.posadeus.controller.model.ranking

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 * @param id Player's unique ID
 * @param fullName Player's name
 * @param rank Player's ranking
 * @param points Player's points
 */
data class RankedPlayer(

    @get:JsonProperty("id", required = true) val id: String = "0",

    @get:JsonProperty("fullName", required = true) val fullName: String = "",

    @get:JsonProperty("rank", required = true) val rank: Int,

    @get:JsonProperty("points", required = true) val points: Int
)

