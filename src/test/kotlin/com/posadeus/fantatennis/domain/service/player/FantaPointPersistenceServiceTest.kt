package com.posadeus.fantatennis.domain.service.player

import com.posadeus.fantatennis.controller.model.ranking.RankedPlayerDto
import com.posadeus.fantatennis.domain.infrastructure.PersistPlayersPointsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.FailureReason.EMPTY_RANKING
import com.posadeus.fantatennis.domain.model.FailureReason.MISSING_PLAYERS
import com.posadeus.fantatennis.domain.model.FantaPointPersistence.FantaPointPersistenceFailure
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

  private val service = FantaPointPersistenceService(persistPlayersPointsRepository,
                                                     retrievePlayerService,
                                                     rankingService)

  @Test
  fun `while missing players rankingService returns EmptyRanking`() {

    val players = setOf(anAtpPlayer(id = AN_ATP_PLAYER_ID),
                        anAtpPlayer(id = "A_MISSING_ATP_PLAYER_ID"))

    val domainPlayers = setOf(aDomainPlayer(atpId = AN_ATP_PLAYER_ID))
    val ranking = EmptyRanking

    val expected = FantaPointPersistenceFailure(reason = EMPTY_RANKING)

    every { retrievePlayerService.allPlayers() } returns domainPlayers
    every { rankingService.retrieveRankedPlayer(1000) } returns ranking

    assertThat(service.persist(players)).isEqualTo(expected)
  }

  @Test
  fun `missing players that are missing also in ranking cannot be persisted`() {

    val players = setOf(anAtpPlayer(id = AN_ATP_PLAYER_ID),
                        anAtpPlayer(id = "A_MISSING_ATP_PLAYER_ID"))

    val domainPlayers = setOf(aDomainPlayer(atpId = AN_ATP_PLAYER_ID))
    val rankedPlayers = listOf(RankedPlayerDto(id = AN_ATP_PLAYER_ID, rank = 1, points = SOME_POINTS),
                               RankedPlayerDto(id = ANOTHER_ATP_PLAYER_ID, rank = 2, points = SOME_POINTS))
    val ranking = RankedPlayers(rankedPlayers)

    val expected = FantaPointPersistenceFailure(reason = MISSING_PLAYERS)

    every { retrievePlayerService.allPlayers() } returns domainPlayers
    every { rankingService.retrieveRankedPlayer(1000) } returns ranking

    assertThat(service.persist(players)).isEqualTo(expected)
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
    private const val A_TOURNAMENT_ID = 123
    private const val A_YEAR = 2222
    private const val SOME_POINTS = 100
  }
}