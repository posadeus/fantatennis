package com.posadeus.controller.model.player

import com.fasterxml.jackson.annotation.JsonProperty

/**
 *
 * @param city Born town's name
 * @param country Born country's name
 */
data class Birthplace(

    @get:JsonProperty("city", required = true) val city: String,

    @get:JsonProperty("country", required = true) val country: String
)

