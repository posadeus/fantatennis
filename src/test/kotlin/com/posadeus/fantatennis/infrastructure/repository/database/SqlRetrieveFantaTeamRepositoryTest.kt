package com.posadeus.fantatennis.infrastructure.repository.database

import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTeamRepository
import com.posadeus.fantatennis.domain.model.DomainTeam.NotFoundDomainTeam
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.*
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTeamDto
import io.mockk.*
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.dao.EmptyResultDataAccessException

class SqlRetrieveFantaTeamRepositoryTest {

  private val teamDao: TeamDao = mockk()
  private val fantaTeamDao: FantaTeamDao = mockk()
  private val fantaTournamentTeamDao: FantaTournamentTeamDao = mockk()

  private val repository: RetrieveFantaTeamRepository = SqlRetrieveFantaTeamRepository(teamDao, fantaTeamDao, fantaTournamentTeamDao)

  @Test
  fun `retrieve fails due to fanta team not found`() {

    val message = "Where is the fanta team?"

    val expected = NotFoundDomainTeam

    every { fantaTeamDao.retrieveBy(A_TEAM_ID) } throws EmptyResultDataAccessException(message, 1)

    assertThat(repository.retrieveByTeamId(A_TEAM_ID)).isEqualTo(expected)

    verify { teamDao wasNot Called }
    verify { fantaTournamentTeamDao wasNot Called }
  }

  @Test
  fun `retrieve fails due to team not found`() {

    val fantaTeam = JdbcFantaTeamDto(teamId = A_TEAM_ID, ownerId = AN_OWNER_ID)

    val expected = NotFoundDomainTeam

    every { fantaTeamDao.retrieveBy(A_TEAM_ID) } returns fantaTeam
    every { teamDao.retrieveBy(setOf(A_TEAM_ID)) } returns emptyList()

    assertThat(repository.retrieveByTeamId(A_TEAM_ID)).isEqualTo(expected)

    verify { fantaTournamentTeamDao wasNot Called }
  }

  companion object {

    private const val A_TEAM_ID = 123
    private const val AN_OWNER_ID = "AN_OWNER_ID"
  }
}