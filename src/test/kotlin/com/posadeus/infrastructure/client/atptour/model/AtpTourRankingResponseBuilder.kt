package com.posadeus.infrastructure.client.atptour.model

class AtpTourRankingResponseBuilder(private var rankNo: Int = 0,
                                    private var name: String = "",
                                    private var points: String = "0,0",
                                    private var urlHeadshotImage: String = "",
                                    private var urlCountryFlag: String = "",
                                    private var movement: Int = 0,
                                    private var country: String = "",
                                    private var countryCode: String = "",
                                    private var playerId: String = "",
                                    private var playerProfileUrl: String = "") {

  fun withRankNo(rankNo: Int): AtpTourRankingResponseBuilder {
    this.rankNo = rankNo
    return this
  }

  fun withName(name: String): AtpTourRankingResponseBuilder {
    this.name = name
    return this
  }

  fun withPoints(points: String): AtpTourRankingResponseBuilder {
    this.points = points
    return this
  }

  fun withUrlHeadshotImage(urlHeadshotImage: String): AtpTourRankingResponseBuilder {
    this.urlHeadshotImage = urlHeadshotImage
    return this
  }

  fun withUrlCountryFlag(urlCountryFlag: String): AtpTourRankingResponseBuilder {
    this.urlCountryFlag = urlCountryFlag
    return this
  }

  fun withMovement(movement: Int): AtpTourRankingResponseBuilder {
    this.movement = movement
    return this
  }

  fun withCountry(country: String): AtpTourRankingResponseBuilder {
    this.country = country
    return this
  }

  fun withCountryCode(countryCode: String): AtpTourRankingResponseBuilder {
    this.countryCode = countryCode
    return this
  }

  fun withPlayerId(playerId: String): AtpTourRankingResponseBuilder {
    this.playerId = playerId
    return this
  }

  fun withPlayerProfileUrl(playerProfileUrl: String): AtpTourRankingResponseBuilder {
    this.playerProfileUrl = playerProfileUrl
    return this
  }

  fun build() = AtpTourRankingResponse(rankNo,
                                       name,
                                       points,
                                       urlHeadshotImage,
                                       urlCountryFlag,
                                       movement,
                                       country,
                                       countryCode,
                                       playerId,
                                       playerProfileUrl)

  companion object {

    fun anAtpTourRankingResponse() =
        AtpTourRankingResponseBuilder()
  }
}
