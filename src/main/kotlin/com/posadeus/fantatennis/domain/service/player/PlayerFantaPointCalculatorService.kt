package com.posadeus.fantatennis.domain.service.player

import com.posadeus.fantatennis.domain.infrastructure.TournamentInfoRepository
import com.posadeus.fantatennis.domain.model.*

class PlayerFantaPointCalculatorService(private val tournamentInfoRepository: TournamentInfoRepository) {

  fun calculateFantaPointsFor(tournamentId: Int, year: Int): Set<DomainPlayer> =
      when (val tournamentInfo = tournamentInfoRepository.retrieveTournamentInfo(tournamentId, year)) {

        is CompleteTournamentInfo -> {

          when (tournamentInfo.tournamentType) {

            "1000" -> getScores(tournamentInfo, year, tournamentId, scoresRules1000)
            "500" -> getScores(tournamentInfo, year, tournamentId, scoresRules500)
            "250" -> getScores(tournamentInfo, year, tournamentId, scoresRules250)
            else -> emptySet()
          }
        }

        is ErrorTournamentInfo -> emptySet()
      }

  private fun getScores(tournamentInfo: CompleteTournamentInfo,
                        year: Int,
                        tournamentId: Int,
                        scoreRules: Map<String, Double>): Set<DomainPlayer> {

    val scoresFirstRound = tournamentInfo.participants.associateWith { scoreRules["First Round"]!! }
    val scoresSecondRound = tournamentInfo.winners["First Round"]!!.associateWith { scoreRules["Second Round"]!! }
    val scoresThirdRound = tournamentInfo.winners["Second Round"]!!.associateWith { scoreRules["Third Round"]!! }
    val scoresQuarterfinals = tournamentInfo.winners["Third Round"]!!.associateWith { scoreRules["Quarterfinals"]!! }
    val scoresSemifinals = tournamentInfo.winners["Quarterfinals"]!!.associateWith { scoreRules["Semifinals"]!! }
    val scoresRunnerUp = tournamentInfo.winners["Semifinals"]!!.associateWith { scoreRules["RunnerUp"]!! }
    val scoresWinner = tournamentInfo.winners["Final"]!!.associateWith { scoreRules["Winner"]!! }

    val scoresByPlayer =
        scoresFirstRound + scoresSecondRound + scoresThirdRound + scoresQuarterfinals + scoresSemifinals + scoresRunnerUp + scoresWinner

    return scoresByPlayer
        .map { DomainPlayer(id = it.key, tournamentPoints = mapOf(year to mapOf(tournamentId to it.value))) }
        .toSet()
  }

  companion object {

    private val scoresRules1000 = mapOf("Winner" to 40.0,
                                        "RunnerUp" to 28.0,
                                        "Semifinals" to 16.0,
                                        "Quarterfinals" to 8.0,
                                        "Third Round" to 4.0,
                                        "Second Round" to 2.0,
                                        "First Round" to 1.0)
    private val scoresRules500 = mapOf("Winner" to 20.0,
                                       "RunnerUp" to 14.0,
                                       "Semifinals" to 8.0,
                                       "Quarterfinals" to 4.0,
                                       "Third Round" to 2.0,
                                       "Second Round" to 1.0,
                                       "First Round" to 0.0)
    private val scoresRules250 = mapOf("Winner" to 10.0,
                                       "RunnerUp" to 7.0,
                                       "Semifinals" to 4.0,
                                       "Quarterfinals" to 2.0,
                                       "Third Round" to 1.0,
                                       "Second Round" to 0.0,
                                       "First Round" to 0.0)
  }
}