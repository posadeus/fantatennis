package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.it

import com.github.benmanes.caffeine.cache.Cache
import com.posadeus.fantatennis.app.configuration.infrastructure.jdbc.dao.PlayerDaoConfiguration
import com.posadeus.fantatennis.domain.infrastructure.RetrievePlayersRepository
import com.posadeus.fantatennis.domain.model.DomainPlayer
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.JdbcRetrievePlayersRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.player.CachedPlayerDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerDto
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
import java.math.BigDecimal

@ExtendWith(SpringExtension::class)
@Import(IntegrationTestConfiguration::class, PlayerDaoConfiguration::class)
@TestPropertySource(properties = [
    "caches.caffeine.player-cache.expire-after-write-duration=10080",
    "caches.caffeine.player-cache.maximum-size=1000"
])
class JdbcRetrievePlayersRepositoryIT {

  @Autowired
  private lateinit var playerCache: Cache<Unit, List<JdbcPlayerDto>>

  @Autowired
  private lateinit var jdbcPlayerDao: PlayerDao

  private lateinit var repository: RetrievePlayersRepository

  @BeforeEach
  fun setUp() {

    val cachedPlayerDao = CachedPlayerDao(playerCache, jdbcPlayerDao)

    repository = JdbcRetrievePlayersRepository(cachedPlayerDao)

    playerCache.invalidateAll()
  }

  @Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD)
  @Test
  fun `players not found`() {

    val expected = emptySet<DomainPlayer>()

    assertThat(repository.retrieve()).isEqualTo(expected)
  }

  @SqlGroup(
      Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD),
      Sql(scripts = ["/test-containers/populate-database.sql"], executionPhase = BEFORE_TEST_METHOD)
  )
  @Test
  fun `players found`() {

    val player1 = DomainPlayer(id = "A0B1", atpId = "A0B1", fullName = "AAA BBB", rolandGarrosId = BigDecimal(123))
    val player2 = DomainPlayer(id = "C0D1", atpId = "C0D1", fullName = "CCC DDD")
    val player3 = DomainPlayer(id = "E2F8", atpId = "E2F8", fullName = "EEE FFF")
    val player4 = DomainPlayer(id = "GH00", atpId = "GH00", fullName = "GGG HHH")
    val player5 = DomainPlayer(id = "I0J7", atpId = "I0J7", fullName = "III JJJ")
    val player6 = DomainPlayer(id = "K5L8", atpId = "K5L8", fullName = "KKK LLL")
    val player7 = DomainPlayer(id = "MN98", atpId = "MN98", fullName = "MMM NNN")
    val player8 = DomainPlayer(id = "O7P6", atpId = "O7P6", fullName = "OOO PPP")
    val player9 = DomainPlayer(id = "QR43", atpId = "QR43", fullName = "QQQ RRR")
    val player0 = DomainPlayer(id = "S7T5", atpId = "S7T5", fullName = "SSS TTT")
    val expected = setOf(player1, player2, player3, player4, player5, player6, player7, player8, player9, player0)

    assertThat(repository.retrieve()).isEqualTo(expected)
  }
}