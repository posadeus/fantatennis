package com.posadeus.fantatennis.infrastructure.client.ausopen.model

sealed interface AusOpenResponse

data object AusOpenErrorResponse : AusOpenResponse
data class AusOpenOkResponse(val tournament: Any?,
                             val year: Any?,
                             val event: Any?,
                             val matches: List<AusOpenMatch>,
                             val courts: Any?,
                             val players: List<AusOpenPlayer>,
                             val teams: List<AusOpenTeamDefinition>,
                             val rounds: List<AusOpenRound>) : AusOpenResponse

data class AusOpenMatch(val date: Any?,
                        val actual_start_time: Any?,
                        val match_centre_link: Any?,
                        val uuid: Any?,
                        val match_id: Any?,
                        val team_substituted_footnote: Any?,
                        val team_substituted: Any?,
                        val id: Any?,
                        val order: Any?,
                        val promoted: Any?,
                        val promoted_weight: Any?,
                        val match_status: Any?,
                        val match_state: Any?,
                        val round_id: String,
                        val duration: Any?,
                        val teams: List<AusOpenTeam>,
                        val event_uuid: Any?,
                        val court_id: Any?,
                        val session: Any?,
                        val session_order: Any?,
                        val restricted_start_time: Any?,
                        val restricted_start_time_timestamp: Any?,
                        val activity_order: Any?)

data class AusOpenTeam(val team_id: String,
                       val score: Any?,
                       val status: String?)

data class AusOpenPlayer(val uuid: String,
                         val nid: Any?,
                         val player_id: Any?,
                         val tour_id: String,
                         val first_name: Any?,
                         val last_name: Any?,
                         val full_name: Any?,
                         val short_name: Any?,
                         val gender: Any?,
                         val nationality: Any?,
                         val hero_image: Any?,
                         val hero_image_144: Any?,
                         val hero_image_240: Any?,
                         val profile_image_104: Any?,
                         val profile_image_32: Any?,
                         val image: Any?,
                         val player_icon: Any?,
                         val rankings: Any?,
                         val promoted: Any?,
                         val promoted_weight: Any?,
                         val birth_place: Any?,
                         val dob: Any?,
                         val career_loses: Any?,
                         val career_prize_money: Any?,
                         val career_titles: Any?,
                         val career_wins: Any?,
                         val coach: Any?,
                         val player_height: Any?,
                         val player_weight: Any?,
                         val turned_pro: Any?,
                         val resident_of: Any?,
                         val events_contested: Any?,
                         val profile_link: Any?)

data class AusOpenTeamDefinition(val uuid: String,
                                 val seed: Any?,
                                 val entry_status: Any?,
                                 val players: List<String>)

data class AusOpenRound(val uuid: String,
                        val name: String)