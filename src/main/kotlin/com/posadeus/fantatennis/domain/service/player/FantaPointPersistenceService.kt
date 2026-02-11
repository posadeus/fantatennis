package com.posadeus.fantatennis.domain.service.player

import com.posadeus.fantatennis.controller.model.ranking.RankedPlayerDto
import com.posadeus.fantatennis.domain.infrastructure.*
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.FantaPointPersistence.FantaPointPersistenceSucceedWithErrors
import com.posadeus.fantatennis.domain.model.FantaPointPersistence.FantaPointPersistenceSucceeded
import com.posadeus.fantatennis.domain.model.PlayerPersistence.PlayerPersistenceFailure
import com.posadeus.fantatennis.domain.model.PlayerPersistence.PlayerPersistenceSuccess
import org.slf4j.LoggerFactory

class FantaPointPersistenceService(private val persistPlayersPointsRepository: PersistPlayersPointsRepository,
                                   private val retrievePlayersRepository: RetrievePlayersRepository,
                                   private val rankingRepository: RankingRepository,
                                   private val persistPlayersRepository: PersistPlayersRepository) {

  fun persist(players: Set<AtpPlayer>): FantaPointPersistence {

    val allPlayersByAtpId = retrievePlayersRepository.retrieve()
        .groupBy { it.atpId }

    val notRegisteredPlayersIds = players
        .filterNot { it.id in allPlayersByAtpId.keys }
        .map { it.id }
        .toSet()

    return if (notRegisteredPlayersIds.isEmpty())
      persistPlayersPoints(players)
    else {

      LOGGER.warn("Missing players: $notRegisteredPlayersIds")

      val missingPlayers = retrieveMissingPlayers(notRegisteredPlayersIds)

      if (missingPlayers.toRegister.isNotEmpty())
        when (val result = persistPlayersRepository.persistAll(missingPlayers.toRegister)) {

          is PlayerPersistenceFailure -> persistFilteredPlayers(players, result.message) { it.id in missingPlayers.toSearchIds }
          is PlayerPersistenceSuccess -> persistFilteredPlayers(players) { it.id in missingPlayers.notFound }
        }
      else
        persistFilteredPlayers(players) { it.id in missingPlayers.toSearchIds }
    }
  }

  private fun persistFilteredPlayers(players: Set<AtpPlayer>,
                                     errorMessage: String? = null,
                                     arePlayersToPersist: (AtpPlayer) -> Boolean): FantaPointPersistence =
      players
          .filterNot(arePlayersToPersist)
          .toSet()
          .let { persistPlayersPoints(it, errorMessage) }

  private fun persistPlayersPoints(playersScoresToPersist: Set<AtpPlayer>, errorMessage: String? = null): FantaPointPersistence =
      if (errorMessage == null) {

        persistPlayersPointsRepository.persistAll(playersScoresToPersist)
      }
      else {

        when (val result = persistPlayersPointsRepository.persistAll(playersScoresToPersist)) {

          FantaPointPersistenceSucceeded -> FantaPointPersistenceSucceedWithErrors(errorMessage)
          else -> result
        }
      }

  private fun retrieveMissingPlayers(notRegisteredPlayersIds: Set<AtpPlayerId>): MissingPlayers =
      when (val rankedPlayers = rankingRepository.retrieveRanking(1000)) {

        is EmptyRanking -> MissingPlayers(toSearchIds = notRegisteredPlayersIds,
                                          toRegister = emptySet(),
                                          notFound = notRegisteredPlayersIds)
        is RankedPlayers -> {

          val rankedPlayersByAtpId = rankedPlayers.players
              .associateBy { it.id }

          val playersToRegister = notRegisteredPlayersIds
              .filter { rankedPlayersByAtpId[it] != null }
              .map { toDomain(rankedPlayersByAtpId[it]!!) }
              .toSet()

          val playersToRegisterIds = playersToRegister
              .map { it.id }

          val notFoundPlayersIds = notRegisteredPlayersIds
              .filterNot { it in playersToRegisterIds }
              .toSet()

          if (notFoundPlayersIds.isNotEmpty())
                MissingPlayers(toSearchIds = notRegisteredPlayersIds,
                               toRegister = playersToRegister,
                               notFound = notFoundPlayersIds)
                    .also { LOGGER.warn("Players $notFoundPlayersIds not present in top 1000") }
          else
            MissingPlayers(toSearchIds = notRegisteredPlayersIds, toRegister = playersToRegister)
        }
      }

  private fun toDomain(rankedPlayer: RankedPlayerDto): DomainPlayer =
      DomainPlayer(id = rankedPlayer.id,
                   atpId = rankedPlayer.id,
                   fullName = rankedPlayer.fullName)

  private data class MissingPlayers(val toSearchIds: Set<AtpPlayerId>,
                                    val toRegister: Set<DomainPlayer>,
                                    val notFound: Set<AtpPlayerId> = emptySet())

  companion object {

    private val LOGGER = LoggerFactory.getLogger(FantaPointPersistenceService::class.java)
  }
}
