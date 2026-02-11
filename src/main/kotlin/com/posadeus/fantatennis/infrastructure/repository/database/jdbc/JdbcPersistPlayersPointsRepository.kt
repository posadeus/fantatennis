package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.infrastructure.PersistPlayersPointsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.FailureReason.PERSISTENCE_ERROR
import com.posadeus.fantatennis.domain.model.FantaPointPersistence.FantaPointPersistenceFailure
import com.posadeus.fantatennis.domain.model.FantaPointPersistence.FantaPointPersistenceSucceeded
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerPointsDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerPointsDto
import org.slf4j.LoggerFactory

class JdbcPersistPlayersPointsRepository(private val playerPointsDao: PlayerPointsDao) : PersistPlayersPointsRepository {

  override fun persistAll(players: Set<AtpPlayer>): FantaPointPersistence =
      try {

        players
            .flatMap(::toJdbcPlayersPoints)
            .let(::persistAll)
            .let { FantaPointPersistenceSucceeded }
      }
      catch (e: RuntimeException) {

        LOGGER.error(e.message)
        FantaPointPersistenceFailure(PERSISTENCE_ERROR)
      }

  private fun persistAll(playersPoints: List<JdbcPlayerPointsDto>) =
      playerPointsDao.persistAll(playersPoints)

  private fun toJdbcPlayersPoints(player: AtpPlayer): List<JdbcPlayerPointsDto> =
      player.tournamentPoints
          .entries
          .flatMap { tournamentsPointsByYear ->
            tournamentsPointsByYear.value
                .entries
                .map { toJdbcPlayerPointsDto(tournamentsPointsByYear.key, it, player.id) }
          }

  private fun toJdbcPlayerPointsDto(year: Year,
                                    pointsByTournament: Map.Entry<TournamentId, Double>,
                                    playerId: AtpPlayerId) =
      JdbcPlayerPointsDto(tournamentYear = year,
                          tournamentId = pointsByTournament.key,
                          playerId = playerId,
                          fantaPoints = pointsByTournament.value)

  companion object {

    private val LOGGER = LoggerFactory.getLogger(JdbcPersistPlayersPointsRepository::class.java)
  }
}
