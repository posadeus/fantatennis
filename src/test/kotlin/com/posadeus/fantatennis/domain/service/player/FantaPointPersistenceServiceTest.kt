package com.posadeus.fantatennis.domain.service.player

import com.posadeus.fantatennis.controller.model.ranking.RankedPlayerDto
import com.posadeus.fantatennis.domain.infrastructure.*
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.FantaPointPersistence.FantaPointPersistenceSucceedWithErrors
import com.posadeus.fantatennis.domain.model.FantaPointPersistence.FantaPointPersistenceSucceeded
import com.posadeus.fantatennis.domain.model.PlayerPersistence.PlayerPersistenceFailure
import com.posadeus.fantatennis.domain.model.PlayerPersistence.PlayerPersistenceSuccess
import com.posadeus.fantatennis.domain.model.TestAtpPlayer.anAtpPlayer
import com.posadeus.fantatennis.domain.model.TestDomainPlayer.aDomainPlayer
import io.mockk.*
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test

class FantaPointPersistenceServiceTest {

  private val persistPlayersPointsRepository: PersistPlayersPointsRepository = mockk()
  private val retrievePlayersRepository: RetrievePlayersRepository = mockk()
  private val rankingRepository: RankingRepository = mockk()
  private val persistPlayersRepository: PersistPlayersRepository = mockk()

  private val service = FantaPointPersistenceService(persistPlayersPointsRepository,
                                                     retrievePlayersRepository,
                                                     rankingRepository,
                                                     persistPlayersRepository)

  @Test
  fun `persist all players when all are found`() {

    val players = setOf(anAtpPlayer(id = AN_ATP_PLAYER_ID),
                        anAtpPlayer(id = ANOTHER_ATP_PLAYER_ID))

    val domainPlayers = setOf(aDomainPlayer(atpId = AN_ATP_PLAYER_ID), aDomainPlayer(atpId = ANOTHER_ATP_PLAYER_ID))

    val expected = FantaPointPersistenceSucceeded

    every { retrievePlayersRepository.retrieve() } returns domainPlayers
    every { persistPlayersPointsRepository.persistAll(players) } returns FantaPointPersistenceSucceeded

    assertThat(service.persist(players)).isEqualTo(expected)

    verify { rankingRepository wasNot called }
    verify { persistPlayersRepository wasNot called }
    verify(exactly = 1) { persistPlayersPointsRepository.persistAll(players) }
  }

  @Test
  fun `persist all players when missing players are found in ranking`() {

    val players = setOf(anAtpPlayer(id = AN_ATP_PLAYER_ID),
                        anAtpPlayer(id = "A_MISSING_ATP_PLAYER_ID"),
                        anAtpPlayer(id = "ANOTHER_MISSING_ATP_PLAYER_ID"))

    val domainPlayers = setOf(aDomainPlayer(atpId = AN_ATP_PLAYER_ID))
    val rankedPlayers = listOf(RankedPlayerDto(id = AN_ATP_PLAYER_ID, rank = 1, points = SOME_POINTS),
                               RankedPlayerDto(id = "A_MISSING_ATP_PLAYER_ID", rank = 2, points = SOME_POINTS, fullName = A_FULL_NAME),
                               RankedPlayerDto(id = "ANOTHER_MISSING_ATP_PLAYER_ID", rank = 3, points = SOME_POINTS, fullName = A_FULL_NAME))
    val ranking = RankedPlayers(rankedPlayers)
    val domainPlayerToPersist1 = DomainPlayer(id = "A_MISSING_ATP_PLAYER_ID", atpId = "A_MISSING_ATP_PLAYER_ID", fullName = A_FULL_NAME)
    val domainPlayerToPersist2 = DomainPlayer(id = "ANOTHER_MISSING_ATP_PLAYER_ID", atpId = "ANOTHER_MISSING_ATP_PLAYER_ID", fullName = A_FULL_NAME)
    val domainPlayersToPersist = setOf(domainPlayerToPersist1, domainPlayerToPersist2)
    val playerPersistence = PlayerPersistenceSuccess
    val atpPlayersToPersist = setOf(anAtpPlayer(id = AN_ATP_PLAYER_ID),
                                    anAtpPlayer(id = "A_MISSING_ATP_PLAYER_ID"),
                                    anAtpPlayer(id = "ANOTHER_MISSING_ATP_PLAYER_ID"))

    val expected = FantaPointPersistenceSucceeded

    every { retrievePlayersRepository.retrieve() } returns domainPlayers
    every { rankingRepository.retrieveRanking(1000) } returns ranking
    every { persistPlayersRepository.persistAll(domainPlayersToPersist) } returns playerPersistence
    every { persistPlayersPointsRepository.persistAll(atpPlayersToPersist) } returns FantaPointPersistenceSucceeded

    assertThat(service.persist(players)).isEqualTo(expected)

    verify(exactly = 1) { persistPlayersPointsRepository.persistAll(atpPlayersToPersist) }
  }

  @Test
  fun `persist all possible players when partial missing players have been found in ranking`() {

    val players = setOf(anAtpPlayer(id = AN_ATP_PLAYER_ID),
                        anAtpPlayer(id = "A_MISSING_ATP_PLAYER_ID"),
                        anAtpPlayer(id = "ANOTHER_MISSING_ATP_PLAYER_ID"))

    val domainPlayers = setOf(aDomainPlayer(atpId = AN_ATP_PLAYER_ID))
    val rankedPlayers = listOf(RankedPlayerDto(id = AN_ATP_PLAYER_ID, rank = 1, points = SOME_POINTS),
                               RankedPlayerDto(id = "A_MISSING_ATP_PLAYER_ID", rank = 2, points = SOME_POINTS, fullName = A_FULL_NAME))
    val ranking = RankedPlayers(rankedPlayers)
    val domainPlayerToPersist1 = DomainPlayer(id = "A_MISSING_ATP_PLAYER_ID", atpId = "A_MISSING_ATP_PLAYER_ID", fullName = A_FULL_NAME)
    val domainPlayersToPersist = setOf(domainPlayerToPersist1)
    val playerPersistence = PlayerPersistenceSuccess
    val atpPlayersToPersist = setOf(anAtpPlayer(id = AN_ATP_PLAYER_ID), anAtpPlayer(id = "A_MISSING_ATP_PLAYER_ID"))

    val expected = FantaPointPersistenceSucceeded

    every { retrievePlayersRepository.retrieve() } returns domainPlayers
    every { rankingRepository.retrieveRanking(1000) } returns ranking
    every { persistPlayersRepository.persistAll(domainPlayersToPersist) } returns playerPersistence
    every { persistPlayersPointsRepository.persistAll(atpPlayersToPersist) } returns FantaPointPersistenceSucceeded

    assertThat(service.persist(players)).isEqualTo(expected)

    verify(exactly = 1) { persistPlayersPointsRepository.persistAll(atpPlayersToPersist) }
  }

  @Test
  fun `persist only registered players when rankingRepository retrieveRanking()`() {

    val players = setOf(anAtpPlayer(id = AN_ATP_PLAYER_ID),
                        anAtpPlayer(id = "A_MISSING_ATP_PLAYER_ID"))

    val domainPlayers = setOf(aDomainPlayer(atpId = AN_ATP_PLAYER_ID))
    val ranking = EmptyRanking
    val atpPlayersToPersist = setOf(anAtpPlayer(id = AN_ATP_PLAYER_ID))

    val expected = FantaPointPersistenceSucceeded

    every { retrievePlayersRepository.retrieve() } returns domainPlayers
    every { rankingRepository.retrieveRanking(1000) } returns ranking
    every { persistPlayersPointsRepository.persistAll(atpPlayersToPersist) } returns FantaPointPersistenceSucceeded

    assertThat(service.persist(players)).isEqualTo(expected)

    verify { persistPlayersRepository wasNot called }
    verify(exactly = 1) { persistPlayersPointsRepository.persistAll(atpPlayersToPersist) }
  }

  @Test
  fun `persist only registered players when missing players are not found in ranking`() {

    val players = setOf(anAtpPlayer(id = AN_ATP_PLAYER_ID),
                        anAtpPlayer(id = "A_MISSING_ATP_PLAYER_ID"),
                        anAtpPlayer(id = "ANOTHER_MISSING_ATP_PLAYER_ID"))

    val domainPlayers = setOf(aDomainPlayer(atpId = AN_ATP_PLAYER_ID))
    val rankedPlayers = listOf(RankedPlayerDto(id = AN_ATP_PLAYER_ID, rank = 1, points = SOME_POINTS))
    val ranking = RankedPlayers(rankedPlayers)
    val atpPlayersToPersist = setOf(anAtpPlayer(id = AN_ATP_PLAYER_ID))

    val expected = FantaPointPersistenceSucceeded

    every { retrievePlayersRepository.retrieve() } returns domainPlayers
    every { rankingRepository.retrieveRanking(1000) } returns ranking
    every { persistPlayersPointsRepository.persistAll(atpPlayersToPersist) } returns FantaPointPersistenceSucceeded

    assertThat(service.persist(players)).isEqualTo(expected)

    verify { persistPlayersRepository wasNot called }
    verify(exactly = 1) { persistPlayersPointsRepository.persistAll(atpPlayersToPersist) }
  }

  @Test
  fun `persist only already registered players when new player persistence fails`() {

    val players = setOf(anAtpPlayer(id = AN_ATP_PLAYER_ID),
                        anAtpPlayer(id = "A_MISSING_ATP_PLAYER_ID"),
                        anAtpPlayer(id = "ANOTHER_MISSING_ATP_PLAYER_ID"))

    val domainPlayers = setOf(aDomainPlayer(atpId = AN_ATP_PLAYER_ID))
    val rankedPlayers = listOf(RankedPlayerDto(id = AN_ATP_PLAYER_ID, rank = 1, points = SOME_POINTS),
                               RankedPlayerDto(id = "A_MISSING_ATP_PLAYER_ID", rank = 2, points = SOME_POINTS, fullName = A_FULL_NAME),
                               RankedPlayerDto(id = "ANOTHER_MISSING_ATP_PLAYER_ID", rank = 3, points = SOME_POINTS, fullName = A_FULL_NAME))
    val ranking = RankedPlayers(rankedPlayers)
    val domainPlayerToPersist1 = DomainPlayer(id = "A_MISSING_ATP_PLAYER_ID", atpId = "A_MISSING_ATP_PLAYER_ID", fullName = A_FULL_NAME)
    val domainPlayerToPersist2 = DomainPlayer(id = "ANOTHER_MISSING_ATP_PLAYER_ID", atpId = "ANOTHER_MISSING_ATP_PLAYER_ID", fullName = A_FULL_NAME)
    val domainPlayersToPersist = setOf(domainPlayerToPersist1, domainPlayerToPersist2)
    val playerPersistence = PlayerPersistenceFailure(message = "You have an error", error = "I'm an error")
    val atpPlayersToPersist = setOf(anAtpPlayer(id = AN_ATP_PLAYER_ID))

    val expected = FantaPointPersistenceSucceedWithErrors(message = "You have an error")

    every { retrievePlayersRepository.retrieve() } returns domainPlayers
    every { rankingRepository.retrieveRanking(1000) } returns ranking
    every { persistPlayersRepository.persistAll(domainPlayersToPersist) } returns playerPersistence
    every { persistPlayersPointsRepository.persistAll(atpPlayersToPersist) } returns FantaPointPersistenceSucceeded

    assertThat(service.persist(players)).isEqualTo(expected)

    verify(exactly = 1) { persistPlayersPointsRepository.persistAll(atpPlayersToPersist) }
  }

  companion object {

    private const val AN_ATP_PLAYER_ID = "AN_ATP_PLAYER_ID"
    private const val ANOTHER_ATP_PLAYER_ID = "ANOTHER_ATP_PLAYER_ID"
    private const val A_FULL_NAME = "A_FULL_NAME"
    private const val SOME_POINTS = 100
  }
}