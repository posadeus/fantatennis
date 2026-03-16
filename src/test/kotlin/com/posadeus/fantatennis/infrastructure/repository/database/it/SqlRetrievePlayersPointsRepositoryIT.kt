package com.posadeus.fantatennis.infrastructure.repository.database.it

import com.github.benmanes.caffeine.cache.Cache
import com.posadeus.fantatennis.app.configuration.infrastructure.jdbc.dao.PlayerDaoConfiguration
import com.posadeus.fantatennis.app.configuration.infrastructure.jdbc.dao.PlayerPointsDaoConfiguration
import com.posadeus.fantatennis.domain.infrastructure.RetrievePlayersPointsRepository
import com.posadeus.fantatennis.domain.model.PlayersPoints.FoundPlayersPoints
import com.posadeus.fantatennis.domain.model.PlayersPoints.FoundPlayersPoints.PlayerPoints
import com.posadeus.fantatennis.infrastructure.repository.database.SqlRetrievePlayersPointsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerPointsDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.player.CachedPlayerDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.playerpoints.CachedPlayerPointsDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerPointsDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.it.IntegrationTestConfiguration
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.*
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
    "caches.caffeine.players-points-by-tournament-year-cache.expire-after-write-duration=10080",
    "caches.caffeine.players-points-by-tournament-year-cache.maximum-size=1000",
    "caches.caffeine.player-cache.expire-after-write-duration=10080",
    "caches.caffeine.player-cache.maximum-size=1000"
])
class SqlRetrievePlayersPointsRepositoryIT {

  @Autowired
  private lateinit var playersPointsByTournamentIdCache: Cache<Int, List<JdbcPlayerPointsDto>>

  @Autowired
  private lateinit var playersPointsByTournamentYearCache: Cache<Int, List<JdbcPlayerPointsDto>>

  @Autowired
  private lateinit var jdbcPlayerPointsDao: PlayerPointsDao

  @Autowired
  private lateinit var playerCache: Cache<Unit, List<JdbcPlayerDto>>

  @Autowired
  private lateinit var jdbcPlayerDao: PlayerDao

  private lateinit var repository: RetrievePlayersPointsRepository

  @BeforeEach
  fun setUp() {

    val cachedPlayerPointsDao = CachedPlayerPointsDao(playersPointsByTournamentIdCache, playersPointsByTournamentYearCache, jdbcPlayerPointsDao)
    val cachedPlayerDao = CachedPlayerDao(playerCache, jdbcPlayerDao)

    repository = SqlRetrievePlayersPointsRepository(cachedPlayerPointsDao, cachedPlayerDao)

    playersPointsByTournamentIdCache.invalidateAll()
    playersPointsByTournamentYearCache.invalidateAll()
    playerCache.invalidateAll()
  }

  @Nested
  inner class RetrieveByTournamentId {

    @Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD)
    @Test
    fun `players points not found`() {

      val expected = FoundPlayersPoints(playersPoints = emptyList())

      assertThat(repository.retrieveByTournamentId(A_TOURNAMENT_ID)).isEqualTo(expected)
    }

    @SqlGroup(
        Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD),
        Sql(scripts = ["/test-containers/populate-database.sql"], executionPhase = BEFORE_TEST_METHOD)
    )
    @Test
    fun `players found`() {

      val playerPoints1 = PlayerPoints(playerId = "A0B1",
                                       playerName = "AAA BBB",
                                       pointsByTournament = mapOf(A_TOURNAMENT_ID to 8.00),
                                       totalPoints = 8.00)
      val playerPoints2 = PlayerPoints(playerId = "C0D1",
                                       playerName = "CCC DDD",
                                       pointsByTournament = mapOf(A_TOURNAMENT_ID to 2.00),
                                       totalPoints = 2.00)
      val playerPoints3 = PlayerPoints(playerId = "E2F8",
                                       playerName = "EEE FFF",
                                       pointsByTournament = mapOf(A_TOURNAMENT_ID to 0.00),
                                       totalPoints = 0.00)
      val playerPoints4 = PlayerPoints(playerId = "GH00",
                                       playerName = "GGG HHH",
                                       pointsByTournament = mapOf(A_TOURNAMENT_ID to 1.00),
                                       totalPoints = 1.00)
      val playerPoints5 = PlayerPoints(playerId = "I0J7",
                                       playerName = "III JJJ",
                                       pointsByTournament = mapOf(A_TOURNAMENT_ID to 1.00),
                                       totalPoints = 1.00)
      val playerPoints6 = PlayerPoints(playerId = "K5L8",
                                       playerName = "KKK LLL",
                                       pointsByTournament = mapOf(A_TOURNAMENT_ID to 10.00),
                                       totalPoints = 10.00)
      val playerPoints7 = PlayerPoints(playerId = "MN98",
                                       playerName = "MMM NNN",
                                       pointsByTournament = mapOf(A_TOURNAMENT_ID to 2.00),
                                       totalPoints = 2.00)
      val playerPoints8 = PlayerPoints(playerId = "O7P6",
                                       playerName = "OOO PPP",
                                       pointsByTournament = mapOf(A_TOURNAMENT_ID to 4.00),
                                       totalPoints = 4.00)
      val playerPoints9 = PlayerPoints(playerId = "QR43",
                                       playerName = "QQQ RRR",
                                       pointsByTournament = mapOf(A_TOURNAMENT_ID to 0.00),
                                       totalPoints = 0.00)
      val playerPoints10 = PlayerPoints(playerId = "S7T5",
                                        playerName = "SSS TTT",
                                        pointsByTournament = mapOf(A_TOURNAMENT_ID to 8.00),
                                        totalPoints = 8.00)
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

      assertThat(repository.retrieveByTournamentId(A_TOURNAMENT_ID)).isEqualTo(expected)
    }
  }

  @Nested
  inner class RetrieveByYear {

    @Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD)
    @Test
    fun `no player points found for year`() {

      assertThat(repository.retrieveByYear(A_YEAR)).isEqualTo(FoundPlayersPoints(emptyList()))
    }

    @SqlGroup(
        Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD),
        Sql(scripts = ["/test-containers/populate-database.sql"], executionPhase = BEFORE_TEST_METHOD)
    )
    @Test
    fun `player points found for year`() {

      val playerPoints1 = PlayerPoints(playerId = "A0B1",
                                       playerName = "AAA BBB",
                                       pointsByTournament = mapOf(1 to 0.00, 2 to 8.00, 3 to 4.00, 4 to 2.00),
                                       totalPoints = 14.00)
      val playerPoints2 = PlayerPoints(playerId = "C0D1",
                                       playerName = "CCC DDD",
                                       pointsByTournament = mapOf(1 to 1.00, 2 to 2.00, 3 to 4.00, 4 to 0.00),
                                       totalPoints = 7.00)
      val playerPoints3 = PlayerPoints(playerId = "E2F8",
                                       playerName = "EEE FFF",
                                       pointsByTournament = mapOf(1 to 2.00, 2 to 0.00, 3 to 2.00, 4 to 1.00),
                                       totalPoints = 5.00)
      val playerPoints4 = PlayerPoints(playerId = "GH00",
                                       playerName = "GGG HHH",
                                       pointsByTournament = mapOf(1 to 4.00, 2 to 1.00, 3 to 1.00, 4 to 1.00),
                                       totalPoints = 7.00)
      val playerPoints5 = PlayerPoints(playerId = "I0J7",
                                       playerName = "III JJJ",
                                       pointsByTournament = mapOf(1 to 4.00, 2 to 1.00, 3 to 0.00, 4 to 2.00),
                                       totalPoints = 7.00)
      val playerPoints6 = PlayerPoints(playerId = "K5L8",
                                       playerName = "KKK LLL",
                                       pointsByTournament = mapOf(1 to 16.00, 2 to 10.00, 3 to 8.00, 4 to 4.00),
                                       totalPoints = 38.00)
      val playerPoints7 = PlayerPoints(playerId = "MN98",
                                       playerName = "MMM NNN",
                                       pointsByTournament = mapOf(1 to 0.00, 2 to 2.00, 3 to 1.00, 4 to 10.00),
                                       totalPoints = 13.00)
      val playerPoints8 = PlayerPoints(playerId = "O7P6",
                                       playerName = "OOO PPP",
                                       pointsByTournament = mapOf(1 to 32.00, 2 to 4.00, 3 to 2.00, 4 to 1.00),
                                       totalPoints = 39.00)
      val playerPoints9 = PlayerPoints(playerId = "QR43",
                                       playerName = "QQQ RRR",
                                       pointsByTournament = mapOf(1 to 40.00, 2 to 0.00, 3 to 1.00),
                                       totalPoints = 41.00)
      val playerPoints10 = PlayerPoints(playerId = "S7T5",
                                        playerName = "SSS TTT",
                                        pointsByTournament = mapOf(1 to 2.00, 2 to 8.00, 3 to 4.00, 4 to 8.00),
                                        totalPoints = 22.00)

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

      assertThat(repository.retrieveByYear(2025)).isEqualTo(expected)
    }
  }

  companion object {

    private const val A_TOURNAMENT_ID = 2
    private const val A_YEAR = 2025
  }
}
