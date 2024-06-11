package com.posadeus.fantatennis.infrastructure.client.atptour.model

class AtpTourRankingOkResponseBuilder(private var rankNo: Int = 0,
                                      private var name: String = "",
                                      private var points: String = "0,0",
                                      private var urlHeadshotImage: String = "",
                                      private var urlCountryFlag: String = "",
                                      private var movement: Int = 0,
                                      private var country: String = "",
                                      private var countryCode: String = "",
                                      private var playerId: String = "",
                                      private var playerProfileUrl: String = "") {

  fun withRankNo(rankNo: Int): AtpTourRankingOkResponseBuilder {
    this.rankNo = rankNo
    return this
  }

  fun withName(name: String): AtpTourRankingOkResponseBuilder {
    this.name = name
    return this
  }

  fun withPoints(points: String): AtpTourRankingOkResponseBuilder {
    this.points = points
    return this
  }

  fun withUrlHeadshotImage(urlHeadshotImage: String): AtpTourRankingOkResponseBuilder {
    this.urlHeadshotImage = urlHeadshotImage
    return this
  }

  fun withUrlCountryFlag(urlCountryFlag: String): AtpTourRankingOkResponseBuilder {
    this.urlCountryFlag = urlCountryFlag
    return this
  }

  fun withMovement(movement: Int): AtpTourRankingOkResponseBuilder {
    this.movement = movement
    return this
  }

  fun withCountry(country: String): AtpTourRankingOkResponseBuilder {
    this.country = country
    return this
  }

  fun withCountryCode(countryCode: String): AtpTourRankingOkResponseBuilder {
    this.countryCode = countryCode
    return this
  }

  fun withPlayerId(playerId: String): AtpTourRankingOkResponseBuilder {
    this.playerId = playerId
    return this
  }

  fun withPlayerProfileUrl(playerProfileUrl: String): AtpTourRankingOkResponseBuilder {
    this.playerProfileUrl = playerProfileUrl
    return this
  }

  fun build() = AtpTourRankingOkResponse(rankNo,
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

    fun anAtpTourRankingOkResponse() =
        AtpTourRankingOkResponseBuilder()
  }
}
