package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.it

import com.posadeus.fantatennis.controller.model.fantatournament.FantaTournamentDto
import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.controller.model.team.TeamPlayerDto
import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTournamentResultsRepository
import com.posadeus.fantatennis.domain.model.FoundFantaTournamentResults
import com.posadeus.fantatennis.domain.model.NotFoundFantaTournamentId
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.JdbcRetrieveFantaTournamentResultsRepository
import org.assertj.core.api.AssertionsForClassTypes.assertThat
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
class JdbcRetrieveFantaTournamentResultsRepositoryIT {

  @Autowired
  private lateinit var namedParameterJdbcTemplate: NamedParameterJdbcTemplate

  private lateinit var repository: RetrieveFantaTournamentResultsRepository

  @BeforeEach
  fun setUp() {

    repository = JdbcRetrieveFantaTournamentResultsRepository(namedParameterJdbcTemplate)
  }

  @Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD)
  @Test
  fun `tournament results not found`() {

    val expected = NotFoundFantaTournamentId

    assertThat(repository.retrieve(1)).isEqualTo(expected)
  }

  @SqlGroup(
      Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD),
      Sql(scripts = ["/test-containers/populate-database.sql"], executionPhase = BEFORE_TEST_METHOD)
  )
  @Test
  fun `tournament results successfully retrieved`() {

    val a0b1 = TeamPlayerDto(fullName = "AAA BBB", fantaPoints = 12.00)
    val mn98 = TeamPlayerDto(fullName = "MMM NNN", fantaPoints = 1.00)
    val k5l8 = TeamPlayerDto(fullName = "KKK LLL", fantaPoints = 34.00)
    val s7t5 = TeamPlayerDto(fullName = "SSS TTT", fantaPoints = 8.00)

    val c0d1 = TeamPlayerDto(fullName = "CCC DDD", fantaPoints = 7.00)
    val o7p6 = TeamPlayerDto(fullName = "OOO PPP", fantaPoints = 38.00)
    val qr43 = TeamPlayerDto(fullName = "QQQ RRR", fantaPoints = 41.00)

    val e2f8 = TeamPlayerDto(fullName = "EEE FFF", fantaPoints = 4.00)
    val gh00 = TeamPlayerDto(fullName = "GGG HHH", fantaPoints = 6.00)
    val i0j7 = TeamPlayerDto(fullName = "III JJJ", fantaPoints = 5.00)

    val team1 = TeamDto(owner = "AN_OWNER", players = listOf(k5l8, a0b1, s7t5, mn98), totalScore = 55.00)
    val team2 = TeamDto(owner = "ANOTHER_OWNER", players = listOf(qr43, o7p6, c0d1), totalScore = 86.00)
    val team3 = TeamDto(owner = "THIRD_OWNER", players = listOf(gh00, i0j7, e2f8), totalScore = 15.00)
    val teamOrderedListByTotalScore = listOf(team2, team1, team3)

    val expected = FoundFantaTournamentResults(FantaTournamentDto(teams = teamOrderedListByTotalScore))

    assertThat(repository.retrieve(1)).isEqualTo(expected)
  }
}