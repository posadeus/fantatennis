package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto

import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.TournamentResultsDto

// FIXME: remove interface and all the methods and change it to data class
class TournamentResultsDtoImpl(private val tournamentId: Int,
                               private val teamId: Int,
                               private val ownerId: String,
                               private val playerId: String,
                               private val playerFullName: String,
                               private val playerTotalScore: Double) : TournamentResultsDto {

  override fun getTournamentId(): Int = tournamentId

  override fun getTeamId(): Int = teamId

  override fun getOwnerId(): String = ownerId

  override fun getPlayerId(): String = playerId

  override fun getPlayerFullName(): String = playerFullName

  override fun getPlayerTotalScore(): Double = playerTotalScore
}