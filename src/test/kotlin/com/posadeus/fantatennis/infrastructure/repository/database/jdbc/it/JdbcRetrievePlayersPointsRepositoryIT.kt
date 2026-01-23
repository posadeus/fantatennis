package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.it

import com.github.benmanes.caffeine.cache.Cache
import com.posadeus.fantatennis.app.configuration.infrastructure.jdbc.dao.PlayerDaoConfiguration
import com.posadeus.fantatennis.app.configuration.infrastructure.jdbc.dao.PlayerPointsDaoConfiguration
import com.posadeus.fantatennis.domain.infrastructure.RetrievePlayersPointsRepository
import com.posadeus.fantatennis.domain.model.PlayersPoints.FoundPlayersPoints
import com.posadeus.fantatennis.domain.model.PlayersPoints.FoundPlayersPoints.PlayerPoints
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.JdbcRetrievePlayersPointsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerPointsDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.player.CachedPlayerDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.playerpoints.CachedPlayerPointsDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerPointsDto
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(IntegrationTestConfiguration::class, PlayerPointsDaoConfiguration::class, PlayerDaoConfiguration::class)
@TestPropertySource(properties = [
    "caches.caffeine.players-points-by-tournament-id-cache.expire-after-write-duration=10080",
    "caches.caffeine.players-points-by-tournament-id-cache.maximum-size=1000",
    "caches.caffeine.player-cache.expire-after-write-duration=10080",
    "caches.caffeine.player-cache.maximum-size=1000"
])
class JdbcRetrievePlayersPointsRepositoryIT {

  @Autowired
  private lateinit var playersPointsCache: Cache<Int, List<JdbcPlayerPointsDto>>

  @Autowired
  private lateinit var jdbcPlayerPointsDao: PlayerPointsDao

  @Autowired
  private lateinit var playerCache: Cache<Unit, List<JdbcPlayerDto>>

  @Autowired
  private lateinit var jdbcPlayerDao: PlayerDao

  private lateinit var repository: RetrievePlayersPointsRepository

  @BeforeEach
  fun setUp() {

    val cachedPlayerPointsDao = CachedPlayerPointsDao(playersPointsCache, jdbcPlayerPointsDao)
    val cachedPlayerDao = CachedPlayerDao(playerCache, jdbcPlayerDao)

    repository = JdbcRetrievePlayersPointsRepository(cachedPlayerPointsDao, cachedPlayerDao)

    playersPointsCache.invalidateAll()
    playerCache.invalidateAll()
  }

  @Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD)
  @Test
  fun `players points not found`() {

    val expected = FoundPlayersPoints(playersPoints = emptyList())

    assertThat(repository.retrieveBy(A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  @SqlGroup(
      Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD),
      Sql(scripts = ["/test-containers/populate-database.sql"], executionPhase = BEFORE_TEST_METHOD)
  )
  @Test
  fun `players found`() {

    val playerPoints1 = PlayerPoints(playerId = "A0B1", playerName = "AAA BBB", totalPoints = 8.00)
    val playerPoints2 = PlayerPoints(playerId = "C0D1", playerName = "CCC DDD", totalPoints = 2.00)
    val playerPoints3 = PlayerPoints(playerId = "E2F8", playerName = "EEE FFF", totalPoints = 0.00)
    val playerPoints4 = PlayerPoints(playerId = "GH00", playerName = "GGG HHH", totalPoints = 1.00)
    val playerPoints5 = PlayerPoints(playerId = "I0J7", playerName = "III JJJ", totalPoints = 1.00)
    val playerPoints6 = PlayerPoints(playerId = "K5L8", playerName = "KKK LLL", totalPoints = 10.00)
    val playerPoints7 = PlayerPoints(playerId = "MN98", playerName = "MMM NNN", totalPoints = 2.00)
    val playerPoints8 = PlayerPoints(playerId = "O7P6", playerName = "OOO PPP", totalPoints = 4.00)
    val playerPoints9 = PlayerPoints(playerId = "QR43", playerName = "QQQ RRR", totalPoints = 0.00)
    val playerPoints10 = PlayerPoints(playerId = "S7T5", playerName = "SSS TTT", totalPoints = 8.00)
    val expected = FoundPlayersPoints(playersPoints = listOf(playerPoints1,
                                                             playerPoints2,
                                                             playerPoints3,
                                                             playerPoints4,
                                                             playerPoints5,
                                                             playerPoints6,
                                                             playerPoints7,
                                                             playerPoints8,
                                                             playerPoints9,
                                                             playerPoints10))

    assertThat(repository.retrieveBy(A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  companion object {

    private const val A_TOURNAMENT_ID = 2
  }
}