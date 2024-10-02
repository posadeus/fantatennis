package com.posadeus.fantatennis.infrastructure.client.ausopen.model

class AusOpenPlayerBuilder(private var uuid: String = "A_PLAYER_UUID",
                           private var nid: Any? = null,
                           private var player_id: Any? = null,
                           private var tour_id: String = "ATPA_PLAYER_TOUR_ID",
                           private var first_name: Any? = null,
                           private var last_name: Any? = null,
                           private var full_name: Any? = null,
                           private var short_name: Any? = null,
                           private var gender: Any? = null,
                           private var nationality: Any? = null,
                           private var hero_image: Any? = null,
                           private var hero_image_144: Any? = null,
                           private var hero_image_240: Any? = null,
                           private var profile_image_104: Any? = null,
                           private var profile_image_32: Any? = null,
                           private var image: Any? = null,
                           private var player_icon: Any? = null,
                           private var rankings: Any? = null,
                           private var promoted: Any? = null,
                           private var promoted_weight: Any? = null,
                           private var birth_place: Any? = null,
                           private var dob: Any? = null,
                           private var career_loses: Any? = null,
                           private var career_prize_money: Any? = null,
                           private var career_titles: Any? = null,
                           private var career_wins: Any? = null,
                           private var coach: Any? = null,
                           private var player_height: Any? = null,
                           private var player_weight: Any? = null,
                           private var turned_pro: Any? = null,
                           private var resident_of: Any? = null,
                           private var events_contested: Any? = null,
                           private var profile_link: Any? = null) {

  fun withUuid(uuid: String): AusOpenPlayerBuilder {
    this.uuid = uuid
    return this
  }

  fun withTourId(tour_id: String): AusOpenPlayerBuilder {
    this.tour_id = tour_id
    return this
  }

  fun build() = AusOpenPlayer(uuid,
                              nid,
                              player_id,
                              tour_id,
                              first_name,
                              last_name,
                              full_name,
                              short_name,
                              gender,
                              nationality,
                              hero_image,
                              hero_image_144,
                              hero_image_240,
                              profile_image_104,
                              profile_image_32,
                              image,
                              player_icon,
                              rankings,
                              promoted,
                              promoted_weight,
                              birth_place,
                              dob,
                              career_loses,
                              career_prize_money,
                              career_titles,
                              career_wins,
                              coach,
                              player_height,
                              player_weight,
                              turned_pro,
                              resident_of,
                              events_contested,
                              profile_link)

  companion object {

    fun anAusOpenPlayer() =
        AusOpenPlayerBuilder()
  }
}