package com.posadeus.fantatennis.infrastructure.repository.database

import com.posadeus.fantatennis.domain.exception.FantaTeamCreationException
import com.posadeus.fantatennis.domain.infrastructure.CreateTeamRepository
import com.posadeus.fantatennis.domain.model.FantaTeam
import com.posadeus.fantatennis.infrastructure.assertThrowsWithMessage
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.*
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcFantaTournamentDto.aJdbcFantaTournamentDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcFantaTournamentTeamDto.aJdbcFantaTournamentTeamDto
import com.posadeus.fantatennis.infrastructure.repository.exception.NoInsertException
import io.mockk.*
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.dao.EmptyResultDataAccessException

class SqlCreateTeamRepositoryTest {

  private val fantaTournamentDao: FantaTournamentDao = mockk()
  private val fantaTeamDao: FantaTeamDao = mockk()
  private val fantaTournamentTeamDao: FantaTournamentTeamDao = mockk()

  private val repository: CreateTeamRepository = SqlCreateTeamRepository(fantaTournamentDao, fantaTeamDao, fantaTournamentTeamDao)

  @Test
  fun `create team fails due to missing fanta tournament`() {

    val errorMessage = "What do you want?"
    val expectedMessage = "Error during DB operation $errorMessage"

    every { fantaTournamentDao.retrieveBy(A_MISSING_FANTA_TOURNAMENT_ID) } throws EmptyResultDataAccessException(errorMessage, 1)

    assertThrowsWithMessage<FantaTeamCreationException>(expectedMessage) { repository.create(AN_OWNER_ID, A_MISSING_FANTA_TOURNAMENT_ID) }

    verify { fantaTeamDao wasNot called }
    verify { fantaTournamentTeamDao wasNot called }
  }

  @Test
  fun `create team fails due to an error during fantaTeams creation`() {

    val fantaTournamentDto = aJdbcFantaTournamentDto(id = A_FANTA_TOURNAMENT_ID)

    val errorMessage = "I'm an error"
    val expectedMessage = "Error during DB operation $errorMessage"

    every { fantaTournamentDao.retrieveBy(A_FANTA_TOURNAMENT_ID) } returns fantaTournamentDto
    every { fantaTeamDao.persist(AN_OWNER_ID) } throws NoInsertException(errorMessage)

    assertThrowsWithMessage<FantaTeamCreationException>(expectedMessage) { repository.create(AN_OWNER_ID, A_FANTA_TOURNAMENT_ID) }

    verify { fantaTournamentTeamDao wasNot called }
  }

  @Test
  fun `create team fails due to an error during fantaTournamentsTeams creation`() {

    val fantaTournamentDto = aJdbcFantaTournamentDto(id = A_FANTA_TOURNAMENT_ID)
    val fantaTournamentTeam = aJdbcFantaTournamentTeamDto(teamId = A_TEAM_ID, fantaTournamentId = A_FANTA_TOURNAMENT_ID)

    val errorMessage = "I'm an error"
    val expectedMessage = "Error during DB operation $errorMessage"

    every { fantaTournamentDao.retrieveBy(A_FANTA_TOURNAMENT_ID) } returns fantaTournamentDto
    every { fantaTeamDao.persist(AN_OWNER_ID) } returns A_TEAM_ID
    every { fantaTournamentTeamDao.persist(fantaTournamentTeam) } throws NoInsertException(errorMessage)

    assertThrowsWithMessage<FantaTeamCreationException>(expectedMessage) { repository.create(AN_OWNER_ID, A_FANTA_TOURNAMENT_ID) }
  }

  @Test
  fun `create team works`() {

    val fantaTournamentDto = aJdbcFantaTournamentDto(id = 77)
    val fantaTournamentTeam = aJdbcFantaTournamentTeamDto(teamId = 42, fantaTournamentId = 77)

    val expected = FantaTeam(id = 42, ownerId = "AN_OWNER_ID")

    every { fantaTournamentDao.retrieveBy(77) } returns fantaTournamentDto
    every { fantaTeamDao.persist("AN_OWNER_ID") } returns 42
    every { fantaTournamentTeamDao.persist(fantaTournamentTeam) } returns Unit

    assertThat(repository.create("AN_OWNER_ID", 77)).isEqualTo(expected)
  }

  companion object {

    private const val AN_OWNER_ID = "AN_OWNER_ID"
    private const val A_MISSING_FANTA_TOURNAMENT_ID = 1
    private const val A_FANTA_TOURNAMENT_ID = 1
    private const val A_TEAM_ID = 42
  }
}