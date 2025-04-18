package com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao

import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.FantaTournamentsEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FantaTournamentsDao : JpaRepository<FantaTournamentsEntity, Int>

interface TournamentResultsDto {

  fun getTournamentId(): Int
  fun getTeamId(): Int
  fun getOwnerId(): String
  fun getPlayerId(): String
  fun getPlayerFullName(): String
  fun getPlayerTotalScore(): Double
}