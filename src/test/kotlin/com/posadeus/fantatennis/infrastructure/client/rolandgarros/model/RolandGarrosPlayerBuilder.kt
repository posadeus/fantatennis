package com.posadeus.fantatennis.infrastructure.client.rolandgarros.model

class RolandGarrosPlayerBuilder(private var id: Long = 123L,
                                private var firstName: Any? = null,
                                private var lastName: Any? = null,
                                private var shortName: Any? = null,
                                private var shortNameLowercase: Any? = null,
                                private var lastNameLowercase: Any? = null,
                                private var ranking: Any? = null,
                                private var rankingDouble: Any? = null,
                                private var country: Any? = null,
                                private var sex: Any? = null,
                                private var playerCardUrl: Any? = null,
                                private var imageMarkup: Any? = null,
                                private var imageUrl: Any? = null,
                                private var hasService: Any? = null,
                                private var countryName: Any? = null,
                                private var birth: Any? = null,
                                private var info: Any? = null,
                                private var palmaresData: Any? = null,
                                private var acceptedEvents: Any? = null) {

  fun withId(id: Long): RolandGarrosPlayerBuilder {
    this.id = id
    return this
  }

  fun build() = RolandGarrosPlayer(id,
                                   firstName,
                                   lastName,
                                   shortName,
                                   shortNameLowercase,
                                   lastNameLowercase,
                                   ranking,
                                   rankingDouble,
                                   country,
                                   sex,
                                   playerCardUrl,
                                   imageMarkup,
                                   imageUrl,
                                   hasService,
                                   countryName,
                                   birth,
                                   info,
                                   palmaresData,
                                   acceptedEvents)

  companion object {

    fun aRolandGarrosPlayer() =
        RolandGarrosPlayerBuilder()
  }
}