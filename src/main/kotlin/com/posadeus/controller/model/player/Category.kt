package com.posadeus.controller.model.player

import com.fasterxml.jackson.annotation.JsonProperty

/**
 *
 * @param career 
 * @param ytd 
 */
data class Category(

    @get:JsonProperty("career", required = true) val career: Result,

    @get:JsonProperty("ytd", required = true) val ytd: Result
)

