package com.posadeus.fantatennis.infrastructure.repository.database.mysql.it

import com.posadeus.fantatennis.domain.model.EmptyTournamentByTeam
import com.posadeus.fantatennis.domain.model.FoundTournamentByTeam
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.MySqlFantaTournamentsTeamsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.*
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@DataJpaTest
@ContextConfiguration(classes = [com.posadeus.fantatennis.app.Application::class])
@ComponentScan(basePackages = ["com.posadeus.fantatennis.app.configuration.infrastructure.mysql"])
class MySqlFantaTournamentsTeamsRepositoryIT {

  @Autowired
  private lateinit var fantaTournamentsTeamsDao: FantaTournamentsTeamsDao

  @Autowired
  private lateinit var fantaTournamentsDao: FantaTournamentsDao

  @Autowired
  private lateinit var fantaTeamsDao: FantaTeamsDao

  @Autowired
  private lateinit var mySqlFantaTournamentsTeamsRepository: MySqlFantaTournamentsTeamsRepository

  @BeforeEach
  fun setUp() {

    deleteAll()
  }

  @Test
  fun `retrieve tournament by teamId`() {

    assertThat(fantaTournamentsTeamsDao.findAll()).isEqualTo(arrayListOf<FantaTournamentsTeamsEntity>())

    val tournament = FantaTournamentsEntity(id = 2384,
                                            startingTournament = 1,
                                            endingTournament = 3,
                                            year = 2020)

    fantaTournamentsDao.save(tournament)

    val fantaTeam = FantaTeamsEntity(teamId = 123)

    fantaTeamsDao.save(fantaTeam)

    val fantaTournamentsTeams = FantaTournamentsTeamsEntity(FantaTournamentsTeamsKeyEmbedded(tournamentId = 2384,
                                                                                             teamId = 123),
                                                            tournament = tournament,
                                                            fantaTeam = fantaTeam)

    fantaTournamentsTeamsDao.save(fantaTournamentsTeams)

    val expected = FoundTournamentByTeam(teamId = 123,
                                         startingTournamentId = 1,
                                         endingTournamentId = 3,
                                         tournamentYear = 2020)

    assertThat(mySqlFantaTournamentsTeamsRepository.retrieveTournamentByTeamId(123)).isEqualTo(expected)
  }

  @Test
  fun `tournament by teamId not found`() {

    assertThat(fantaTournamentsTeamsDao.findAll()).isEqualTo(arrayListOf<FantaTournamentsTeamsEntity>())

    assertThat(mySqlFantaTournamentsTeamsRepository.retrieveTournamentByTeamId(123)).isEqualTo(EmptyTournamentByTeam)
  }

  private fun deleteAll() {

    fantaTournamentsTeamsDao.deleteAll()
    fantaTournamentsDao.deleteAll()
    fantaTeamsDao.deleteAll()
  }
}