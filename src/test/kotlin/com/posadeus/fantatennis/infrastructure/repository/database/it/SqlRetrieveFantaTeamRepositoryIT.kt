package com.posadeus.fantatennis.infrastructure.repository.database.it

import com.github.benmanes.caffeine.cache.Cache
import com.posadeus.fantatennis.app.configuration.infrastructure.jdbc.dao.*
import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTeamRepository
import com.posadeus.fantatennis.domain.model.DomainTeam.FoundDomainTeam
import com.posadeus.fantatennis.domain.model.DomainTeam.NotFoundDomainTeam
import com.posadeus.fantatennis.domain.model.TeamId
import com.posadeus.fantatennis.domain.model.TournamentRange
import com.posadeus.fantatennis.infrastructure.repository.database.SqlRetrieveFantaTeamRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.*
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.fantateam.CachedFantaTeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.fantatournamentteam.CachedFantaTournamentTeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.team.CachedTeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.*
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.it.IntegrationTestConfiguration
import org.assertj.core.api.AssertionsForClassTypes.assertThat
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
@Import(IntegrationTestConfiguration::class,
        TeamDaoConfiguration::class,
        FantaTeamDaoConfiguration::class,
        FantaTournamentTeamDaoConfiguration::class)
@TestPropertySource(properties = [
  "caches.caffeine.team-cache.expire-after-write-duration=10080",
  "caches.caffeine.team-cache.maximum-size=1000",
  "caches.caffeine.fanta-team-cache.expire-after-write-duration=10080",
  "caches.caffeine.fanta-team-cache.expire-after-access-duration=10080",
  "caches.caffeine.fanta-team-cache.maximum-size=1000",
  "caches.caffeine.fanta-tournament-team-by-tournament-id-cache.expire-after-write-duration=10080",
  "caches.caffeine.fanta-tournament-team-by-tournament-id-cache.maximum-size=1000",
  "caches.caffeine.fanta-tournament-team-by-team-id-cache.expire-after-write-duration=10080",
  "caches.caffeine.fanta-tournament-team-by-team-id-cache.maximum-size=1000",
])
class SqlRetrieveFantaTeamRepositoryIT {

  @Autowired
  private lateinit var teamCache: Cache<Set<TeamId>, List<JdbcTeamDto>>

  @Autowired
  private lateinit var fantaTeamCache: Cache<Int, JdbcFantaTeamDto>

  @Autowired
  private lateinit var fantaTournamentTeamCacheByTournamentId: Cache<Int, JdbcFantaTournamentTeamDto>

  @Autowired
  private lateinit var fantaTournamentTeamCacheByTeamId: Cache<Int, JdbcFantaTournamentTeamDto>

  @Autowired
  private lateinit var jdbcTeamDao: TeamDao

  @Autowired
  private lateinit var jdbcFantaTeamDao: FantaTeamDao

  @Autowired
  private lateinit var jdbcFantaTournamentTeamDao: FantaTournamentTeamDao

  private lateinit var repository: RetrieveFantaTeamRepository

  @BeforeEach
  fun setUp() {

    val cachedTeamDao = CachedTeamDao(teamCache, jdbcTeamDao)
    val cachedFantaTeamDao = CachedFantaTeamDao(fantaTeamCache, jdbcFantaTeamDao)
    val cachedFantaTournamentTeamDao = CachedFantaTournamentTeamDao(fantaTournamentTeamCacheByTournamentId,
                                                                    fantaTournamentTeamCacheByTeamId,
                                                                    jdbcFantaTournamentTeamDao)

    repository = SqlRetrieveFantaTeamRepository(cachedTeamDao, cachedFantaTeamDao, cachedFantaTournamentTeamDao)

    teamCache.invalidateAll()
    fantaTeamCache.invalidateAll()
    fantaTournamentTeamCacheByTeamId.invalidateAll()
  }

  @Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD)
  @Test
  fun `no team found`() {

    val expected = NotFoundDomainTeam

    assertThat(repository.retrieveByTeamId(A_TEAM_ID)).isEqualTo(expected)
  }

  @SqlGroup(
      Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD),
      Sql(scripts = ["/test-containers/populate-database.sql"], executionPhase = BEFORE_TEST_METHOD)
  )
  @Test
  fun `retrieve works`() {

    val expected = FoundDomainTeam(teamId = 1,
                                   ownerId = "AN_OWNER",
                                   fantaTournamentId = 1,
                                   players = mapOf("A0B1" to TournamentRange(start = 1, end = null),
                                                   "MN98" to TournamentRange(start = 1, end = 1),
                                                   "K5L8" to TournamentRange(start = 1, end = null),
                                                   "S7T5" to TournamentRange(start = 2, end = 2),
                                                   "MN98" to TournamentRange(start = 3, end = null)))

    assertThat(repository.retrieveByTeamId(1)).isEqualTo(expected)
  }

  companion object {

    private const val A_TEAM_ID = 123
  }
}