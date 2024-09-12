package com.posadeus.fantatennis.infrastructure.client.usopen.model

sealed interface UsOpenResponse

data object UsOpenErrorResponse : UsOpenResponse
data class UsOpenOkResponse(val eventName: Any?,
                            val drawSize: Any?,
                            val drawFormat: Any?,
                            val totalRounds: Any?,
                            val prizeMoney: Any?,
                            val matches: Array<UsOpenMatch>) : UsOpenResponse {

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as UsOpenOkResponse

    if (eventName != other.eventName) return false
    if (drawSize != other.drawSize) return false
    if (drawFormat != other.drawFormat) return false
    if (totalRounds != other.totalRounds) return false
    if (prizeMoney != other.prizeMoney) return false
    if (!matches.contentEquals(other.matches)) return false

    return true
  }

  override fun hashCode(): Int {
    var result = eventName?.hashCode() ?: 0
    result = 31 * result + (drawSize?.hashCode() ?: 0)
    result = 31 * result + (drawFormat?.hashCode() ?: 0)
    result = 31 * result + (totalRounds?.hashCode() ?: 0)
    result = 31 * result + (prizeMoney?.hashCode() ?: 0)
    result = 31 * result + matches.contentHashCode()
    return result
  }
}

data class UsOpenMatch(val match_id: Any?,
                       val eventName: Any?,
                       val shortEventName: Any?,
                       val eventCode: Any?,
                       val courtName: Any?,
                       val shortCourtName: Any?,
                       val courtId: Any?,
                       val roundCode: Any?,
                       val roundName: Any?,
                       val roundNameShort: String,
                       val eventDay: Any?,
                       val duration: Any?,
                       val statsLevel: Any?,
                       val status: Any?,
                       val statusCode: Any?,
                       val winner: String?,
                       val epoch: Any?,
                       val team1: UsOpenTeam,
                       val team2: UsOpenTeam,
                       val flags: Any?,
                       val scores: Any?)

data class UsOpenTeam(val firstNameA: Any?,
                      val lastNameA: Any?,
                      val displayNameA: Any?,
                      val idA: String,
                      val nationA: Any?,
                      val firstNameB: Any?,
                      val lastNameB: Any?,
                      val displayNameB: Any?,
                      val idB: Any?,
                      val nationB: Any?,
                      val seed: Any?,
                      val entryStatus: Any?,
                      val totalSetsWon: Any?,
                      val won: Boolean?,
                      val serve: Any?)