package com.posadeus.fantatennis.infrastructure.repository.database.mysql.it

import com.posadeus.fantatennis.domain.infrastructure.FantaTournamentsRepository
import com.posadeus.fantatennis.domain.model.FantaTournament.InvalidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.FantaTournamentsDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.FantaTournamentsEntity
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
class MySqlFantaTournamentsRepositoryIT {

  @Autowired
  private lateinit var fantaTournamentsDao: FantaTournamentsDao

  @Autowired
  private lateinit var mySqlFantaTournamentsRepository: FantaTournamentsRepository

  @BeforeEach
  fun setUp() {

    deleteAll()
  }

  @Test
  fun `fanta tournament retrieved`() {

    assertThat(fantaTournamentsDao.findAll()).isEmpty()

    val fantaTournamentsEntity = FantaTournamentsEntity(id = A_TOURNAMENT_ID,
                                                        startingTournament = A_STARTING_TOURNAMENT_ID,
                                                        endingTournament = AN_ENDING_TOURNAMENT_ID,
                                                        year = A_TOURNAMENT_YEAR)

    fantaTournamentsDao.save(fantaTournamentsEntity)

    val expected = ValidFantaTournament(id = A_TOURNAMENT_ID,
                                        startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                        endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                        tournamentYear = A_TOURNAMENT_YEAR)

    assertThat(mySqlFantaTournamentsRepository.retrieve(A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  @Test
  fun `fanta tournament not found`() {

    assertThat(fantaTournamentsDao.findAll()).isEmpty()

    val expected = InvalidFantaTournament

    assertThat(mySqlFantaTournamentsRepository.retrieve(A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  private fun deleteAll() {

    fantaTournamentsDao.deleteAll()
  }

  companion object {

    private const val A_TOURNAMENT_ID = 111
    private const val A_STARTING_TOURNAMENT_ID = 1
    private const val AN_ENDING_TOURNAMENT_ID = 10
    private const val A_TOURNAMENT_YEAR = 2222
  }
}