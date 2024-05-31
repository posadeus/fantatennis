package com.posadeus.infrastructure.client.atptour.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class AtpTourRankingResponse(val rankNo: Int,
                                  val name: String,
                                  val points: String,
                                  val urlHeadshotImage: String,
                                  val urlCountryFlag: String,
                                  val movement: Int,
                                  val country: String,
                                  val countryCode: String,
                                  val playerId: String,
                                  val playerProfileUrl: String)