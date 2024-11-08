package com.posadeus.fantatennis.infrastructure.repository.database.mysql.it

import com.posadeus.fantatennis.controller.model.team.TournamentCreationDto
import com.posadeus.fantatennis.domain.model.FantaTeamOk
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.MySqlFantaTeamsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.*
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.FantaTournamentsEntity
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.FantaTournamentsTeamsKeyEmbedded
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
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
class MySqlFantaTeamsRepositoryIT {

  @Autowired
  private lateinit var fantaTeamsDao: FantaTeamsDao

  @Autowired
  private lateinit var fantaTournamentsTeamsDao: FantaTournamentsTeamsDao

  @Autowired
  private lateinit var fantaTournamentsDao: FantaTournamentsDao

  @Autowired
  private lateinit var mySqlFantaTeamsRepository: MySqlFantaTeamsRepository

  @BeforeEach
  fun setUp() {

    deleteAll()
  }

  @Nested
  inner class CreateTeam {

    @Test
    fun `fanta team saved`() {

      assertThat(fantaTeamsDao.findAll()).isEmpty()
      assertThat(fantaTournamentsTeamsDao.findAll()).isEmpty()
      assertThat(fantaTournamentsDao.findAll()).isEmpty()

      val fantaTournamentsEntity = FantaTournamentsEntity(id = 1,
                                                          startingTournament = A_STARTING_TOURNAMENT_ID,
                                                          endingTournament = AN_ENDING_TOURNAMENT_ID,
                                                          year = A_TOURNAMENT_YEAR)


      fantaTournamentsDao.save(fantaTournamentsEntity)
      fantaTournamentsDao.flush()

      assertThat(fantaTournamentsDao.findAll()).size().isEqualTo(1)

      val tournamentCreationDto = TournamentCreationDto(id = 1,
                                                        startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                                        endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                                        tournamentYear = A_TOURNAMENT_YEAR)

      val expected = FantaTeamOk(id = 1, ownerId = AN_OWNER_ID)

      assertThat(mySqlFantaTeamsRepository.createTeam(AN_OWNER_ID, tournamentCreationDto)).isEqualTo(expected)

      assertThat(fantaTeamsDao.findById(1)).isPresent
      assertThat(fantaTournamentsTeamsDao.findById(FantaTournamentsTeamsKeyEmbedded(1, 1))).isPresent
    }
  }

  private fun deleteAll() {

    fantaTournamentsDao.deleteAll()
    fantaTournamentsTeamsDao.deleteAll()
    fantaTeamsDao.deleteAll()
  }

  companion object {

    private const val AN_OWNER_ID = "AN_OWNER_ID"
    private const val A_STARTING_TOURNAMENT_ID = 1
    private const val AN_ENDING_TOURNAMENT_ID = 10
    private const val A_TOURNAMENT_YEAR = 2222
  }
}