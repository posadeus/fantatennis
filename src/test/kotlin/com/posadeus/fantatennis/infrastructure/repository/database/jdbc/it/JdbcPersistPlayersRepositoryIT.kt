package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.it

import com.github.benmanes.caffeine.cache.Cache
import com.posadeus.fantatennis.app.configuration.infrastructure.jdbc.PersistPlayersRepositoryConfiguration
import com.posadeus.fantatennis.app.configuration.infrastructure.jdbc.dao.PlayerDaoConfiguration
import com.posadeus.fantatennis.domain.infrastructure.PersistPlayersRepository
import com.posadeus.fantatennis.domain.model.DomainPlayer
import com.posadeus.fantatennis.domain.model.PlayerPersistence.PlayerPersistenceFailure
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerDto
import org.assertj.core.api.Assertions.assertThat
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
@Import(IntegrationTestConfiguration::class, PersistPlayersRepositoryConfiguration::class, PlayerDaoConfiguration::class)
@TestPropertySource(properties = [
  "caches.caffeine.player-cache.expire-after-write-duration=10080",
  "caches.caffeine.player-cache.maximum-size=1000"
])
class JdbcPersistPlayersRepositoryIT {

  @Autowired
  private lateinit var playerCache: Cache<Unit, List<JdbcPlayerDto>>

  @Autowired
  private lateinit var jdbcPlayerDao: PlayerDao

  @Autowired
  private lateinit var repository: PersistPlayersRepository

  @SqlGroup(
      Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD),
      Sql(scripts = ["/test-containers/populate-database.sql"], executionPhase = BEFORE_TEST_METHOD)
  )
  @Test
  fun `exception happens during inserts, transaction reverted`() {

    val playersInDatabase = jdbcPlayerDao.retrieveAll().size

    val player1 = DomainPlayer(id = "AAAA", atpId = "AAAA", fullName = A_FULL_NAME)
    val player2 = DomainPlayer(id = "A0B1", atpId = "A0B1", fullName = ANOTHER_FULL_NAME)
    val players = setOf(player1, player2)

    val expectedError = """
      PreparedStatementCallback; SQL [INSERT INTO PLAYERS
      (PLAYER_ID, ATP_TOUR_ID, FULL_NAME)
      VALUES(?, ?, ?);]; Duplicate entry 'A0B1' for key 'PLAYERS.PRIMARY'
    """.trimIndent()

    val expected = PlayerPersistenceFailure(message = "Persistence failure, please verify your input.", error = expectedError)

    assertThat(repository.persistAll(players)).isEqualTo(expected)

    assertThat(jdbcPlayerDao.retrieveAll().size).isEqualTo(playersInDatabase)
  }

  @SqlGroup(
      Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD),
      Sql(scripts = ["/test-containers/populate-database.sql"], executionPhase = BEFORE_TEST_METHOD)
  )
  @Test
  fun `persist success for all inputs`() {

    val playersInDatabase = jdbcPlayerDao.retrieveAll().size

    val player1 = DomainPlayer(id = A_PLAYER_ID, atpId = AN_ATP_PLAYER_ID, fullName = A_FULL_NAME)
    val player2 = DomainPlayer(id = ANOTHER_PLAYER_ID, atpId = ANOTHER_ATP_PLAYER_ID, fullName = ANOTHER_FULL_NAME)
    val players = setOf(player1, player2)

    repository.persistAll(players)

    assertThat(jdbcPlayerDao.retrieveAll().size).isEqualTo(playersInDatabase + 2)
  }

  companion object {

    private const val A_PLAYER_ID = "AAAA"
    private const val AN_ATP_PLAYER_ID = "AAAA"
    private const val A_FULL_NAME = "A_FULL_NAME"
    private const val ANOTHER_PLAYER_ID = "BBBB"
    private const val ANOTHER_ATP_PLAYER_ID = "BBBB"
    private const val ANOTHER_FULL_NAME = "ANOTHER_FULL_NAME"
  }
}