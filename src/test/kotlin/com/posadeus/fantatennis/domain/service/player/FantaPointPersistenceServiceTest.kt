package com.posadeus.fantatennis.domain.service.player

import com.posadeus.fantatennis.controller.model.ranking.RankedPlayerDto
import com.posadeus.fantatennis.domain.infrastructure.PersistPlayersPointsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.FantaPointPersistence.FantaPointPersistenceSucceedWithErrors
import com.posadeus.fantatennis.domain.model.FantaPointPersistence.FantaPointPersistenceSuccess
import com.posadeus.fantatennis.domain.model.PlayerPersistence.PlayerPersistenceFailure
import com.posadeus.fantatennis.domain.model.PlayerPersistence.PlayerPersistenceSuccess
import com.posadeus.fantatennis.domain.model.TestAtpPlayer.anAtpPlayer
import com.posadeus.fantatennis.domain.model.TestDomainPlayer.aDomainPlayer
import com.posadeus.fantatennis.domain.service.ranking.RankingService
import io.mockk.*
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test

class FantaPointPersistenceServiceTest {

  private val persistPlayersPointsRepository: PersistPlayersPointsRepository = mockk()
  private val retrievePlayerService: RetrievePlayerService = mockk()
  private val rankingService: RankingService = mockk()
  private val persistPlayerService: PersistPlayerService = mockk()

  private val service = FantaPointPersistenceService(persistPlayersPointsRepository,
                                                     retrievePlayerService,
                                                     rankingService,
                                                     persistPlayerService)

  @Test
  fun `persist all players when all are found`() {

    val players = setOf(anAtpPlayer(id = AN_ATP_PLAYER_ID),
                        anAtpPlayer(id = ANOTHER_ATP_PLAYER_ID))

    val domainPlayers = setOf(aDomainPlayer(atpId = AN_ATP_PLAYER_ID), aDomainPlayer(atpId = ANOTHER_ATP_PLAYER_ID))

    val expected = FantaPointPersistenceSuccess

    every { retrievePlayerService.allPlayers() } returns domainPlayers
    every { persistPlayersPointsRepository.persistAll(players) } returns Unit

    assertThat(service.persist(players)).isEqualTo(expected)

    verify { rankingService wasNot called }
    verify { persistPlayerService wasNot called }
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

    val expected = FantaPointPersistenceSuccess

    every { retrievePlayerService.allPlayers() } returns domainPlayers
    every { rankingService.retrieveRankedPlayer(1000) } returns ranking
    every { persistPlayerService.persistAll(domainPlayersToPersist) } returns playerPersistence
    every { persistPlayersPointsRepository.persistAll(atpPlayersToPersist) } returns Unit

    assertThat(service.persist(players)).isEqualTo(expected)

    verify(exactly = 1) { persistPlayersPointsRepository.persistAll(atpPlayersToPersist) }
  }

  @Test
  fun `persist all possible players when partial missing players are found in ranking`() {

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

    val expected = FantaPointPersistenceSuccess

    every { retrievePlayerService.allPlayers() } returns domainPlayers
    every { rankingService.retrieveRankedPlayer(1000) } returns ranking
    every { persistPlayerService.persistAll(domainPlayersToPersist) } returns playerPersistence
    every { persistPlayersPointsRepository.persistAll(atpPlayersToPersist) } returns Unit

    assertThat(service.persist(players)).isEqualTo(expected)

    verify(exactly = 1) { persistPlayersPointsRepository.persistAll(atpPlayersToPersist) }
  }

  @Test
  fun `persist only registered players when rankingService returns EmptyRanking`() {

    val players = setOf(anAtpPlayer(id = AN_ATP_PLAYER_ID),
                        anAtpPlayer(id = "A_MISSING_ATP_PLAYER_ID"))

    val domainPlayers = setOf(aDomainPlayer(atpId = AN_ATP_PLAYER_ID))
    val ranking = EmptyRanking
    val atpPlayersToPersist = setOf(anAtpPlayer(id = AN_ATP_PLAYER_ID))

    val expected = FantaPointPersistenceSuccess

    every { retrievePlayerService.allPlayers() } returns domainPlayers
    every { rankingService.retrieveRankedPlayer(1000) } returns ranking
    every { persistPlayersPointsRepository.persistAll(atpPlayersToPersist) } returns Unit

    assertThat(service.persist(players)).isEqualTo(expected)

    verify { persistPlayerService wasNot called }
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

    val expected = FantaPointPersistenceSuccess

    every { retrievePlayerService.allPlayers() } returns domainPlayers
    every { rankingService.retrieveRankedPlayer(1000) } returns ranking
    every { persistPlayersPointsRepository.persistAll(atpPlayersToPersist) } returns Unit

    assertThat(service.persist(players)).isEqualTo(expected)

    verify { persistPlayerService wasNot called }
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

    every { retrievePlayerService.allPlayers() } returns domainPlayers
    every { rankingService.retrieveRankedPlayer(1000) } returns ranking
    every { persistPlayerService.persistAll(domainPlayersToPersist) } returns playerPersistence
    every { persistPlayersPointsRepository.persistAll(atpPlayersToPersist) } returns Unit

    assertThat(service.persist(players)).isEqualTo(expected)

    verify(exactly = 1) { persistPlayersPointsRepository.persistAll(atpPlayersToPersist) }
  }







  @Test
  fun `persist scores`() {

    val players = setOf(AtpPlayer(id = "PlayerId1",
                                  tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 28.0))),
                        AtpPlayer(id = "PlayerId2",
                                  tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 16.0))))

    every { persistPlayersPointsRepository.persistAll(players) } just runs

    service.persistScores(players)

    verify(exactly = 1) { persistPlayersPointsRepository.persistAll(players) }
  }

  @Test
  fun `repository not called if players is empty`() {

    val players = emptySet<AtpPlayer>()

    service.persistScores(players)

    verify { persistPlayersPointsRepository wasNot called }
  }

  companion object {

    private const val AN_ATP_PLAYER_ID = "AN_ATP_PLAYER_ID"
    private const val ANOTHER_ATP_PLAYER_ID = "ANOTHER_ATP_PLAYER_ID"
    private const val A_FULL_NAME = "A_FULL_NAME"
    private const val A_TOURNAMENT_ID = 123
    private const val A_YEAR = 2222
    private const val SOME_POINTS = 100
  }
}