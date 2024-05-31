package com.posadeus.controller.model.ranking

import com.fasterxml.jackson.annotation.JsonProperty

/**
 *
 * @param id PLayer's unique ID
 * @param name Player's name
 * @param surname Player's surname
 * @param age Player's age
 * @param rank Player's ranking
 * @param points Player's points
 */
data class RankedPlayer(

    @get:JsonProperty("id", required = true) val id: Int = 0,

    @get:JsonProperty("name", required = true) val name: String = "",

    @get:JsonProperty("surname", required = true) val surname: String = "",

    @get:JsonProperty("age", required = true) val age: Int = 0,

    @get:JsonProperty("rank", required = true) val rank: Int,

    @get:JsonProperty("points", required = true) val points: Int
)

