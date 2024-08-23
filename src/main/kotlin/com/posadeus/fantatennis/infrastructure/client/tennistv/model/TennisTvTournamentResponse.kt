package com.posadeus.fantatennis.infrastructure.client.tennistv.model

sealed interface TennisTvTournamentResponse

data class TennisTvTournamentOkResponse(val tournament: TournamentResponse): TennisTvTournamentResponse
data object TennisTvTournamentErrorResponse : TennisTvTournamentResponse


data class TournamentResponse(val ms: MS,
                              val MD: Any,
                              val QS: Any)

data class MS(val EventTypeCode: String,
              val Description: String,
              val DrawSize: Int,
              val NumByes: Int,
              val HasRoundRobin: Boolean,
              val IsTeamEvent: Boolean,
              val Breakdown: Any,
              val RoundRobinRound: Any,
              val Rounds: Array<Round>,
              val Withdrawals: Any)

data class Round(val RoundId: Int,
                 val RoundIdModernized: Int,
                 val RoundName: String,
                 val Fixtures: Array<Fixture>,
                 val TeamFixtures: Array<Any>)

data class Fixture(val MatchCode: String,
                   val Result: Result,
                   val DrawLineTop: Any,
                   val DrawLineBottom: Any,
                   val TopPlaceholderText: String,
                   val BottomPlaceholderText: String,
                   val IsTopKnown: Boolean,
                   val IsBottomKnown: Boolean,
                   val Winner: Int,
                   val ResultString: String,
                   val PulseStatus: String,
                   val Match: Match?)

data class Result(val MatchCode: Any,
                  val IsDoubles: Any,
                  val DrawLineTop: Any,
                  val DrawLineBottom: Any,
                  val ScoringType: Any,
                  val NumSets: Any,
                  val CourtSequence: Any,
                  val Day: Any,
                  val Winner: Any,
                  val ResultString: Any,
                  val ResultReason: Any,
                  val ResultType: Any,
                  val Umpire: Any,
                  val TeamTop: ResultTeam,
                  val TeamBottom: ResultTeam,
                  val SetResults: Any,
                  val MatchTime: Any)

data class ResultTeam(val Player: ResultTeamPlayer?,
                      val Partner: Any)

data class ResultTeamPlayer(val PlayerId: String,
                            val FirstName: Any,
                            val LastName: Any,
                            val Nationality: Any,
                            val OrderInTeam: Any)

data class Match(val MatchId: String,
                 val UmpireFirstName: String,
                 val UmpireLastName: String,
                 val DateSeq: Int,
                 val MatchDate: String,
                 val CourtId: Int,
                 val CourtName: String,
                 val Round: MatchRound,
                 val MatchTime: String,
                 val NumberOfSets: Int,
                 val Status: String,
                 val WinningPlayerId: String,
                 val Reason: Any,
                 val ResultString: String,
                 val Serve: Any,
                 val PlayerTeam1: PlayerTeam,
                 val PlayerTeam2: PlayerTeam,
                 val TournamentId: Int,
                 val TournamentYear: Int,
                 val PulseStatus: String,
                 val CourtSeq: Int)

data class MatchRound(val ShortName: String,
                      val LongName: String)

data class PlayerTeam(val PlayerId: String,
                      val PartnerId: Any,
                      val PlayerFirstName: String,
                      val PlayerFirstNameFull: String,
                      val PlayerLastName: String,
                      val PlayerCountryCode: String,
                      val PartnerFirstName: String,
                      val PartnerFirstNameFull: Any,
                      val PartnerLastName: Any,
                      val PartnerCountryCode: Any,
                      val SeedPlayerTeam: Int,
                      val EntryStatusPlayerTeam: Any,
                      val GamePointsPlayerTeam: Any,
                      val Sets: Array<Any>)