package com.posadeus.fantatennis.domain.service

import com.posadeus.fantatennis.controller.model.ranking.RankedPlayerDto
import com.posadeus.fantatennis.domain.exception.NoPointsForTournamentException
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.PlayerPersistence.PlayerPersistenceSucceeded
import com.posadeus.fantatennis.domain.service.player.*
import com.posadeus.fantatennis.domain.service.ranking.RankingService
import io.mockk.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class FantaPointServiceTest {

  private val fantaPointCalculatorService: FantaPointCalculatorService = mockk()
  private val retrievePlayerService: RetrievePlayerService = mockk()
  private val persistPlayerService: PersistPlayerService = mockk()
  private val rankingService: RankingService = mockk()
  private val fantaPointPersistenceService: FantaPointPersistenceService = mockk()

  private val service = FantaPointService(fantaPointCalculatorService,
                                          fantaPointPersistenceService,
                                          retrievePlayerService,
                                          persistPlayerService,
                                          rankingService)

  @Test
  fun `all tournament's players found`() {

    val atpPlayers = setOf(AtpPlayer(id = "AN_ATP_PLAYER_ID_1",
                                     tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 28.0))),
                           AtpPlayer(id = "AN_ATP_PLAYER_ID_2",
                                     tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 16.0))))

    val domainPlayers = setOf(DomainPlayer(id = "A_DOMAIN_PLAYER_ID_1",
                                           atpId = "AN_ATP_PLAYER_ID_1",
                                           fullName = A_FULL_NAME),
                              DomainPlayer(id = "A_DOMAIN_PLAYER_ID_2",
                                           atpId = "AN_ATP_PLAYER_ID_2",
                                           fullName = A_SECOND_FULL_NAME),
                              DomainPlayer(id = A_DOMAIN_PLAYER_ID_3,
                                           atpId = "AN_ATP_PLAYER_ID_3",
                                           fullName = A_THIRD_FULL_NAME))

    every { fantaPointCalculatorService.calculateFantaPointsFor(A_TOURNAMENT_ID, A_YEAR) } returns atpPlayers
    every { retrievePlayerService.allPlayers() } returns domainPlayers
    every { fantaPointPersistenceService.persistScores(atpPlayers) } just runs

    service.playerFantaPointsFor(A_TOURNAMENT_ID, A_YEAR)

    verify(exactly = 1) { fantaPointCalculatorService.calculateFantaPointsFor(A_TOURNAMENT_ID, A_YEAR) }
    verify(exactly = 1) { retrievePlayerService.allPlayers() }
    verify(exactly = 1) { fantaPointPersistenceService.persistScores(atpPlayers) }
    verify { rankingService wasNot called }
    verify { persistPlayerService wasNot called }
  }

  @Test
  fun `not all tournament's players found`() {

    val atpPlayers = setOf(AtpPlayer(id = AN_ATP_PLAYER_ID,
                                     tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 28.0))),
                           AtpPlayer(id = "ANOTHER_ATP_PLAYER_ID",
                                     tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 16.0))))
    val domainPlayers = setOf(DomainPlayer(id = A_DOMAIN_PLAYER_ID,
                                           atpId = AN_ATP_PLAYER_ID,
                                           fullName = A_FULL_NAME))
    val ranking = RankedPlayers(listOf(RankedPlayerDto(id = AN_ATP_PLAYER_ID,
                                                       fullName = A_FULL_NAME,
                                                       rank = A_RANKING,
                                                       points = A_POINTS),
                                       RankedPlayerDto(id = "ANOTHER_ATP_PLAYER_ID",
                                                       fullName = "ANOTHER_FULL_NAME",
                                                       rank = ANOTHER_RANKING,
                                                       points = ANOTHER_POINTS)))
    val missingDomainPlayers = setOf(DomainPlayer(id = "ANOTHER_ATP_PLAYER_ID",
                                                  atpId = "ANOTHER_ATP_PLAYER_ID",
                                                  fullName = "ANOTHER_FULL_NAME"))

    every { fantaPointCalculatorService.calculateFantaPointsFor(A_TOURNAMENT_ID, A_YEAR) } returns atpPlayers
    every { retrievePlayerService.allPlayers() } returns domainPlayers
    every { rankingService.retrieveRankedPlayer(1000) } returns ranking
    every { persistPlayerService.persistAll(missingDomainPlayers) } returns PlayerPersistenceSucceeded
    every { fantaPointPersistenceService.persistScores(atpPlayers) } just runs

    service.playerFantaPointsFor(A_TOURNAMENT_ID, A_YEAR)

    verify(exactly = 1) { fantaPointCalculatorService.calculateFantaPointsFor(A_TOURNAMENT_ID, A_YEAR) }
    verify(exactly = 1) { retrievePlayerService.allPlayers() }
    verify(exactly = 1) { rankingService.retrieveRankedPlayer(1000) }
    verify(exactly = 1) { persistPlayerService.persistAll(missingDomainPlayers) }
    verify(exactly = 1) { fantaPointPersistenceService.persistScores(atpPlayers) }
  }

  @Test
  fun `not all tournament's players found neither in the ranking`() {

    val atpPlayers = setOf(AtpPlayer(id = AN_ATP_PLAYER_ID,
                                     tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 28.0))),
                           AtpPlayer(id = "ANOTHER_ATP_PLAYER_ID",
                                     tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 16.0))),
                           AtpPlayer(id = "A_THIRD_ATP_PLAYER_ID",
                                     tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 12.0))))
    val domainPlayers = setOf(DomainPlayer(id = A_DOMAIN_PLAYER_ID,
                                           atpId = AN_ATP_PLAYER_ID,
                                           fullName = A_FULL_NAME))
    val ranking = RankedPlayers(listOf(RankedPlayerDto(id = AN_ATP_PLAYER_ID,
                                                       fullName = A_FULL_NAME,
                                                       rank = A_RANKING,
                                                       points = A_POINTS),
                                       RankedPlayerDto(id = "ANOTHER_ATP_PLAYER_ID",
                                                       fullName = "ANOTHER_FULL_NAME",
                                                       rank = ANOTHER_RANKING,
                                                       points = ANOTHER_POINTS)))
    val missingDomainPlayers = setOf(DomainPlayer(id = "ANOTHER_ATP_PLAYER_ID",
                                                  atpId = "ANOTHER_ATP_PLAYER_ID",
                                                  fullName = "ANOTHER_FULL_NAME"))
    val playerScoresToUpdate = setOf(AtpPlayer(id = AN_ATP_PLAYER_ID,
                                               tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 28.0))),
                                     AtpPlayer(id = "ANOTHER_ATP_PLAYER_ID",
                                               tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 16.0))))

    every { fantaPointCalculatorService.calculateFantaPointsFor(A_TOURNAMENT_ID, A_YEAR) } returns atpPlayers
    every { retrievePlayerService.allPlayers() } returns domainPlayers
    every { rankingService.retrieveRankedPlayer(1000) } returns ranking
    every { persistPlayerService.persistAll(missingDomainPlayers) } returns PlayerPersistenceSucceeded
    every { fantaPointPersistenceService.persistScores(playerScoresToUpdate) } just runs

    service.playerFantaPointsFor(A_TOURNAMENT_ID, A_YEAR)

    verify(exactly = 1) { fantaPointCalculatorService.calculateFantaPointsFor(A_TOURNAMENT_ID, A_YEAR) }
    verify(exactly = 1) { retrievePlayerService.allPlayers() }
    verify(exactly = 1) { rankingService.retrieveRankedPlayer(1000) }
    verify(exactly = 1) { persistPlayerService.persistAll(missingDomainPlayers) }
    verify(exactly = 1) { fantaPointPersistenceService.persistScores(playerScoresToUpdate) }
  }

  @Test
  fun `tournamentPlayers is empty`() {

    every { fantaPointCalculatorService.calculateFantaPointsFor(A_TOURNAMENT_ID, A_YEAR) } returns emptySet()

    assertThrows<NoPointsForTournamentException> { service.playerFantaPointsFor(A_TOURNAMENT_ID, A_YEAR) }

    verify(exactly = 1) { fantaPointCalculatorService.calculateFantaPointsFor(A_TOURNAMENT_ID, A_YEAR) }
    verify { retrievePlayerService wasNot called }
    verify { persistPlayerService wasNot called }
    verify { rankingService wasNot called }
    verify { fantaPointPersistenceService wasNot called }
  }

  @Test
  fun `not all tournament's players found and ranking is empty`() {

    val atpPlayers = setOf(AtpPlayer(id = AN_ATP_PLAYER_ID,
                                     tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 28.0))),
                           AtpPlayer(id = "ANOTHER_ATP_PLAYER_ID",
                                     tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 16.0))),
                           AtpPlayer(id = "A_THIRD_ATP_PLAYER_ID",
                                     tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 12.0))))
    val domainPlayers = setOf(DomainPlayer(id = A_DOMAIN_PLAYER_ID,
                                           atpId = AN_ATP_PLAYER_ID,
                                           fullName = A_FULL_NAME))

    every { fantaPointCalculatorService.calculateFantaPointsFor(A_TOURNAMENT_ID, A_YEAR) } returns atpPlayers
    every { retrievePlayerService.allPlayers() } returns domainPlayers
    every { rankingService.retrieveRankedPlayer(1000) } returns EmptyRanking

    assertThrows<ClassCastException> { service.playerFantaPointsFor(A_TOURNAMENT_ID, A_YEAR) }

    verify(exactly = 1) { fantaPointCalculatorService.calculateFantaPointsFor(A_TOURNAMENT_ID, A_YEAR) }
    verify(exactly = 1) { retrievePlayerService.allPlayers() }
    verify(exactly = 1) { rankingService.retrieveRankedPlayer(1000) }
    verify { fantaPointPersistenceService wasNot called }
    verify { persistPlayerService wasNot called }
  }

  companion object {

    private const val A_TOURNAMENT_ID = 1
    private const val A_YEAR = 2000
    private const val A_RANKING = 1
    private const val ANOTHER_RANKING = 1
    private const val A_POINTS = 1
    private const val ANOTHER_POINTS = 1
    private const val A_FULL_NAME = "A_FULL_NAME"
    private const val A_SECOND_FULL_NAME = "A_SECOND_FULL_NAME"
    private const val A_THIRD_FULL_NAME = "A_THIRD_FULL_NAME"
    private const val A_DOMAIN_PLAYER_ID = "A_DOMAIN_PLAYER_ID"
    private const val A_DOMAIN_PLAYER_ID_3 = "A_DOMAIN_PLAYER_ID_3"
    private const val AN_ATP_PLAYER_ID = "AN_ATP_PLAYER_ID"
  }
}