package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.it

import com.posadeus.fantatennis.app.configuration.infrastructure.jdbc.RetrieveFantaTournamentRepositoryConfiguration
import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTournamentRepository
import com.posadeus.fantatennis.domain.model.FantaTournament.InvalidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
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
@Import(IntegrationTestConfiguration::class, RetrieveFantaTournamentRepositoryConfiguration::class)
class JdbcRetrieveFantaTournamentRepositoryIT {

  @Autowired
  private lateinit var namedParameterJdbcTemplate: NamedParameterJdbcTemplate

  @Autowired
  private lateinit var repository: RetrieveFantaTournamentRepository

  @Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD)
  @Test
  fun `fanta tournament not found`() {

    val expected = InvalidFantaTournament

    assertThat(repository.retrieve(1)).isEqualTo(expected)
  }

  @SqlGroup(
      Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD),
      Sql(scripts = ["/test-containers/populate-database.sql"], executionPhase = BEFORE_TEST_METHOD)
  )
  @Test
  fun `fanta tournament found`() {

    val expected = ValidFantaTournament(id = 1,
                                        startingTournamentId = 1,
                                        endingTournamentId = 3,
                                        tournamentYear = 2025)

    assertThat(repository.retrieve(1)).isEqualTo(expected)
  }
}