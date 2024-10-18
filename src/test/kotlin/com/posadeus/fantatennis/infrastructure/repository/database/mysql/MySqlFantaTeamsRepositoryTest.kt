package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.domain.infrastructure.FantaTeamsRepository
import com.posadeus.fantatennis.domain.model.FantaTeamError
import com.posadeus.fantatennis.domain.model.FantaTeamOk
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.FantaTeamsDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.FantaTeamsEntity
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MySqlFantaTeamsRepositoryTest {

  private val dao: FantaTeamsDao = mockk()

  private val repository: FantaTeamsRepository = MySqlFantaTeamsRepository(dao)

  @Test
  fun `team is created`() {

    val toCreateFantaTeamsEntity = FantaTeamsEntity(ownerId = AN_OWNER_ID)
    val createdFantaTeamsEntity = FantaTeamsEntity(teamId = 1, ownerId = AN_OWNER_ID)

    val expected = FantaTeamOk(id = 1, ownerId = AN_OWNER_ID)

    every { dao.save(toCreateFantaTeamsEntity) } returns createdFantaTeamsEntity

    assertThat(repository.createTeam(AN_OWNER_ID)).isEqualTo(expected)
  }

  @Test
  fun `error during creation`() {

    val toCreateFantaTeamsEntity = FantaTeamsEntity(ownerId = AN_OWNER_ID)

    val expected = FantaTeamError

    every { dao.save(toCreateFantaTeamsEntity) } throws Exception()

    assertThat(repository.createTeam(AN_OWNER_ID)).isEqualTo(expected)
  }

  companion object {

    private const val AN_OWNER_ID = "AN_OWNER_ID"
  }
}