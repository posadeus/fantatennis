package com.posadeus.fantatennis.infrastructure.repository.database

import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTeamRepository
import com.posadeus.fantatennis.domain.model.DomainTeam.FoundDomainTeam
import com.posadeus.fantatennis.domain.model.DomainTeam.NotFoundDomainTeam
import com.posadeus.fantatennis.domain.model.TournamentRange
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.*
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTeamDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTournamentTeamDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcTeamDto.aJdbcTeamDto
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

  @Test
  fun `retrieve fails due to tournamentId not found`() {

    val fantaTeam = JdbcFantaTeamDto(teamId = A_TEAM_ID, ownerId = AN_OWNER_ID)
    val teams = listOf(aJdbcTeamDto(teamId = A_TEAM_ID))

    val expected = NotFoundDomainTeam

    every { fantaTeamDao.retrieveBy(A_TEAM_ID) } returns fantaTeam
    every { teamDao.retrieveBy(setOf(A_TEAM_ID)) } returns teams
    every { fantaTournamentTeamDao.retrieveByTeamId(A_TEAM_ID) } throws EmptyResultDataAccessException(1)

    assertThat(repository.retrieveByTeamId(A_TEAM_ID)).isEqualTo(expected)
  }

  @Test
  fun `retrieve succeed`() {

    val fantaTeam = JdbcFantaTeamDto(teamId = A_TEAM_ID, ownerId = AN_OWNER_ID)
    val team1 = aJdbcTeamDto(teamId = A_TEAM_ID,
                             playerId = A_PLAYER_ID,
                             startingTournamentId = A_STARTING_TOURNAMENT,
                             endingTournamentId = null)
    val team2 = aJdbcTeamDto(teamId = A_TEAM_ID,
                             playerId = ANOTHER_PLAYER_ID,
                             startingTournamentId = A_STARTING_TOURNAMENT,
                             endingTournamentId = AN_ENDING_TOURNAMENT)
    val teams = listOf(team1, team2)
    val fantaTournamentTeam = JdbcFantaTournamentTeamDto(teamId = A_TEAM_ID, fantaTournamentId = A_FANTA_TOURNAMENT_ID)

    val expected = FoundDomainTeam(teamId = A_TEAM_ID,
                                   ownerId = AN_OWNER_ID,
                                   fantaTournamentId = A_FANTA_TOURNAMENT_ID,
                                   players = mapOf(A_PLAYER_ID to TournamentRange(start = A_STARTING_TOURNAMENT, end = null),
                                                   ANOTHER_PLAYER_ID to TournamentRange(start = A_STARTING_TOURNAMENT, end = AN_ENDING_TOURNAMENT)))

    every { fantaTeamDao.retrieveBy(A_TEAM_ID) } returns fantaTeam
    every { teamDao.retrieveBy(setOf(A_TEAM_ID)) } returns teams
    every { fantaTournamentTeamDao.retrieveByTeamId(A_TEAM_ID) } returns fantaTournamentTeam

    assertThat(repository.retrieveByTeamId(A_TEAM_ID)).isEqualTo(expected)
  }

  companion object {

    private const val A_TEAM_ID = 123
    private const val A_FANTA_TOURNAMENT_ID = 34
    private const val A_STARTING_TOURNAMENT = 1
    private const val AN_ENDING_TOURNAMENT = 4
    private const val AN_OWNER_ID = "AN_OWNER_ID"
    private const val A_PLAYER_ID = "A_PLAYER_ID"
    private const val ANOTHER_PLAYER_ID = "ANOTHER_PLAYER_ID"
  }
}