package com.posadeus.fantatennis.domain.service.team

import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.controller.model.team.TeamPlayerDto
import com.posadeus.fantatennis.domain.infrastructure.FantaTournamentsTeamsRepository
import com.posadeus.fantatennis.domain.infrastructure.PlayerPointsRepository
import com.posadeus.fantatennis.domain.model.*
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TeamServiceTest {

  private val fantaTournamentsTeamsRepository: FantaTournamentsTeamsRepository = mockk()
  private val playerPointsRepository: PlayerPointsRepository = mockk()

  private val service = TeamService(fantaTournamentsTeamsRepository,
                                    playerPointsRepository)

  @Test
  fun `retrieve a team`() {

    val tournamentByTeam = FoundTournamentByTeam(teamId = A_TEAM_ID,
                                                 startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                                 endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                                 tournamentYear = A_TOURNAMENT_YEAR)
    val playerPoints1 = PlayerPoints(playerId = A_PLAYER_ID, playerName = "A_PLAYER_NAME_1", totalPoints = 20.0)
    val playerPoints2 = PlayerPoints(playerId = ANOTHER_PLAYER_ID, playerName = "A_PLAYER_NAME_2", totalPoints = 18.0)
    val teamOrderedPlayerPoints = TeamOrderedPlayerPoints(listOf(playerPoints1, playerPoints2))

    val teamPlayerDto1 = TeamPlayerDto(fullName = "A_PLAYER_NAME_1", fantaPoints = 20.0)
    val teamPlayerDto2 = TeamPlayerDto(fullName = "A_PLAYER_NAME_2", fantaPoints = 18.0)
    val expected = FoundTeam(TeamDto(listOf(teamPlayerDto1, teamPlayerDto2), 38.0))

    every { fantaTournamentsTeamsRepository.retrieveTournamentByTeamId(A_TEAM_ID) } returns tournamentByTeam
    every { playerPointsRepository.retrieve(tournamentByTeam) } returns teamOrderedPlayerPoints

    assertThat(service.getTeam(A_TEAM_ID)).isEqualTo(expected)
  }

  @Test
  fun `tournamentByTeam not found`() {

    val expected = TeamIdNotFoundTeam

    every { fantaTournamentsTeamsRepository.retrieveTournamentByTeamId(A_TEAM_ID) } returns EmptyTournamentByTeam

    assertThat(service.getTeam(A_TEAM_ID)).isEqualTo(expected)

    verify { playerPointsRepository wasNot called }
  }

  companion object {

    private const val A_TEAM_ID = 1
    private const val A_STARTING_TOURNAMENT_ID = 1
    private const val AN_ENDING_TOURNAMENT_ID = 1
    private const val A_TOURNAMENT_YEAR = 1
    private const val A_PLAYER_ID = "A_PLAYER_ID"
    private const val ANOTHER_PLAYER_ID = "ANOTHER_PLAYER_ID"
  }
}