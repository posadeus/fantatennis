package com.posadeus.fantatennis.infrastructure.client.tennistv.model

sealed interface TennisTvTournamentResponse

data class TennisTvTournamentOkResponse(val tournament: TournamentResponse): TennisTvTournamentResponse
data object TennisTvTournamentErrorResponse : TennisTvTournamentResponse


data class TournamentResponse(val MS: MS,
                              val MD: Any?,
                              val QS: Any?)

data class MS(val EventTypeCode: String?,
              val Description: String?,
              val DrawSize: Int,
              val NumByes: Int,
              val HasRoundRobin: Boolean?,
              val IsTeamEvent: Boolean?,
              val Breakdown: Array<Breakdown>,
              val RoundRobinRound: Any?,
              val Rounds: Array<Round>,
              val Withdrawals: Any?) {

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as MS

    if (EventTypeCode != other.EventTypeCode) return false
    if (Description != other.Description) return false
    if (DrawSize != other.DrawSize) return false
    if (NumByes != other.NumByes) return false
    if (HasRoundRobin != other.HasRoundRobin) return false
    if (IsTeamEvent != other.IsTeamEvent) return false
    if (!Breakdown.contentEquals(other.Breakdown)) return false
    if (RoundRobinRound != other.RoundRobinRound) return false
    if (!Rounds.contentEquals(other.Rounds)) return false
    if (Withdrawals != other.Withdrawals) return false

    return true
  }

  override fun hashCode(): Int {
    var result = EventTypeCode?.hashCode() ?: 0
    result = 31 * result + (Description?.hashCode() ?: 0)
    result = 31 * result + DrawSize
    result = 31 * result + NumByes
    result = 31 * result + (HasRoundRobin?.hashCode() ?: 0)
    result = 31 * result + (IsTeamEvent?.hashCode() ?: 0)
    result = 31 * result + Breakdown.contentHashCode()
    result = 31 * result + (RoundRobinRound?.hashCode() ?: 0)
    result = 31 * result + Rounds.contentHashCode()
    result = 31 * result + (Withdrawals?.hashCode() ?: 0)
    return result
  }
}

data class Breakdown(val Id: Int?,
                     val RoundIdModernized: Int?,
                     val Name: String?,
                     val PrizeMoney: String?,
                     val Points: String)

data class Round(val RoundId: Int,
                 val RoundIdModernized: Int?,
                 val RoundName: String,
                 val Fixtures: Array<Fixture>,
                 val TeamFixtures: Array<Any>?) {

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Round

    if (RoundId != other.RoundId) return false
    if (RoundIdModernized != other.RoundIdModernized) return false
    if (RoundName != other.RoundName) return false
    if (!Fixtures.contentEquals(other.Fixtures)) return false
    if (TeamFixtures != null) {
      if (other.TeamFixtures == null) return false
      if (!TeamFixtures.contentEquals(other.TeamFixtures)) return false
    }
    else if (other.TeamFixtures != null) return false

    return true
  }

  override fun hashCode(): Int {
    var result = RoundId
    result = 31 * result + (RoundIdModernized ?: 0)
    result = 31 * result + RoundName.hashCode()
    result = 31 * result + Fixtures.contentHashCode()
    result = 31 * result + (TeamFixtures?.contentHashCode() ?: 0)
    return result
  }
}

data class Fixture(val MatchCode: String?,
                   val Result: Result?,
                   val DrawLineTop: Any?,
                   val DrawLineBottom: Any?,
                   val TopPlaceholderText: String?,
                   val BottomPlaceholderText: String?,
                   val IsTopKnown: Boolean?,
                   val IsBottomKnown: Boolean?,
                   val Winner: Int?,
                   val ResultString: String?,
                   val PulseStatus: String?,
                   val Match: Match?)

data class Result(val MatchCode: Any?,
                  val IsDoubles: Any?,
                  val DrawLineTop: Any?,
                  val DrawLineBottom: Any?,
                  val ScoringType: Any?,
                  val NumSets: Any?,
                  val CourtSequence: Any?,
                  val Day: Any?,
                  val Winner: Any?,
                  val ResultString: Any?,
                  val ResultReason: Any?,
                  val ResultType: Any?,
                  val Umpire: Any?,
                  val TeamTop: ResultTeam,
                  val TeamBottom: ResultTeam,
                  val SetResults: Any?,
                  val MatchTime: Any?)

data class ResultTeam(val Player: ResultTeamPlayer?,
                      val Partner: Any?)

data class ResultTeamPlayer(val PlayerId: String,
                            val FirstName: Any?,
                            val LastName: Any?,
                            val Nationality: Any?,
                            val OrderInTeam: Any?)

data class Match(val MatchId: String?,
                 val UmpireFirstName: String?,
                 val UmpireLastName: String?,
                 val DateSeq: Int?,
                 val MatchDate: String?,
                 val CourtId: Int?,
                 val CourtName: String?,
                 val Round: MatchRound,
                 val MatchTime: String?,
                 val NumberOfSets: Int?,
                 val Status: String?,
                 val WinningPlayerId: String,
                 val Reason: Any?,
                 val ResultString: String?,
                 val Serve: Any?,
                 val PlayerTeam1: PlayerTeam,
                 val PlayerTeam2: PlayerTeam,
                 val TournamentId: Int,
                 val TournamentYear: Int,
                 val PulseStatus: String?,
                 val CourtSeq: Int?)

data class MatchRound(val ShortName: String,
                      val LongName: String)

data class PlayerTeam(val PlayerId: String,
                      val PartnerId: Any?,
                      val PlayerFirstName: String?,
                      val PlayerFirstNameFull: String?,
                      val PlayerLastName: String?,
                      val PlayerCountryCode: String?,
                      val PartnerFirstName: String?,
                      val PartnerFirstNameFull: Any?,
                      val PartnerLastName: Any?,
                      val PartnerCountryCode: Any?,
                      val SeedPlayerTeam: Int?,
                      val EntryStatusPlayerTeam: Any?,
                      val GamePointsPlayerTeam: Any?,
                      val Sets: Array<Any>?) {

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as PlayerTeam

    if (PlayerId != other.PlayerId) return false
    if (PartnerId != other.PartnerId) return false
    if (PlayerFirstName != other.PlayerFirstName) return false
    if (PlayerFirstNameFull != other.PlayerFirstNameFull) return false
    if (PlayerLastName != other.PlayerLastName) return false
    if (PlayerCountryCode != other.PlayerCountryCode) return false
    if (PartnerFirstName != other.PartnerFirstName) return false
    if (PartnerFirstNameFull != other.PartnerFirstNameFull) return false
    if (PartnerLastName != other.PartnerLastName) return false
    if (PartnerCountryCode != other.PartnerCountryCode) return false
    if (SeedPlayerTeam != other.SeedPlayerTeam) return false
    if (EntryStatusPlayerTeam != other.EntryStatusPlayerTeam) return false
    if (GamePointsPlayerTeam != other.GamePointsPlayerTeam) return false
    if (Sets != null) {
      if (other.Sets == null) return false
      if (!Sets.contentEquals(other.Sets)) return false
    }
    else if (other.Sets != null) return false

    return true
  }

  override fun hashCode(): Int {
    var result = PlayerId.hashCode()
    result = 31 * result + (PartnerId?.hashCode() ?: 0)
    result = 31 * result + (PlayerFirstName?.hashCode() ?: 0)
    result = 31 * result + (PlayerFirstNameFull?.hashCode() ?: 0)
    result = 31 * result + (PlayerLastName?.hashCode() ?: 0)
    result = 31 * result + (PlayerCountryCode?.hashCode() ?: 0)
    result = 31 * result + (PartnerFirstName?.hashCode() ?: 0)
    result = 31 * result + (PartnerFirstNameFull?.hashCode() ?: 0)
    result = 31 * result + (PartnerLastName?.hashCode() ?: 0)
    result = 31 * result + (PartnerCountryCode?.hashCode() ?: 0)
    result = 31 * result + (SeedPlayerTeam ?: 0)
    result = 31 * result + (EntryStatusPlayerTeam?.hashCode() ?: 0)
    result = 31 * result + (GamePointsPlayerTeam?.hashCode() ?: 0)
    result = 31 * result + (Sets?.contentHashCode() ?: 0)
    return result
  }
}