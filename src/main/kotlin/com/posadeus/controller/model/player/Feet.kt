package com.posadeus.controller.model.player

import com.fasterxml.jackson.annotation.JsonProperty

/**
 *
 * @param ft Feet measure
 * @param &#x60;in&#x60; In measure
 */
data class Feet(

    @get:JsonProperty("ft", required = true) val ft: Int,

    @get:JsonProperty("in", required = true) val `in`: Int
)

