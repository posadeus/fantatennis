package com.posadeus.fantatennis.infrastructure.client.ausopen.model

import com.posadeus.fantatennis.infrastructure.client.ausopen.model.AusOpenMatchBuilder.Companion.anAusOpenMatch
import com.posadeus.fantatennis.infrastructure.client.ausopen.model.AusOpenPlayerBuilder.Companion.anAusOpenPlayer
import com.posadeus.fantatennis.infrastructure.client.ausopen.model.AusOpenRoundBuilder.Companion.anAusOpenRound
import com.posadeus.fantatennis.infrastructure.client.ausopen.model.AusOpenTeamDefinitionBuilder.Companion.anAusOpenTeamDefinition

class AusOpenOkResponseBuilder(private var tournament: Any? = null,
                               private var year: Any? = null,
                               private var event: Any? = null,
                               private var matches: List<AusOpenMatch> = listOf(anAusOpenMatch().build()),
                               private var courts: Any? = null,
                               private var players: List<AusOpenPlayer> = listOf(anAusOpenPlayer().build()),
                               private var teams: List<AusOpenTeamDefinition> = listOf(anAusOpenTeamDefinition().build()),
                               private var rounds: List<AusOpenRound> = listOf(anAusOpenRound().build())) {

  fun withMatches(matches: List<AusOpenMatch>): AusOpenOkResponseBuilder {
    this.matches = matches
    return this
  }

  fun withPlayers(players: List<AusOpenPlayer>): AusOpenOkResponseBuilder {
    this.players = players
    return this
  }

  fun withTeams(teams: List<AusOpenTeamDefinition>): AusOpenOkResponseBuilder {
    this.teams = teams
    return this
  }

  fun withRounds(rounds: List<AusOpenRound>): AusOpenOkResponseBuilder {
    this.rounds = rounds
    return this
  }

  fun build() = AusOpenOkResponse(tournament,
                                  year,
                                  event,
                                  matches,
                                  courts,
                                  players,
                                  teams,
                                  rounds)

  companion object {

    fun anAusOpenOkResponse() =
        AusOpenOkResponseBuilder()
  }
}