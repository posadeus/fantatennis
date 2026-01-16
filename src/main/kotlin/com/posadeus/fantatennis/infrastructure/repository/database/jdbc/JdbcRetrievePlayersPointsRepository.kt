package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.infrastructure.RetrievePlayersPointsRepository
import com.posadeus.fantatennis.domain.model.PlayersPoints
import com.posadeus.fantatennis.domain.model.PlayersPoints.FoundPlayersPoints
import com.posadeus.fantatennis.domain.model.PlayersPoints.FoundPlayersPoints.PlayerPoints
import com.posadeus.fantatennis.domain.model.PlayersPoints.InternalErrorPlayersPoints
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerPointsDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerPointsDto
import org.slf4j.LoggerFactory

class JdbcRetrievePlayersPointsRepository(private val playerPointsDao: PlayerPointsDao,
                                          private val playerDao: PlayerDao) : RetrievePlayersPointsRepository {

  override fun retrieveBy(tournamentId: Int): PlayersPoints =
      try {

        playerPointsDao.retrieveByTournamentId(tournamentId)
            .takeIf(List<JdbcPlayerPointsDto>::isNotEmpty)
            ?.let { playersPoints ->

              val allPlayers = playerDao.retrieveAll()

              playersPoints
                  .mapNotNull { playerPoints ->
                    allPlayers
                        .firstOrNull { it.playerId == playerPoints.playerId }
                        ?.let { toPlayerPoints(playerPoints, it) }
                  }
                  .let(::FoundPlayersPoints)
            }
        ?: FoundPlayersPoints(playersPoints = emptyList())
      }
      catch (e: RuntimeException) {

        LOGGER.error(e.message)
        InternalErrorPlayersPoints
      }

  private fun toPlayerPoints(playerPoints: JdbcPlayerPointsDto, jdbcPlayerDto: JdbcPlayerDto): PlayerPoints =
      PlayerPoints(playerId = playerPoints.playerId,
                   playerName = jdbcPlayerDto.fullName,
                   totalPoints = playerPoints.fantaPoints)

  companion object {

    private val LOGGER = LoggerFactory.getLogger(JdbcRetrievePlayersPointsRepository::class.java)
  }
}