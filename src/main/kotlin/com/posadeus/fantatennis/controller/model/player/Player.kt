package com.posadeus.fantatennis.controller.model.player

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 * @param id Player's unique ID
 * @param name Player's name
 * @param surname Player's surname
 * @param age Player's age
 * @param country 
 * @param single 
 * @param double 
 * @param turnedProYear Player's turning pro year
 * @param weight 
 * @param height 
 * @param birthplace 
 * @param plays 
 */
data class Player(

    @get:JsonProperty("id", required = true) val id: Int = 0,

    @get:JsonProperty("name", required = true) val name: String = "",

    @get:JsonProperty("surname", required = true) val surname: String = "",

    @get:JsonProperty("age", required = true) val age: Int = 0,

    @get:JsonProperty("country", required = true) val country: Country,

    @get:JsonProperty("single", required = true) val single: Category,

    @get:JsonProperty("double", required = true) val double: Category,

    @get:JsonProperty("turnedProYear") val turnedProYear: Int? = 0,

    @get:JsonProperty("weight") val weight: Weight? = null,

    @get:JsonProperty("height") val height: Height? = null,

    @get:JsonProperty("birthplace") val birthplace: Birthplace? = null,

    @get:JsonProperty("plays") val plays: Plays? = null
)

