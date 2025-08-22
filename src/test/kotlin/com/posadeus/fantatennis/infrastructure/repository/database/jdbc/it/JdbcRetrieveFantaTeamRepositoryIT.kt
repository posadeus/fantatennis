package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.it

import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.controller.model.team.TeamPlayerDto
import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTeamRepository
import com.posadeus.fantatennis.domain.model.FoundTeam
import com.posadeus.fantatennis.domain.model.TeamIdNotFoundTeam
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.JdbcRetrieveFantaTeamRepository
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(IntegrationTestConfiguration::class)
class JdbcRetrieveFantaTeamRepositoryIT {

  @Autowired
  private lateinit var namedParameterJdbcTemplate: NamedParameterJdbcTemplate

  private lateinit var repository: RetrieveFantaTeamRepository

  @BeforeEach
  fun setUp() {

    repository = JdbcRetrieveFantaTeamRepository(namedParameterJdbcTemplate)
  }

  @SqlGroup(
      Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD),
      Sql(scripts = ["/test-containers/populate-database.sql"], executionPhase = BEFORE_TEST_METHOD)
  )
  @Test
  fun `no team found`() {

    val expected = TeamIdNotFoundTeam

    assertThat(repository.retrieve(A_TEAM_ID)).isEqualTo(expected)
  }

  @SqlGroup(
      Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD),
      Sql(scripts = ["/test-containers/populate-database.sql"], executionPhase = BEFORE_TEST_METHOD),
      Sql(scripts = ["/test-containers/empty-team-added.sql"], executionPhase = BEFORE_TEST_METHOD)
  )
  @Test
  fun `team players not found`() {

    val expected = FoundTeam(team = TeamDto(owner = "SEVENTH_OWNER", players = emptyList(), totalScore = 0.00))

    assertThat(repository.retrieve(9)).isEqualTo(expected)
  }

  @SqlGroup(
      Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD),
      Sql(scripts = ["/test-containers/populate-database.sql"], executionPhase = BEFORE_TEST_METHOD)
  )
  @Test
  fun `fanta team retrieved`() {

    val expected = FoundTeam(team = TeamDto(owner = "AN_OWNER",
                                            players = listOf(TeamPlayerDto(fullName = "KKK LLL", fantaPoints = 34.00),
                                                             TeamPlayerDto(fullName = "SSS TTT", fantaPoints = 14.00),
                                                             TeamPlayerDto(fullName = "AAA BBB", fantaPoints = 12.00),
                                                             TeamPlayerDto(fullName = "MMM NNN", fantaPoints = 2.00)),
                                            totalScore = 62.00))

    assertThat(repository.retrieve(1)).isEqualTo(expected)
  }

  companion object {

    private const val A_TEAM_ID = 1234
  }
}