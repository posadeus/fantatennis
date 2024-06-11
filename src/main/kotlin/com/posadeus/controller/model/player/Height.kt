package com.posadeus.controller.model.player

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 * @param cm Player's height in cm
 * @param feet 
 */
data class Height(

    @get:JsonProperty("cm", required = true) val cm: Int,

    @get:JsonProperty("feet", required = true) val feet: Feet
)

