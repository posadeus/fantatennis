package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.it

import com.posadeus.fantatennis.controller.model.fantatournament.FantaTournamentToCreateDto
import com.posadeus.fantatennis.domain.infrastructure.CreateFantaTournamentRepository
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.JdbcCreateFantaTournamentRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(IntegrationTestConfiguration::class)
class JdbcCreateFantaTournamentRepositoryIT {

  @Autowired
  private lateinit var jdbcTemplate: JdbcTemplate

  private lateinit var repository: CreateFantaTournamentRepository

  @BeforeEach
  fun setUp() {

    repository = JdbcCreateFantaTournamentRepository(jdbcTemplate)
  }

  @Test
  fun `tournament successfully created`() {

    val dto = FantaTournamentToCreateDto(startingTournamentId = 2,
                                         endingTournamentId = 10,
                                         tournamentYear = 2022)

    val expected = ValidFantaTournament(id = 1,
                                        startingTournamentId = 2,
                                        endingTournamentId = 10,
                                        tournamentYear = 2022)

    assertThat(repository.create(dto)).isEqualTo(expected)
  }
}