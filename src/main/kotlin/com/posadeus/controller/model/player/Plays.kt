package com.posadeus.controller.model.player

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 * @param hand Preferred hand
 * @param backhand Backhand type
 */
data class Plays(

    @get:JsonProperty("hand", required = true) val hand: String,

    @get:JsonProperty("backhand", required = true) val backhand: String
)

