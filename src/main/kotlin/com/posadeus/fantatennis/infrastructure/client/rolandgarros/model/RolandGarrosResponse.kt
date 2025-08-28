package com.posadeus.fantatennis.infrastructure.client.rolandgarros.model

sealed interface RolandGarrosResponse

data object RolandGarrosErrorResponse : RolandGarrosResponse
data class RolandGarrosOkResponse(val title: Any?,
                                  val description: Any?,
                                  val downloadPdfLabel: Any?,
                                  val fullDrawDownloadPdfLabel: Any?,
                                  val noResultsLabel: Any?,
                                  val searchPlayerPlaceholder: Any?,
                                  val favoriteLabel: Any?,
                                  val types: Any?,
                                  val eventYears: Any?,
                                  val currentRound: Any?,
                                  val tournamentEvent: RolandGarrosTournamentEvent,
                                  val isRgBracketEnabled: Any?,
                                  val bracketButtonLabel: Any?,
                                  val rgBracketUrl: Any?,
                                  val pdfFileName: Any?,
                                  val orLabel: Any?) : RolandGarrosResponse {

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as RolandGarrosOkResponse

    if (title != other.title) return false
    if (description != other.description) return false
    if (downloadPdfLabel != other.downloadPdfLabel) return false
    if (fullDrawDownloadPdfLabel != other.fullDrawDownloadPdfLabel) return false
    if (noResultsLabel != other.noResultsLabel) return false
    if (searchPlayerPlaceholder != other.searchPlayerPlaceholder) return false
    if (favoriteLabel != other.favoriteLabel) return false
    if (types != other.types) return false
    if (eventYears != other.eventYears) return false
    if (currentRound != other.currentRound) return false
    if (tournamentEvent != other.tournamentEvent) return false
    if (isRgBracketEnabled != other.isRgBracketEnabled) return false
    if (bracketButtonLabel != other.bracketButtonLabel) return false
    if (rgBracketUrl != other.rgBracketUrl) return false
    if (pdfFileName != other.pdfFileName) return false
    if (orLabel != other.orLabel) return false

    return true
  }

  override fun hashCode(): Int {
    var result = title?.hashCode() ?: 0
    result = 31 * result + (description?.hashCode() ?: 0)
    result = 31 * result + (downloadPdfLabel?.hashCode() ?: 0)
    result = 31 * result + (fullDrawDownloadPdfLabel?.hashCode() ?: 0)
    result = 31 * result + (noResultsLabel?.hashCode() ?: 0)
    result = 31 * result + (searchPlayerPlaceholder?.hashCode() ?: 0)
    result = 31 * result + (favoriteLabel?.hashCode() ?: 0)
    result = 31 * result + (types?.hashCode() ?: 0)
    result = 31 * result + (eventYears?.hashCode() ?: 0)
    result = 31 * result + (currentRound?.hashCode() ?: 0)
    result = 31 * result + tournamentEvent.hashCode()
    result = 31 * result + (isRgBracketEnabled?.hashCode() ?: 0)
    result = 31 * result + (bracketButtonLabel?.hashCode() ?: 0)
    result = 31 * result + (rgBracketUrl?.hashCode() ?: 0)
    result = 31 * result + (pdfFileName?.hashCode() ?: 0)
    result = 31 * result + (orLabel?.hashCode() ?: 0)
    return result
  }
}

data class RolandGarrosTournamentEvent(val activeRound: Any?,
                                       val totalRounds: Any?,
                                       val roundNavs: Array<RolandGarrosRoundNav>,
                                       val roundResults: Array<RolandGarrosRoundResult>) {

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as RolandGarrosTournamentEvent

    if (activeRound != other.activeRound) return false
    if (totalRounds != other.totalRounds) return false
    if (!roundNavs.contentEquals(other.roundNavs)) return false
    if (!roundResults.contentEquals(other.roundResults)) return false

    return true
  }

  override fun hashCode(): Int {
    var result = activeRound?.hashCode() ?: 0
    result = 31 * result + (totalRounds?.hashCode() ?: 0)
    result = 31 * result + roundNavs.contentHashCode()
    result = 31 * result + roundResults.contentHashCode()
    return result
  }
}

data class RolandGarrosRoundNav(val label: String,
                                val numberOfMatches: Int)

data class RolandGarrosRoundResult(val roundLabel: String,
                                   val roundNumber: Int,
                                   val matches: Array<RolandGarrosMatch>) {

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as RolandGarrosRoundResult

    if (roundLabel != other.roundLabel) return false
    if (roundNumber != other.roundNumber) return false
    if (!matches.contentEquals(other.matches)) return false

    return true
  }

  override fun hashCode(): Int {
    var result = roundLabel.hashCode()
    result = 31 * result + roundNumber
    result = 31 * result + matches.contentHashCode()
    return result
  }
}

data class RolandGarrosMatch(val id: Any?,
                             val url: Any?,
                             val matchData: RolandGarrosMatchData,
                             val teamA: RolandGarrosTeam,
                             val teamB: RolandGarrosTeam,
                             val umpire: Any?,
                             val showUmpire: Any?,
                             val excitementRate: Any?)

data class RolandGarrosMatchData(val type: Any?,
                                 val typeLabel: Any?,
                                 val round: Any?,
                                 val roundLabel: Any?,
                                 val courtName: Any?,
                                 val durationInMinutes: Any?,
                                 val endTimestamp: Any?,
                                 val startingAt: Any?,
                                 val dateSchedule: Any?,
                                 val notBefore: Any?,
                                 val status: Any?,
                                 val statusLabel: String?,
                                 val notBeforeLabel: Any?,
                                 val fromLabel: Any?,
                                 val isNightSession: Any?,
                                 val nightSessionLabel: Any?,
                                 val daySessionLabel: Any?)

data class RolandGarrosTeam(val players: Array<RolandGarrosPlayer>,
                            val sets: Any?,
                            val winner: Boolean,
                            val seed: Any?,
                            val hasService: Any?,
                            val points: Any?,
                            val endCause: Any?,
                            val entryStatus: Any?,
                            val winnerOfLabel: Any?,
                            val winnerOfPart1: Any?,
                            val winnerOfPart2: Any?) {

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as RolandGarrosTeam

    if (!players.contentEquals(other.players)) return false
    if (sets != other.sets) return false
    if (winner != other.winner) return false
    if (seed != other.seed) return false
    if (hasService != other.hasService) return false
    if (points != other.points) return false
    if (endCause != other.endCause) return false
    if (entryStatus != other.entryStatus) return false
    if (winnerOfLabel != other.winnerOfLabel) return false
    if (winnerOfPart1 != other.winnerOfPart1) return false
    if (winnerOfPart2 != other.winnerOfPart2) return false

    return true
  }

  override fun hashCode(): Int {
    var result = players.contentHashCode()
    result = 31 * result + (sets?.hashCode() ?: 0)
    result = 31 * result + winner.hashCode()
    result = 31 * result + seed.hashCode()
    result = 31 * result + (hasService?.hashCode() ?: 0)
    result = 31 * result + (points?.hashCode() ?: 0)
    result = 31 * result + (endCause?.hashCode() ?: 0)
    result = 31 * result + (entryStatus?.hashCode() ?: 0)
    result = 31 * result + (winnerOfLabel?.hashCode() ?: 0)
    result = 31 * result + (winnerOfPart1?.hashCode() ?: 0)
    result = 31 * result + (winnerOfPart2?.hashCode() ?: 0)
    return result
  }
}

data class RolandGarrosPlayer(val id: Long,
                              val firstName: Any?,
                              val lastName: Any?,
                              val shortName: Any?,
                              val shortNameLowercase: Any?,
                              val lastNameLowercase: Any?,
                              val ranking: Any?,
                              val rankingDouble: Any?,
                              val country: Any?,
                              val sex: Any?,
                              val playerCardUrl: Any?,
                              val imageMarkup: Any?,
                              val imageUrl: Any?,
                              val hasService: Any?,
                              val countryName: Any?,
                              val birth: Any?,
                              val info: Any?,
                              val palmaresData: Any?,
                              val acceptedEvents: Any?)