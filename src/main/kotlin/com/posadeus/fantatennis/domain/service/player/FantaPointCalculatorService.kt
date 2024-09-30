package com.posadeus.fantatennis.domain.service.player

import com.posadeus.fantatennis.domain.infrastructure.TournamentInfoRepository
import com.posadeus.fantatennis.domain.infrastructure.TournamentsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.Round.*

class FantaPointCalculatorService(private val tournamentInfoRepositories: List<TournamentInfoRepository>,
                                  private val tournamentsRepository: TournamentsRepository) {

  fun calculateFantaPointsFor(tournamentId: Int, year: Int): Set<AtpPlayer> {

    val tournament = tournamentsRepository.readTournaments().associateBy { it.id }[tournamentId]
    val tennisTvId = tournament?.tennisTvId
                     ?: return emptySet()

    val tournamentInfo =
        tournamentInfoRepositories
            .first { it.canProcess(tennisTvId) }
            .retrieveTournamentInfo(tennisTvId, year)

    return when (tournamentInfo) {

      is CompleteTournamentInfo -> {

        when (tournament.points) {

          2000 -> getScores(tournamentInfo, scoresRules2000)
          1000 -> getScores(tournamentInfo, scoresRules1000)
          500 -> getScores(tournamentInfo, scoresRules500)
          250 -> getScores(tournamentInfo, scoresRules250)
          else -> emptyMap()
        }.map { toAtpPlayer(it, year, tournamentId) }
        .toSet()
      }

      is ErrorTournamentInfo -> emptySet()
    }
  }

  private fun getScores(tournamentInfo: CompleteTournamentInfo,
                        scoreRules: Map<String, Double>): Map<AtpPlayerId, Double> {

    val scoresFirstRound: Map<AtpPlayerId, Double>
    val scoresSecondRound: Map<AtpPlayerId, Double>
    val scoresThirdRound: Map<AtpPlayerId, Double>
    val scoresFourthRound: Map<AtpPlayerId, Double>
    val scoresQuarterfinals: Map<AtpPlayerId, Double>

    if (tournamentInfo.winners[R4] != null) {

      scoresFirstRound = scoresFor(tournamentInfo.participants, scoreRules["Q"])
      scoresSecondRound = scoresFor(tournamentInfo.winners[R1]!!, scoreRules["R1"])
      scoresThirdRound = scoresFor(tournamentInfo.winners[R2]!!, scoreRules["R2"])
      scoresFourthRound = scoresFor(tournamentInfo.winners[R3]!!, scoreRules["R3"])
      scoresQuarterfinals = scoresFor(tournamentInfo.winners[R4]!!, scoreRules["QF"])
    }
    else if (tournamentInfo.winners[R3] != null) {

      scoresFirstRound = emptyMap()
      scoresSecondRound = scoresFor(tournamentInfo.participants, scoreRules["R1"])
      scoresThirdRound = scoresFor(tournamentInfo.winners[R1]!!, scoreRules["R2"])
      scoresFourthRound = scoresFor(tournamentInfo.winners[R2]!!, scoreRules["R3"])
      scoresQuarterfinals = scoresFor(tournamentInfo.winners[R3]!!, scoreRules["QF"])
    }
    else {

      scoresFirstRound = emptyMap()
      scoresSecondRound = emptyMap()
      scoresThirdRound = scoresFor(tournamentInfo.participants, scoreRules["R2"])
      scoresFourthRound = scoresFor(tournamentInfo.winners[R1]!!, scoreRules["R3"])
      scoresQuarterfinals = scoresFor(tournamentInfo.winners[R2]!!, scoreRules["QF"])
    }

    val scoresSemifinals = scoresFor(tournamentInfo.winners[QF]!!, scoreRules["SF"])
    val scoresRunnerUp = scoresFor(tournamentInfo.winners[SF]!!, scoreRules["RU"])
    val scoresWinner = scoresFor(tournamentInfo.winners[F]!!, scoreRules["W"])

    return scoresFirstRound +
           scoresSecondRound +
           scoresThirdRound +
           scoresFourthRound +
           scoresQuarterfinals +
           scoresSemifinals +
           scoresRunnerUp +
           scoresWinner
  }

  private fun scoresFor(playerIds: Set<AtpPlayerId>, scores: Double?) =
      playerIds.associateWith { scores!! }

  private fun toAtpPlayer(scoresByPlayer: Map.Entry<AtpPlayerId, Double>,
                          year: Int,
                          tournamentId: Int) =
      AtpPlayer(id = scoresByPlayer.key,
                tournamentPoints = mapOf(year to mapOf(tournamentId to scoresByPlayer.value)))

  companion object {

    private val scoresRules2000 = mapOf("W" to 80.0,
                                        "RU" to 56.0,
                                        "SF" to 32.0,
                                        "QF" to 16.0,
                                        "R3" to 8.0,
                                        "R2" to 4.0,
                                        "R1" to 2.0,
                                        "Q" to 0.0)
    private val scoresRules1000 = mapOf("W" to 40.0,
                                        "RU" to 28.0,
                                        "SF" to 16.0,
                                        "QF" to 8.0,
                                        "R3" to 4.0,
                                        "R2" to 2.0,
                                        "R1" to 1.0,
                                        "Q" to 0.0)
    private val scoresRules500 = mapOf("W" to 20.0,
                                       "RU" to 14.0,
                                       "SF" to 8.0,
                                       "QF" to 4.0,
                                       "R3" to 2.0,
                                       "R2" to 1.0,
                                       "R1" to 0.0,
                                       "Q" to 0.0)
    private val scoresRules250 = mapOf("W" to 10.0,
                                       "RU" to 7.0,
                                       "SF" to 4.0,
                                       "QF" to 2.0,
                                       "R3" to 1.0,
                                       "R2" to 0.0,
                                       "R1" to 0.0,
                                       "Q" to 0.0)
  }
}