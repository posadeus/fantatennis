package com.posadeus.fantatennis.infrastructure.repository.database.it

import com.github.benmanes.caffeine.cache.Cache
import com.posadeus.fantatennis.app.configuration.infrastructure.jdbc.PersistPlayersPointsRepositoryConfiguration
import com.posadeus.fantatennis.app.configuration.infrastructure.jdbc.dao.PlayerPointsDaoConfiguration
import com.posadeus.fantatennis.domain.infrastructure.PersistPlayersPointsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.repository.database.SqlPersistPlayersPointsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerPointsDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.playerpoints.CachedPlayerPointsDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerPointsDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.it.IntegrationTestConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(IntegrationTestConfiguration::class,
        PersistPlayersPointsRepositoryConfiguration::class,
        PlayerPointsDaoConfiguration::class)
@TestPropertySource(properties = [
  "caches.caffeine.players-points-by-tournament-id-cache.expire-after-write-duration=10080",
  "caches.caffeine.players-points-by-tournament-id-cache.maximum-size=1000",
  "caches.caffeine.players-points-by-tournament-year-cache.expire-after-write-duration=10080",
  "caches.caffeine.players-points-by-tournament-year-cache.maximum-size=1000"
])
class SqlPersistPlayersPointsRepositoryIT {

  @Autowired
  private lateinit var playersPointsByTournamentIdCache: Cache<Int, List<JdbcPlayerPointsDto>>

  @Autowired
  private lateinit var playersPointsByTournamentYearCache: Cache<Int, List<JdbcPlayerPointsDto>>

  @Autowired
  private lateinit var jdbcPlayerPointsDao: PlayerPointsDao

  @Autowired
  private lateinit var namedParameterJdbcTemplate: NamedParameterJdbcTemplate

  @Autowired
  private lateinit var repository: PersistPlayersPointsRepository

  @BeforeEach
  fun setUp() {

    val cachedPlayerPointsDao = CachedPlayerPointsDao(playersPointsByTournamentIdCache, playersPointsByTournamentYearCache, jdbcPlayerPointsDao)

    repository = SqlPersistPlayersPointsRepository(cachedPlayerPointsDao)
  }

  @SqlGroup(
      Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
      Sql(scripts = ["/test-containers/populate-database.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  )
  @Test
  fun `exception happens during inserts, transaction reverted`() {

    val player1 = AtpPlayer(id = "C0D1", mapOf(2026 to mapOf(2 to 10.00)))
    val player2 = AtpPlayer(id = "TOO_LONG_NAME", mapOf(2026 to mapOf(1 to 1.00, 2 to 20.00)))
    val players = setOf(player1, player2)

    val expected = FantaPointPersistence.FantaPointPersistenceFailure(FailureReason.PERSISTENCE_ERROR)

    assertThat(repository.persistAll(players)).isEqualTo(expected)

    val query = """
      SELECT *
      FROM PLAYERS_POINTS
      WHERE PLAYER_ID IN (:playerId)
      AND TOURNAMENT_YEAR = 2026;
    """.trimIndent()

    val queryParams = mapOf("playerId" to listOf("C0D1", "TOO_LONG_NAME"))

    assertThat(namedParameterJdbcTemplate.query(query, queryParams, playerPointsRowMapper).size).isEqualTo(1)
  }

  @SqlGroup(
      Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
      Sql(scripts = ["/test-containers/populate-database.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  )
  @Test
  fun `persist success for all inputs`() {

    val player1 = AtpPlayer(id = "QR43", mapOf(2025 to mapOf(4 to 0.00), 2026 to mapOf(2 to 10.00)))
    val player2 = AtpPlayer(id = "A0B1", mapOf(2026 to mapOf(3 to 1.00, 2 to 20.00)))
    val players = setOf(player1, player2)

    repository.persistAll(players)

    val query = """
      SELECT *
      FROM PLAYERS_POINTS
      WHERE PLAYER_ID IN (:playerId);
    """.trimIndent()

    val queryParams = mapOf("playerId" to listOf("QR43", "A0B1"))

    assertThat(namedParameterJdbcTemplate.query(query, queryParams, playerPointsRowMapper).size).isEqualTo(13)
  }

  @SqlGroup(
      Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
      Sql(scripts = ["/test-containers/populate-database.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  )
  @Test
  fun `persist success for all inputs with record already present`() {

    val player1 = AtpPlayer(id = "QR43", mapOf(2025 to mapOf(4 to 0.00), 2026 to mapOf(2 to 10.00)))
    val player2 = AtpPlayer(id = "A0B1", mapOf(2026 to mapOf(1 to 1.00, 2 to 20.00)))
    val players = setOf(player1, player2)

    repository.persistAll(players)

    val query = """
      SELECT *
      FROM PLAYERS_POINTS
      WHERE PLAYER_ID IN (:playerId);
    """.trimIndent()

    val queryParams = mapOf("playerId" to listOf("QR43", "A0B1"))

    assertThat(namedParameterJdbcTemplate.query(query, queryParams, playerPointsRowMapper).size).isEqualTo(12)
  }

  private val playerPointsRowMapper = RowMapper { rs, _ ->
    JdbcPlayerPointsDto(tournamentYear = rs.getInt("TOURNAMENT_YEAR"),
                        tournamentId = rs.getInt("TOURNAMENT_ID"),
                        playerId = rs.getString("PLAYER_ID"),
                        fantaPoints = rs.getDouble("FANTA_POINTS"))
  }
}