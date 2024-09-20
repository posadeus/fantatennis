package com.posadeus.fantatennis.domain.service.player

import com.posadeus.fantatennis.domain.infrastructure.PlayerPointsRepository
import com.posadeus.fantatennis.domain.infrastructure.PlayersRepository
import com.posadeus.fantatennis.domain.model.AtpPlayer
import com.posadeus.fantatennis.domain.model.DomainPlayer
import io.mockk.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class FantaPointPersistenceServiceTest {

  private val playerPointsRepository: PlayerPointsRepository = mockk()
  private val playersRepository: PlayersRepository = mockk()

  private val service = FantaPointPersistenceService(playerPointsRepository,
                                                     playersRepository)

  @Nested
  inner class ScorePersistence {

    @Test
    fun `persist scores`() {

      val players = setOf(AtpPlayer(id = "PlayerId1",
                                    tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 28.0))),
                          AtpPlayer(id = "PlayerId2",
                                    tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 16.0))))

      every { playerPointsRepository.save(players) } just runs

      service.persistScores(players)

      verify(exactly = 1) { playerPointsRepository.save(players) }
      verify { playersRepository wasNot called }
    }

    @Test
    fun `repository not called if players is empty`() {

      val players = emptySet<AtpPlayer>()

      service.persistScores(players)

      verify { playerPointsRepository wasNot called }
      verify { playersRepository wasNot called }
    }
  }

  @Nested
  inner class PlayersAndScorePersistence {

    @Test
    fun `persist players and scores`() {

      val domainPlayers = setOf(DomainPlayer(id = AN_ID,
                                             atpId = "PlayerId1",
                                             fullName = A_FULL_NAME))

      val playerScores = setOf(AtpPlayer(id = "PlayerId1",
                                         tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 28.0))),
                               AtpPlayer(id = AN_ALREADY_EXISTING_PLAYER_ATP_ID,
                                         tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 16.0))))

      every { playersRepository.saveAll(domainPlayers) } just runs
      every { playerPointsRepository.save(playerScores) } just runs

      service.persistPlayersAndScores(domainPlayers, playerScores)

      verify(exactly = 1) { playersRepository.saveAll(domainPlayers) }
      verify(exactly = 1) { playerPointsRepository.save(playerScores) }
    }
  }

  companion object {

    private const val AN_ALREADY_EXISTING_PLAYER_ATP_ID = "AN_ALREADY_EXISTING_PLAYER_ATP_ID"
    private const val A_FULL_NAME = "A_FULL_NAME"
    private const val AN_ID = "AN_ID"
    private const val A_TOURNAMENT_ID = 123
    private const val A_YEAR = 2222
  }
}