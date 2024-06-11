package com.posadeus.fantatennis.infrastructure.client.atptour.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

sealed interface AtpTourRankingResponse

data class AtpTourRankingsOkResponse(val ranking: List<AtpTourRankingOkResponse>) : AtpTourRankingResponse
data object AtpTourRankingErrorResponse : AtpTourRankingResponse

@JsonIgnoreProperties(ignoreUnknown = true)
data class AtpTourRankingOkResponse(val rankNo: Int,
                                    val name: String,
                                    val points: String,
                                    val urlHeadshotImage: String,
                                    val urlCountryFlag: String,
                                    val movement: Int,
                                    val country: String,
                                    val countryCode: String,
                                    val playerId: String,
                                    val playerProfileUrl: String)