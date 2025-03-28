package com.posadeus.fantatennis.domain.service

import com.posadeus.fantatennis.controller.model.team.*
import com.posadeus.fantatennis.domain.infrastructure.TeamsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.TestDomainPlayer.aDomainPlayer
import com.posadeus.fantatennis.domain.model.TestTournament.aTournament
import com.posadeus.fantatennis.domain.service.player.PlayerService
import com.posadeus.fantatennis.domain.service.team.RetrieveTeamService
import com.posadeus.fantatennis.domain.service.tournament.RetrieveTournamentsService
import io.mockk.*
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.Test

class SwapPlayersTeamServiceTest {

  private val retrieveTeamService: RetrieveTeamService = mockk()
  private val playerService: PlayerService = mockk()
  private val retrieveTournamentsService: RetrieveTournamentsService = mockk()
  private val teamsRepository: TeamsRepository = mockk()

  private val service = SwapPlayersTeamService(retrieveTeamService,
                                               playerService,
                                               retrieveTournamentsService,
                                               teamsRepository)

  // FIXME: maybe a field could be added if it's an "active" player or not, in the FoundTeam
  @Test
  fun `swap completed, with new players fantaPoints to ZERO, with removed and already present players still present in team response`() {

    val playerToRemoveIds = setOf(AN_OLD_PLAYER_ID, ANOTHER_OLD_PLAYER_ID)
    val playersToRemoveDto = PlayersToRemoveDto(playerIds = playerToRemoveIds, endingTournamentId = A_TOURNAMENT_ID)
    val playerToAddIds = setOf(A_NEW_PLAYER_ID, ANOTHER_NEW_PLAYER_ID)
    val playersToAddDto = PlayersToAddDto(playerIds = playerToAddIds, startingTournamentId = ANOTHER_TOURNAMENT_ID)
    val playersToSwap = PlayersToSwapDto(remove = playersToRemoveDto, add = playersToAddDto)

    val oldTeamDto = TeamDto(players = listOf(TeamPlayerDto(fullName = A_PLAYER_FULL_NAME, fantaPoints = 22.0),
                                              TeamPlayerDto(fullName = AN_OLD_PLAYER_FULL_NAME, fantaPoints = 10.0),
                                              TeamPlayerDto(fullName = ANOTHER_OLD_PLAYER_FULL_NAME, fantaPoints = 8.0)),
                             totalScore = 40.0)
    val aPlayer = aDomainPlayer(id = A_PLAYER_ID, fullName = A_PLAYER_FULL_NAME)
    val anOldPlayer = aDomainPlayer(id = AN_OLD_PLAYER_ID, fullName = AN_OLD_PLAYER_FULL_NAME)
    val anotherOldPlayer = aDomainPlayer(id = ANOTHER_OLD_PLAYER_ID, fullName = ANOTHER_OLD_PLAYER_FULL_NAME)
    val aNewPlayer = aDomainPlayer(id = A_NEW_PLAYER_ID, fullName = A_NEW_PLAYER_FULL_NAME)
    val anotherNewPlayer = aDomainPlayer(id = ANOTHER_NEW_PLAYER_ID, fullName = ANOTHER_NEW_PLAYER_FULL_NAME)
    val allPlayers = setOf(aPlayer, anOldPlayer, anotherOldPlayer, aNewPlayer, anotherNewPlayer)
    val endingTournament = aTournament(id = A_TOURNAMENT_ID)
    val startingTournament = aTournament(id = ANOTHER_TOURNAMENT_ID)
    val aTournament = aTournament(id = A_THIRD_TOURNAMENT_ID)
    val allTournaments = listOf(endingTournament, startingTournament, aTournament)
    val playersToRemove = setOf(AN_OLD_PLAYER_ID, ANOTHER_OLD_PLAYER_ID)
    val playersToAdd = setOf(A_NEW_PLAYER_ID, ANOTHER_NEW_PLAYER_ID)
    val swapCommand = SwapCommand(A_TEAM_ID, playersToRemove, playersToAdd, A_TOURNAMENT_ID, ANOTHER_TOURNAMENT_ID)

    val newTeamDto = TeamDto(players = listOf(TeamPlayerDto(fullName = A_PLAYER_FULL_NAME, fantaPoints = 22.0),
                                              TeamPlayerDto(fullName = AN_OLD_PLAYER_FULL_NAME, fantaPoints = 10.0),
                                              TeamPlayerDto(fullName = ANOTHER_OLD_PLAYER_FULL_NAME, fantaPoints = 8.0),
                                              TeamPlayerDto(fullName = A_NEW_PLAYER_FULL_NAME, fantaPoints = 0.0),
                                              TeamPlayerDto(fullName = ANOTHER_NEW_PLAYER_FULL_NAME, fantaPoints = 0.0)),
                             totalScore = 40.0)
    val expected = FoundTeam(newTeamDto)

    every { retrieveTeamService.getTeam(A_TEAM_ID) } returns FoundTeam(oldTeamDto)
    every { playerService.allPlayers() } returns allPlayers
    every { retrieveTournamentsService.retrieveAll() } returns allTournaments
    every { teamsRepository.swapPlayers(swapCommand) } returns expected

    assertThat(service.swap(A_TEAM_ID, playersToSwap)).isEqualTo(expected)
  }

  @Test
  fun `team not found`() {

    val expected = TeamIdNotFoundTeam

    every { retrieveTeamService.getTeam(123) } returns TeamIdNotFoundTeam

    assertThat(service.swap(123, ANY_PLAYER_TO_SWAP)).isEqualTo(expected)

    verify { playerService wasNot called }
    verify { retrieveTournamentsService wasNot called }
    verify(exactly = 0) { teamsRepository.swapPlayers(any()) }
  }

  @Test
  fun `players not found, empty set returned by the repository`() {

    val playerToRemoveIds = setOf(AN_OLD_PLAYER_ID, ANOTHER_OLD_PLAYER_ID)
    val playersToRemoveDto = PlayersToRemoveDto(playerIds = playerToRemoveIds, endingTournamentId = A_TOURNAMENT_ID)
    val playerToAddIds = setOf(A_NEW_PLAYER_ID, ANOTHER_NEW_PLAYER_ID)
    val playersToAddDto = PlayersToAddDto(playerIds = playerToAddIds, startingTournamentId = ANOTHER_TOURNAMENT_ID)
    val playersToSwap = PlayersToSwapDto(remove = playersToRemoveDto, add = playersToAddDto)

    val oldTeamDto = TeamDto(players = listOf(TeamPlayerDto(fullName = A_PLAYER_FULL_NAME, fantaPoints = 22.0),
                                              TeamPlayerDto(fullName = AN_OLD_PLAYER_FULL_NAME, fantaPoints = 10.0),
                                              TeamPlayerDto(fullName = ANOTHER_OLD_PLAYER_FULL_NAME, fantaPoints = 8.0)),
                             totalScore = 40.0)

    val expected = ErrorTeam

    every { retrieveTeamService.getTeam(A_TEAM_ID) } returns FoundTeam(oldTeamDto)
    every { playerService.allPlayers() } returns emptySet()

    assertThat(service.swap(A_TEAM_ID, playersToSwap)).isEqualTo(expected)

    verify { retrieveTournamentsService wasNot called }
    verify(exactly = 0) { teamsRepository.swapPlayers(any()) }
  }

  @Test
  fun `one or more players not found in the set returned by the repository`() {

    val playerToRemoveIds = setOf(AN_OLD_PLAYER_ID, ANOTHER_OLD_PLAYER_ID)
    val playersToRemoveDto = PlayersToRemoveDto(playerIds = playerToRemoveIds, endingTournamentId = A_TOURNAMENT_ID)
    val playerToAddIds = setOf(A_NEW_PLAYER_ID, ANOTHER_NEW_PLAYER_ID)
    val playersToAddDto = PlayersToAddDto(playerIds = playerToAddIds, startingTournamentId = ANOTHER_TOURNAMENT_ID)
    val playersToSwap = PlayersToSwapDto(remove = playersToRemoveDto, add = playersToAddDto)

    val oldTeamDto = TeamDto(players = listOf(TeamPlayerDto(fullName = A_PLAYER_FULL_NAME, fantaPoints = 22.0),
                                              TeamPlayerDto(fullName = AN_OLD_PLAYER_FULL_NAME, fantaPoints = 10.0),
                                              TeamPlayerDto(fullName = ANOTHER_OLD_PLAYER_FULL_NAME, fantaPoints = 8.0)),
                             totalScore = 40.0)
    val anOldPlayer = aDomainPlayer(id = AN_OLD_PLAYER_ID, fullName = AN_OLD_PLAYER_FULL_NAME)
    val anotherOldPlayer = aDomainPlayer(id = ANOTHER_OLD_PLAYER_ID, fullName = ANOTHER_OLD_PLAYER_FULL_NAME)
    val aNewPlayer = aDomainPlayer(id = A_NEW_PLAYER_ID, fullName = A_NEW_PLAYER_FULL_NAME)
    val notAllPlayersFound = setOf(A_PLAYER, anOldPlayer, anotherOldPlayer, aNewPlayer)

    val expected = ErrorTeam

    every { retrieveTeamService.getTeam(A_TEAM_ID) } returns FoundTeam(oldTeamDto)
    every { playerService.allPlayers() } returns notAllPlayersFound

    assertThat(service.swap(A_TEAM_ID, playersToSwap)).isEqualTo(expected)

    verify { retrieveTournamentsService wasNot called }
    verify(exactly = 0) { teamsRepository.swapPlayers(any()) }
  }

  @Test
  fun `one or more tournaments not found`() {

    val playerToRemoveIds = setOf(AN_OLD_PLAYER_ID)
    val playersToRemoveDto = PlayersToRemoveDto(playerIds = playerToRemoveIds, endingTournamentId = A_TOURNAMENT_ID)
    val playerToAddIds = setOf(A_NEW_PLAYER_ID)
    val playersToAddDto = PlayersToAddDto(playerIds = playerToAddIds, startingTournamentId = ANOTHER_TOURNAMENT_ID)
    val playersToSwap = PlayersToSwapDto(remove = playersToRemoveDto, add = playersToAddDto)

    val oldTeamDto = TeamDto(players = listOf(TeamPlayerDto(fullName = A_PLAYER_FULL_NAME, fantaPoints = 22.0),
                                              TeamPlayerDto(fullName = AN_OLD_PLAYER_FULL_NAME, fantaPoints = 10.0)),
                             totalScore = 40.0)
    val aPlayer = aDomainPlayer(id = A_PLAYER_ID, fullName = A_PLAYER_FULL_NAME)
    val anOldPlayer = aDomainPlayer(id = AN_OLD_PLAYER_ID, fullName = AN_OLD_PLAYER_FULL_NAME)
    val aNewPlayer = aDomainPlayer(id = A_NEW_PLAYER_ID, fullName = A_NEW_PLAYER_FULL_NAME)
    val allPlayers = setOf(aPlayer, anOldPlayer, aNewPlayer)
    val endingTournament = aTournament(id = A_TOURNAMENT_ID)
    val notAllTournamentsFound = listOf(endingTournament, A_TOURNAMENT)

    val expected = ErrorTeam

    every { retrieveTeamService.getTeam(A_TEAM_ID) } returns FoundTeam(oldTeamDto)
    every { playerService.allPlayers() } returns allPlayers
    every { retrieveTournamentsService.retrieveAll() } returns notAllTournamentsFound

    assertThat(service.swap(A_TEAM_ID, playersToSwap)).isEqualTo(expected)

    verify(exactly = 0) { teamsRepository.swapPlayers(any()) }
  }

  @Test
  fun `error on swap player`() {

    val playerToRemoveIds = setOf(AN_OLD_PLAYER_ID)
    val playersToRemoveDto = PlayersToRemoveDto(playerIds = playerToRemoveIds, endingTournamentId = A_TOURNAMENT_ID)
    val playerToAddIds = setOf(A_NEW_PLAYER_ID)
    val playersToAddDto = PlayersToAddDto(playerIds = playerToAddIds, startingTournamentId = ANOTHER_TOURNAMENT_ID)
    val playersToSwap = PlayersToSwapDto(remove = playersToRemoveDto, add = playersToAddDto)

    val oldTeamDto = TeamDto(players = listOf(TeamPlayerDto(fullName = A_PLAYER_FULL_NAME, fantaPoints = 22.0),
                                              TeamPlayerDto(fullName = AN_OLD_PLAYER_FULL_NAME, fantaPoints = 10.0)),
                             totalScore = 32.0)
    val aPlayer = aDomainPlayer(id = A_PLAYER_ID, fullName = A_PLAYER_FULL_NAME)
    val anOldPlayer = aDomainPlayer(id = AN_OLD_PLAYER_ID, fullName = AN_OLD_PLAYER_FULL_NAME)
    val aNewPlayer = aDomainPlayer(id = A_NEW_PLAYER_ID, fullName = A_NEW_PLAYER_FULL_NAME)
    val allPlayers = setOf(aPlayer, anOldPlayer, aNewPlayer)
    val endingTournament = aTournament(id = A_TOURNAMENT_ID)
    val startingTournament = aTournament(id = ANOTHER_TOURNAMENT_ID)
    val allTournaments = listOf(endingTournament, startingTournament)
    val playersToRemove = setOf(AN_OLD_PLAYER_ID)
    val playersToAdd = setOf(A_NEW_PLAYER_ID)
    val swapCommand = SwapCommand(A_TEAM_ID, playersToRemove, playersToAdd, A_TOURNAMENT_ID, ANOTHER_TOURNAMENT_ID)

    val expected = ErrorTeam

    every { retrieveTeamService.getTeam(A_TEAM_ID) } returns FoundTeam(oldTeamDto)
    every { playerService.allPlayers() } returns allPlayers
    every { retrieveTournamentsService.retrieveAll() } returns allTournaments
    every { teamsRepository.swapPlayers(swapCommand) } returns ErrorTeam

    assertThat(service.swap(A_TEAM_ID, playersToSwap)).isEqualTo(expected)
  }

  companion object {

    private const val A_PLAYER_ID = "A_PLAYER_ID"
    private const val AN_OLD_PLAYER_ID = "AN_OLD_PLAYER_ID"
    private const val ANOTHER_OLD_PLAYER_ID = "ANOTHER_OLD_PLAYER_ID"
    private const val A_NEW_PLAYER_ID = "A_NEW_PLAYER_ID"
    private const val ANOTHER_NEW_PLAYER_ID = "ANOTHER_NEW_PLAYER_ID"
    private const val A_PLAYER_FULL_NAME = "A_PLAYER_FULL_NAME"
    private const val AN_OLD_PLAYER_FULL_NAME = "AN_OLD_PLAYER_FULL_NAME"
    private const val ANOTHER_OLD_PLAYER_FULL_NAME = "ANOTHER_OLD_PLAYER_FULL_NAME"
    private const val A_NEW_PLAYER_FULL_NAME = "A_NEW_PLAYER_FULL_NAME"
    private const val ANOTHER_NEW_PLAYER_FULL_NAME = "ANOTHER_NEW_PLAYER_FULL_NAME"
    private const val A_TEAM_ID = 1
    private const val A_TOURNAMENT_ID = 1
    private const val ANOTHER_TOURNAMENT_ID = 2
    private const val A_THIRD_TOURNAMENT_ID = 3

    private val ANY_PLAYER_TO_SWAP = PlayersToSwapDto()
    private val A_PLAYER = aDomainPlayer(id = A_PLAYER_ID, fullName = A_PLAYER_FULL_NAME)
    private val A_TOURNAMENT = aTournament(id = A_THIRD_TOURNAMENT_ID)
  }
}