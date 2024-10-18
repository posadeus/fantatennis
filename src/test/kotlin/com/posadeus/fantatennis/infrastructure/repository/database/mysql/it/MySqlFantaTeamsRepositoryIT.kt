package com.posadeus.fantatennis.infrastructure.repository.database.mysql.it

import com.posadeus.fantatennis.domain.model.FantaTeamOk
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.MySqlFantaTeamsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.FantaTeamsDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.FantaTeamsEntity
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
class MySqlFantaTeamsRepositoryIT {

  @Autowired
  private lateinit var fantaTeamsDao: FantaTeamsDao

  @Autowired
  private lateinit var mySqlFantaTeamsRepository: MySqlFantaTeamsRepository

  @BeforeEach
  fun setUp() {

    deleteAll()
  }

  @Test
  fun `fanta team saved`() {

    assertThat(fantaTeamsDao.findAll()).isEqualTo(arrayListOf<FantaTeamsEntity>())

    val expected = FantaTeamOk(id = 1, ownerId = AN_OWNER_ID)

    assertThat(mySqlFantaTeamsRepository.createTeam(AN_OWNER_ID)).isEqualTo(expected)
  }

  private fun deleteAll() {

    fantaTeamsDao.deleteAll()
  }

  companion object {

    private const val AN_OWNER_ID = "AN_OWNER_ID"
  }
}