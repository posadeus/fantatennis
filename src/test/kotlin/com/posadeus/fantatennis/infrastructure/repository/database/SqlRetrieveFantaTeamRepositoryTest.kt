package com.posadeus.fantatennis.infrastructure.repository.database

import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTeamRepository
import com.posadeus.fantatennis.domain.model.DomainTeam.NotFoundDomainTeam
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.*
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test

class SqlRetrieveFantaTeamRepositoryTest {

  private val teamDao: TeamDao = mockk()
  private val fantaTournamentDao: FantaTournamentDao = mockk()
  private val fantaTournamentTeamDao: FantaTournamentTeamDao = mockk()

  private val repository: RetrieveFantaTeamRepository = SqlRetrieveFantaTeamRepository(teamDao, fantaTournamentDao, fantaTournamentTeamDao)

  @Test
  fun `retrieve fails due to team not found`() {

    val expected = NotFoundDomainTeam

    every { teamDao.retrieveBy(setOf(A_TEAM_ID)) } returns emptyList()

    assertThat(repository.retrieveByTeamId(A_TEAM_ID)).isEqualTo(expected)
  }

  companion object {

    private const val A_TEAM_ID = 123
  }
}