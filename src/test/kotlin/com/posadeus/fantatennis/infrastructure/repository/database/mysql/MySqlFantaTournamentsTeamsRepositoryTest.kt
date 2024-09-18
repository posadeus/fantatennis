package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.domain.infrastructure.FantaTournamentsTeamsRepository
import com.posadeus.fantatennis.domain.model.EmptyTournamentByTeam
import com.posadeus.fantatennis.domain.model.FoundTournamentByTeam
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.FantaTournamentsTeamsDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.FantaTournamentsTeamsDto
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.Optional.empty
import java.util.Optional.of

class MySqlFantaTournamentsTeamsRepositoryTest {

  private val fantaTournamentsTeamsDao: FantaTournamentsTeamsDao = mockk()

  private val repository: FantaTournamentsTeamsRepository = MySqlFantaTournamentsTeamsRepository(fantaTournamentsTeamsDao)

  @Test
  fun `retrieve tournament data by teamId`() {

    val fantaTournamentsTeams = of(FantaTournamentsTeamsDto(teamId = 123,
                                                            startingTournamentId = 1,
                                                            endingTournamentId = 3,
                                                            tournamentYear = 2020))

    val expected = FoundTournamentByTeam(teamId = 123,
                                         startingTournamentId = 1,
                                         endingTournamentId = 3,
                                         tournamentYear = 2020)

    every { fantaTournamentsTeamsDao.findTournamentByTeamId(123) } returns fantaTournamentsTeams

    assertThat(repository.retrieveTournamentByTeamId(123)).isEqualTo(expected)
  }

  @Test
  fun `no tournament found for teamId`() {

    val expected = EmptyTournamentByTeam

    every { fantaTournamentsTeamsDao.findTournamentByTeamId(A_TEAM_ID) } returns empty()

    assertThat(repository.retrieveTournamentByTeamId(A_TEAM_ID)).isEqualTo(expected)
  }

  companion object {

    private const val A_TEAM_ID = 123
  }
}