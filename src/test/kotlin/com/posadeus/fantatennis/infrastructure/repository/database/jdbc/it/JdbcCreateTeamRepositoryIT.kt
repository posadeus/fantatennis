package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.it

import com.github.benmanes.caffeine.cache.Cache
import com.posadeus.fantatennis.app.configuration.infrastructure.jdbc.CreateTeamRepositoryConfiguration
import com.posadeus.fantatennis.app.configuration.infrastructure.jdbc.dao.*
import com.posadeus.fantatennis.domain.exception.FantaTeamCreationException
import com.posadeus.fantatennis.domain.infrastructure.CreateTeamRepository
import com.posadeus.fantatennis.domain.model.FantaTeam
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.*
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTeamDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTournamentDto
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD
import org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(IntegrationTestConfiguration::class,
        CreateTeamRepositoryConfiguration::class,
        FantaTournamentDaoConfiguration::class,
        FantaTeamDaoConfiguration::class,
        FantaTournamentTeamDaoConfiguration::class)
@TestPropertySource(properties = [
  "caches.caffeine.fanta-tournament-cache.expire-after-write-duration=10080",
  "caches.caffeine.fanta-tournament-cache.expire-after-access-duration=10080",
  "caches.caffeine.fanta-tournament-cache.maximum-size=1000",
  "caches.caffeine.fanta-team-cache.expire-after-write-duration=10080",
  "caches.caffeine.fanta-team-cache.expire-after-access-duration=10080",
  "caches.caffeine.fanta-team-cache.maximum-size=1000"
])
class JdbcCreateTeamRepositoryIT {

  @Autowired
  private lateinit var fantaTournamentCache: Cache<Int, JdbcFantaTournamentDto>

  @Autowired
  private lateinit var jdbcFantaTournamentDao: FantaTournamentDao

  @Autowired
  private lateinit var fantaTeamCache: Cache<Int, JdbcFantaTeamDto>

  @Autowired
  private lateinit var jdbcFantaTeamDao: FantaTeamDao

  @Autowired
  private lateinit var jdbcFantaTournamentTeamDao: FantaTournamentTeamDao

  @Autowired
  private lateinit var jdbcTemplate: JdbcTemplate

  @Autowired
  private lateinit var repository: CreateTeamRepository

  @Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD)
  @Test
  fun `team creation fails due to missing fanta tournament`() {

    assertThrows<FantaTeamCreationException> { repository.create(AN_OWNER_ID, 1) }
  }

  @SqlGroup(
      Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD),
      Sql(scripts = ["/test-containers/populate-database.sql"], executionPhase = BEFORE_TEST_METHOD)
  )
  @Test
  fun `team creation fails due to insert error on first insert query`() {

    val tooLongOwnerId = "A".repeat(101)

    assertThrows<FantaTeamCreationException> { repository.create(tooLongOwnerId, 1) }
  }

  @SqlGroup(
      Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD),
      Sql(scripts = ["/test-containers/populate-database.sql"], executionPhase = BEFORE_TEST_METHOD)
  )
  @Test
  fun `team creation works`() {

    val expected = FantaTeam(id = 9, ownerId = AN_OWNER_ID)

    assertThat(repository.create(AN_OWNER_ID, 1)).isEqualTo(expected)
  }

  @SqlGroup(
      Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD),
      Sql(scripts = ["/test-containers/alter-fanta_tournaments_teams-to-have-error.sql"], executionPhase = BEFORE_TEST_METHOD),
      Sql(scripts = ["/test-containers/drop-constraints.sql"], executionPhase = AFTER_TEST_METHOD)
  )
  @Test
  fun `team creation fails due to error on fantaTournamentsTeams insert and verify transaction have been rollback`() {

    assertThrows<FantaTeamCreationException> { repository.create(AN_OWNER_ID, 1) }

    val sql = "SELECT COUNT(*) FROM FANTA_TEAMS WHERE TEAM_ID = 9"
    val count = jdbcTemplate.queryForObject(sql, Int::class.java)

    assertThat(count!!).isEqualTo(0)
  }

  companion object {

    private const val AN_OWNER_ID = "AN_OWNER_ID"
  }
}