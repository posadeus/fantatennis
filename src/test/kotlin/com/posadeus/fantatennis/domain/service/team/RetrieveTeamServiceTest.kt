package com.posadeus.fantatennis.domain.service.team

import com.posadeus.fantatennis.controller.model.team.PlayerPointsDto
import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.domain.infrastructure.*
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.DomainTeam.FoundDomainTeam
import com.posadeus.fantatennis.domain.model.DomainTeam.NotFoundDomainTeam
import com.posadeus.fantatennis.domain.model.FantaTournament.InvalidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import com.posadeus.fantatennis.domain.model.PlayersPoints.FoundPlayersPoints
import com.posadeus.fantatennis.domain.model.PlayersPoints.FoundPlayersPoints.PlayerPoints
import com.posadeus.fantatennis.domain.model.PlayersPoints.InternalErrorPlayersPoints
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RetrieveTeamServiceTest {

  private val retrieveFantaTeamRepository: RetrieveFantaTeamRepository = mockk()
  private val retrieveFantaTournamentsRepository: RetrieveFantaTournamentsRepository = mockk()
  private val retrievePlayersPointsRepository: RetrievePlayersPointsRepository = mockk()

  private val service = RetrieveTeamService(retrieveFantaTeamRepository,
                                            retrieveFantaTournamentsRepository,
                                            retrievePlayersPointsRepository)

  @Test
  fun `team not found when retrieveByTeamId returns NotFoundDomainTeam`() {

    every { retrieveFantaTeamRepository.retrieveByTeamId(A_TEAM_ID) } returns NotFoundDomainTeam(A_TEAM_ID)

    assertThat(service.retrieve(A_TEAM_ID)).isEqualTo(TeamIdNotFoundTeam)
  }

  @Test
  fun `error when retrieveBy returns InvalidFantaTournament`() {

    val foundDomainTeam = FoundDomainTeam(teamId = A_TEAM_ID,
                                          ownerId = AN_OWNER_ID,
                                          fantaTournamentId = A_FANTA_TOURNAMENT_ID,
                                          players = emptyMap())

    every { retrieveFantaTeamRepository.retrieveByTeamId(A_TEAM_ID) } returns foundDomainTeam
    every { retrieveFantaTournamentsRepository.retrieveBy(A_FANTA_TOURNAMENT_ID) } returns InvalidFantaTournament

    assertThat(service.retrieve(A_TEAM_ID)).isEqualTo(ErrorTeam)
  }

  @Test
  fun `error when retrieveByYear returns InternalErrorPlayersPoints`() {

    val foundDomainTeam = FoundDomainTeam(teamId = A_TEAM_ID,
                                          ownerId = AN_OWNER_ID,
                                          fantaTournamentId = A_FANTA_TOURNAMENT_ID,
                                          players = emptyMap())
    val validFantaTournament = ValidFantaTournament(id = A_FANTA_TOURNAMENT_ID,
                                                    startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                                    endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                                    tournamentYear = A_TOURNAMENT_YEAR)

    every { retrieveFantaTeamRepository.retrieveByTeamId(A_TEAM_ID) } returns foundDomainTeam
    every { retrieveFantaTournamentsRepository.retrieveBy(A_FANTA_TOURNAMENT_ID) } returns validFantaTournament
    every { retrievePlayersPointsRepository.retrieveByYear(A_TOURNAMENT_YEAR) } returns InternalErrorPlayersPoints

    assertThat(service.retrieve(A_TEAM_ID)).isEqualTo(ErrorTeam)
  }

  @Test
  fun `team found without players returns empty team with zero score`() {

    val foundDomainTeam = FoundDomainTeam(teamId = A_TEAM_ID,
                                          ownerId = AN_OWNER_ID,
                                          fantaTournamentId = A_FANTA_TOURNAMENT_ID,
                                          players = emptyMap())
    val validFantaTournament = ValidFantaTournament(id = A_FANTA_TOURNAMENT_ID,
                                                    startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                                    endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                                    tournamentYear = A_TOURNAMENT_YEAR)
    val foundPlayersPoints = FoundPlayersPoints(listOf(PlayerPoints(playerId = A_PLAYER_ID,
                                                                    playerName = A_PLAYER_NAME,
                                                                    pointsByTournament = mapOf(100 to 10.0),
                                                                    totalPoints = 10.0)))

    val expected = FoundTeam(TeamDto(owner = AN_OWNER_ID,
                                     players = emptyList(),
                                     totalScore = 0.0))

    every { retrieveFantaTeamRepository.retrieveByTeamId(A_TEAM_ID) } returns foundDomainTeam
    every { retrieveFantaTournamentsRepository.retrieveBy(A_FANTA_TOURNAMENT_ID) } returns validFantaTournament
    every { retrievePlayersPointsRepository.retrieveByYear(A_TOURNAMENT_YEAR) } returns foundPlayersPoints

    assertThat(service.retrieve(A_TEAM_ID)).isEqualTo(expected)
  }

  @Test
  fun `team found aggregates and sorts players filtering points outside the fantaTournament range`() {

    val foundDomainTeam = FoundDomainTeam(teamId = A_TEAM_ID,
                                          ownerId = AN_OWNER_ID,
                                          fantaTournamentId = A_FANTA_TOURNAMENT_ID,
                                          players = mapOf(A_PLAYER_ID to setOf(TournamentRange(start = A_STARTING_TOURNAMENT_ID)),
                                                          ANOTHER_PLAYER_ID to setOf(TournamentRange(start = A_STARTING_TOURNAMENT_ID))))
    val validFantaTournament = ValidFantaTournament(id = A_FANTA_TOURNAMENT_ID,
                                                    startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                                    endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                                    tournamentYear = A_TOURNAMENT_YEAR)
    val foundPlayersPoints = FoundPlayersPoints(listOf(PlayerPoints(playerId = A_PLAYER_ID,
                                                                    playerName = A_PLAYER_NAME,
                                                                    pointsByTournament = mapOf(95 to 999.0,
                                                                                               100 to 10.0,
                                                                                               105 to 20.0,
                                                                                               115 to 999.0),
                                                                    totalPoints = 0.0),
                                                       PlayerPoints(playerId = ANOTHER_PLAYER_ID,
                                                                    playerName = ANOTHER_PLAYER_NAME,
                                                                    pointsByTournament = mapOf(102 to 15.0,
                                                                                               108 to 25.0,
                                                                                               115 to 999.0),
                                                                    totalPoints = 0.0)))

    val expected = FoundTeam(TeamDto(owner = AN_OWNER_ID,
                                     players = listOf(PlayerPointsDto(fullName = ANOTHER_PLAYER_NAME, fantaPoints = 40.0),
                                                      PlayerPointsDto(fullName = A_PLAYER_NAME, fantaPoints = 30.0)),
                                     totalScore = 70.0))

    every { retrieveFantaTeamRepository.retrieveByTeamId(A_TEAM_ID) } returns foundDomainTeam
    every { retrieveFantaTournamentsRepository.retrieveBy(A_FANTA_TOURNAMENT_ID) } returns validFantaTournament
    every { retrievePlayersPointsRepository.retrieveByYear(A_TOURNAMENT_YEAR) } returns foundPlayersPoints

    assertThat(service.retrieve(A_TEAM_ID)).isEqualTo(expected)
  }

  @Test
  fun `team found applies per-player range filter when endingTournamentId is set`() {

    val foundDomainTeam = FoundDomainTeam(teamId = A_TEAM_ID,
                                          ownerId = AN_OWNER_ID,
                                          fantaTournamentId = A_FANTA_TOURNAMENT_ID,
                                          players = mapOf(A_PLAYER_ID to setOf(TournamentRange(start = A_STARTING_TOURNAMENT_ID,
                                                                                               end = A_PLAYER_ENDING_TOURNAMENT_ID))))
    val validFantaTournament = ValidFantaTournament(id = A_FANTA_TOURNAMENT_ID,
                                                    startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                                    endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                                    tournamentYear = A_TOURNAMENT_YEAR)
    val foundPlayersPoints = FoundPlayersPoints(listOf(PlayerPoints(playerId = A_PLAYER_ID,
                                                                    playerName = A_PLAYER_NAME,
                                                                    pointsByTournament = mapOf(100 to 5.0,
                                                                                               103 to 10.0,
                                                                                               107 to 100.0,
                                                                                               109 to 200.0),
                                                                    totalPoints = 0.0)))

    val expected = FoundTeam(TeamDto(owner = AN_OWNER_ID,
                                     players = listOf(PlayerPointsDto(fullName = A_PLAYER_NAME, fantaPoints = 15.0)),
                                     totalScore = 15.0))

    every { retrieveFantaTeamRepository.retrieveByTeamId(A_TEAM_ID) } returns foundDomainTeam
    every { retrieveFantaTournamentsRepository.retrieveBy(A_FANTA_TOURNAMENT_ID) } returns validFantaTournament
    every { retrievePlayersPointsRepository.retrieveByYear(A_TOURNAMENT_YEAR) } returns foundPlayersPoints

    assertThat(service.retrieve(A_TEAM_ID)).isEqualTo(expected)
  }

  @Test
  fun `team found excludes points before per-player startingTournamentId when endingTournamentId is null`() {

    val foundDomainTeam = FoundDomainTeam(teamId = A_TEAM_ID,
                                          ownerId = AN_OWNER_ID,
                                          fantaTournamentId = A_FANTA_TOURNAMENT_ID,
                                          players = mapOf(A_PLAYER_ID to setOf(TournamentRange(start = A_PLAYER_STARTING_TOURNAMENT_ID))))
    val validFantaTournament = ValidFantaTournament(id = A_FANTA_TOURNAMENT_ID,
                                                    startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                                    endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                                    tournamentYear = A_TOURNAMENT_YEAR)
    val foundPlayersPoints = FoundPlayersPoints(listOf(PlayerPoints(playerId = A_PLAYER_ID,
                                                                    playerName = A_PLAYER_NAME,
                                                                    pointsByTournament = mapOf(100 to 50.0,
                                                                                               103 to 30.0,
                                                                                               105 to 10.0,
                                                                                               108 to 20.0),
                                                                    totalPoints = 0.0)))

    val expected = FoundTeam(TeamDto(owner = AN_OWNER_ID,
                                     players = listOf(PlayerPointsDto(fullName = A_PLAYER_NAME, fantaPoints = 30.0)),
                                     totalScore = 30.0))

    every { retrieveFantaTeamRepository.retrieveByTeamId(A_TEAM_ID) } returns foundDomainTeam
    every { retrieveFantaTournamentsRepository.retrieveBy(A_FANTA_TOURNAMENT_ID) } returns validFantaTournament
    every { retrievePlayersPointsRepository.retrieveByYear(A_TOURNAMENT_YEAR) } returns foundPlayersPoints

    assertThat(service.retrieve(A_TEAM_ID)).isEqualTo(expected)
  }

  @Test
  fun `team found with single player without points appears with zero score`() {

    val foundDomainTeam = FoundDomainTeam(teamId = A_TEAM_ID,
                                          ownerId = AN_OWNER_ID,
                                          fantaTournamentId = A_FANTA_TOURNAMENT_ID,
                                          players = mapOf(A_PLAYER_ID to setOf(TournamentRange(start = A_STARTING_TOURNAMENT_ID))))
    val validFantaTournament = ValidFantaTournament(id = A_FANTA_TOURNAMENT_ID,
                                                    startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                                    endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                                    tournamentYear = A_TOURNAMENT_YEAR)
    val foundPlayersPoints = FoundPlayersPoints(listOf(PlayerPoints(playerId = A_PLAYER_ID,
                                                                    playerName = A_PLAYER_NAME,
                                                                    pointsByTournament = emptyMap(),
                                                                    totalPoints = 0.0)))

    val expected = FoundTeam(TeamDto(owner = AN_OWNER_ID,
                                     players = listOf(PlayerPointsDto(fullName = A_PLAYER_NAME, fantaPoints = 0.0)),
                                     totalScore = 0.0))

    every { retrieveFantaTeamRepository.retrieveByTeamId(A_TEAM_ID) } returns foundDomainTeam
    every { retrieveFantaTournamentsRepository.retrieveBy(A_FANTA_TOURNAMENT_ID) } returns validFantaTournament
    every { retrievePlayersPointsRepository.retrieveByYear(A_TOURNAMENT_YEAR) } returns foundPlayersPoints

    assertThat(service.retrieve(A_TEAM_ID)).isEqualTo(expected)
  }

  companion object {

    private const val A_TEAM_ID = 1
    private const val A_FANTA_TOURNAMENT_ID = 42
    private const val A_STARTING_TOURNAMENT_ID = 100
    private const val AN_ENDING_TOURNAMENT_ID = 110
    private const val A_PLAYER_ENDING_TOURNAMENT_ID = 104
    private const val A_PLAYER_STARTING_TOURNAMENT_ID = 105
    private const val A_TOURNAMENT_YEAR = 2026
    private const val AN_OWNER_ID = "AN_OWNER_ID"
    private const val A_PLAYER_ID = "A_PLAYER_ID"
    private const val A_PLAYER_NAME = "A_PLAYER_NAME"
    private const val ANOTHER_PLAYER_ID = "ANOTHER_PLAYER_ID"
    private const val ANOTHER_PLAYER_NAME = "ANOTHER_PLAYER_NAME"
  }
}
