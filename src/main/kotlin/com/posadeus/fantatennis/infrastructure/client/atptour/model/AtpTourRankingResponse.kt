package com.posadeus.fantatennis.infrastructure.client.atptour.model

sealed interface AtpTourRankingResponse

data class AtpTourRankingsOkResponse(val ranking: List<AtpTourRankingOkResponse>) : AtpTourRankingResponse
data object AtpTourRankingErrorResponse : AtpTourRankingResponse

//@JsonIgnoreProperties(ignoreUnknown = true)
data class AtpTourRankingOkResponse(val RankNo: Int,
                                    val Name: String,
                                    val Points: String,
                                    val UrlHeadshotImage: String,
                                    val UrlCountryFlag: String,
                                    val Movement: Int,
                                    val Country: String,
                                    val CountryCode: String,
                                    val PlayerId: String,
                                    val PlayerProfileUrl: String)