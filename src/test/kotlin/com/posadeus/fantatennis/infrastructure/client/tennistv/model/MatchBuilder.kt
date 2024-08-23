package com.posadeus.fantatennis.infrastructure.client.tennistv.model

import com.posadeus.fantatennis.infrastructure.client.tennistv.model.MatchRoundBuilder.Companion.aMatchRound
import com.posadeus.fantatennis.infrastructure.client.tennistv.model.PlayerTeamBuilder.Companion.aPlayerTeam

class MatchBuilder(private var MatchId: String? = null,
                   private var UmpireFirstName: String? = null,
                   private var UmpireLastName: String? = null,
                   private var DateSeq: Int? = null,
                   private var MatchDate: String? = null,
                   private var CourtId: Int? = null,
                   private var CourtName: String? = null,
                   private var Round: MatchRound = aMatchRound().build(),
                   private var MatchTime: String? = null,
                   private var NumberOfSets: Int? = null,
                   private var Status: String? = null,
                   private var WinningPlayerId: String = "WinningPlayerId",
                   private var Reason: Any? = null,
                   private var ResultString: String? = null,
                   private var Serve: Any? = null,
                   private var PlayerTeam1: PlayerTeam = aPlayerTeam().build(),
                   private var PlayerTeam2: PlayerTeam = aPlayerTeam().build(),
                   private var TournamentId: Int = 1,
                   private var TournamentYear: Int = 2000,
                   private var PulseStatus: String? = null,
                   private var CourtSeq: Int? = null) {

  fun withRound(Round: MatchRound): MatchBuilder {
    this.Round = Round
    return this
  }

  fun withWinningPlayerId(WinningPlayerId: String): MatchBuilder {
    this.WinningPlayerId = WinningPlayerId
    return this
  }

  fun withPlayerTeam1(PlayerTeam1: PlayerTeam): MatchBuilder {
    this.PlayerTeam1 = PlayerTeam1
    return this
  }

  fun withPlayerTeam2(PlayerTeam2: PlayerTeam): MatchBuilder {
    this.PlayerTeam2 = PlayerTeam2
    return this
  }

  fun withTournamentId(TournamentId: Int): MatchBuilder {
    this.TournamentId = TournamentId
    return this
  }

  fun withTournamentYear(TournamentYear: Int): MatchBuilder {
    this.TournamentYear = TournamentYear
    return this
  }

  fun build() = Match(MatchId,
                      UmpireFirstName,
                      UmpireLastName,
                      DateSeq,
                      MatchDate,
                      CourtId,
                      CourtName,
                      Round,
                      MatchTime,
                      NumberOfSets,
                      Status,
                      WinningPlayerId,
                      Reason,
                      ResultString,
                      Serve,
                      PlayerTeam1,
                      PlayerTeam2,
                      TournamentId,
                      TournamentYear,
                      PulseStatus,
                      CourtSeq)

  companion object {

    fun aMatch() =
        MatchBuilder()
  }
}