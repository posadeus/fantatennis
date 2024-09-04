package com.posadeus.fantatennis.controller.model.player

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * 
 * @param ft Feet measure
 * @param &#x60;in&#x60; In measure
 */
data class Feet(

    @get:JsonProperty("ft", required = true) val ft: Int,

    @get:JsonProperty("in", required = true) val `in`: Int
    ) : Serializable{

    companion object {
        private const val serialVersionUID: Long = 1
    }
}

