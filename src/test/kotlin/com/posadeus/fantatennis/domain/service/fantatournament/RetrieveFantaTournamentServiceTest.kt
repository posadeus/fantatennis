package com.posadeus.fantatennis.domain.service.fantatournament

import com.posadeus.fantatennis.controller.model.fantatournament.FantaTournamentDto
import com.posadeus.fantatennis.controller.model.team.PlayerPointsDto
import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.domain.infrastructure.*
import com.posadeus.fantatennis.domain.model.DomainTeam.NotFoundDomainTeam
import com.posadeus.fantatennis.domain.model.FantaTournament.InvalidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournamentResults.*
import com.posadeus.fantatennis.domain.model.PlayersPoints.FoundPlayersPoints
import com.posadeus.fantatennis.domain.model.PlayersPoints.InternalErrorPlayersPoints
import com.posadeus.fantatennis.domain.model.Teams
import com.posadeus.fantatennis.domain.model.TestDomainTeam.aDomainTeam
import com.posadeus.fantatennis.domain.model.TestPlayerPoints.aPlayerPoints
import com.posadeus.fantatennis.domain.model.TournamentRange
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RetrieveFantaTournamentServiceTest {

  private val retrieveFantaTournamentsRepository: RetrieveFantaTournamentsRepository = mockk()
  private val retrievePlayersPointsRepository: RetrievePlayersPointsRepository = mockk()
  private val retrieveFantaTeamRepository: RetrieveFantaTeamRepository = mockk()

  private val service = RetrieveFantaTournamentService(retrieveFantaTournamentsRepository,
                                                       retrievePlayersPointsRepository,
                                                       retrieveFantaTeamRepository)

  @Test
  fun `retrieve fails due to missing fanta tournament`() {

    val expected = NotFoundFantaTournamentId

    every { retrieveFantaTournamentsRepository.retrieveBy(A_TOURNAMENT_ID) } returns InvalidFantaTournament

    assertThat(service.retrieve(A_TOURNAMENT_ID)).isEqualTo(expected)

    verify { retrievePlayersPointsRepository wasNot called }
    verify { retrieveFantaTeamRepository wasNot called }
  }

  @Test
  fun `retrieve fails due to error on playersPoints repository`() {

    val fantaTournament = ValidFantaTournament(id = A_TOURNAMENT_ID,
                                               startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                               endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                               tournamentYear = A_TOURNAMENT_YEAR)

    val expected = ErrorFantaTournamentResults

    every { retrieveFantaTournamentsRepository.retrieveBy(A_TOURNAMENT_ID) } returns fantaTournament
    every { retrievePlayersPointsRepository.retrieveByYear(A_TOURNAMENT_YEAR) } returns InternalErrorPlayersPoints

    assertThat(service.retrieve(A_TOURNAMENT_ID)).isEqualTo(expected)

    verify { retrieveFantaTeamRepository wasNot called }
  }

  @Test
  fun `retrieve fails due to not all team found in FantaTeamRepository`() {

    val fantaTournament = ValidFantaTournament(id = A_TOURNAMENT_ID,
                                               startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                               endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                               tournamentYear = A_TOURNAMENT_YEAR)
    val playerPoints = aPlayerPoints()
    val foundPlayersPoints = FoundPlayersPoints(listOf(playerPoints))
    val teams = Teams(listOf(aDomainTeam(), NotFoundDomainTeam(A_TEAM_ID)))

    val expected = ErrorFantaTournamentResults

    every { retrieveFantaTournamentsRepository.retrieveBy(A_TOURNAMENT_ID) } returns fantaTournament
    every { retrievePlayersPointsRepository.retrieveByYear(A_TOURNAMENT_YEAR) } returns foundPlayersPoints
    every { retrieveFantaTeamRepository.retrieveByFantaTournamentId(A_TOURNAMENT_ID) } returns teams

    assertThat(service.retrieve(A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  @Test
  fun `retrieve fails due to no match between teams and playersPoints`() {

    val fantaTournament = ValidFantaTournament(id = A_TOURNAMENT_ID,
                                               startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                               endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                               tournamentYear = A_TOURNAMENT_YEAR)
    val playerPoints = aPlayerPoints(playerId = A_PLAYER_ID)
    val foundPlayersPoints = FoundPlayersPoints(listOf(playerPoints))
    val teams = Teams(listOf(aDomainTeam(players = mapOf(ANOTHER_PLAYER_ID to setOf(TournamentRange(A_STARTING_TOURNAMENT_ID, null))))))

    val expected = ErrorFantaTournamentResults

    every { retrieveFantaTournamentsRepository.retrieveBy(A_TOURNAMENT_ID) } returns fantaTournament
    every { retrievePlayersPointsRepository.retrieveByYear(A_TOURNAMENT_YEAR) } returns foundPlayersPoints
    every { retrieveFantaTeamRepository.retrieveByFantaTournamentId(A_TOURNAMENT_ID) } returns teams

    assertThat(service.retrieve(A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  @Test
  fun `retrieve returns empty teams due to not found in FantaTeamRepository`() {

    val fantaTournament = ValidFantaTournament(id = A_TOURNAMENT_ID,
                                               startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                               endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                               tournamentYear = A_TOURNAMENT_YEAR)
    val playerPoints = aPlayerPoints()
    val foundPlayersPoints = FoundPlayersPoints(listOf(playerPoints))
    val teams = Teams(emptyList())

    val expected = FoundFantaTournamentResults(tournament = FantaTournamentDto(teams = emptyList()))

    every { retrieveFantaTournamentsRepository.retrieveBy(A_TOURNAMENT_ID) } returns fantaTournament
    every { retrievePlayersPointsRepository.retrieveByYear(A_TOURNAMENT_YEAR) } returns foundPlayersPoints
    every { retrieveFantaTeamRepository.retrieveByFantaTournamentId(A_TOURNAMENT_ID) } returns teams

    assertThat(service.retrieve(A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  @Test
  fun `retrieve returns only the players and points inside the tournament range, ordered by fantaPoints`() {

    val fantaTournament = ValidFantaTournament(id = A_TOURNAMENT_ID,
                                               startingTournamentId = 1,
                                               endingTournamentId = 2,
                                               tournamentYear = A_TOURNAMENT_YEAR)
    val playerPoints1 = aPlayerPoints(playerId = A_PLAYER_ID,
                                      playerName = A_PLAYER_NAME,
                                      pointsByTournament = mapOf(1 to 3.0, 2 to 6.0, 3 to 10.0))
    val playerPoints2 = aPlayerPoints(playerId = ANOTHER_PLAYER_ID,
                                      playerName = ANOTHER_PLAYER_NAME,
                                      pointsByTournament = mapOf(1 to 10.0, 4 to 7.0))
    val playerPoints3 = aPlayerPoints(playerId = A_THIRD_PLAYER_ID,
                                      playerName = A_THIRD_PLAYER_NAME,
                                      pointsByTournament = mapOf(3 to 5.0, 4 to 1.0))
    val playerPoints4 = aPlayerPoints(playerId = A_FOURTH_PLAYER_ID,
                                      playerName = A_FOURTH_PLAYER_NAME,
                                      pointsByTournament = mapOf(1 to 4.0, 2 to 0.0))
    val foundPlayersPoints = FoundPlayersPoints(listOf(playerPoints1, playerPoints2, playerPoints3, playerPoints4))
    val domainTeam1 = aDomainTeam(ownerId = AN_OWNER_ID,
                                  players = mapOf(A_PLAYER_ID to setOf(TournamentRange(1, 1)),
                                                  ANOTHER_PLAYER_ID to setOf(TournamentRange(2, 4))))
    val domainTeam2 = aDomainTeam(ownerId = ANOTHER_OWNER_ID,
                                  players = mapOf(A_THIRD_PLAYER_ID to setOf(TournamentRange(1, null)),
                                                  A_FOURTH_PLAYER_ID to setOf(TournamentRange(1, null))))
    val teams = Teams(listOf(domainTeam1, domainTeam2))

    val player1 = PlayerPointsDto(fullName = A_PLAYER_NAME, fantaPoints = 3.0)
    val player2 = PlayerPointsDto(fullName = ANOTHER_PLAYER_NAME, fantaPoints = 0.0)
    val player3 = PlayerPointsDto(fullName = A_THIRD_PLAYER_NAME, fantaPoints = 0.0)
    val player4 = PlayerPointsDto(fullName = A_FOURTH_PLAYER_NAME, fantaPoints = 4.0)
    val teamDto1 = TeamDto(owner = AN_OWNER_ID, players = listOf(player1, player2), totalScore = 3.0)
    val teamDto2 = TeamDto(owner = ANOTHER_OWNER_ID, players = listOf(player4, player3), totalScore = 4.0)
    val expected = FoundFantaTournamentResults(tournament = FantaTournamentDto(listOf(teamDto2, teamDto1)))

    every { retrieveFantaTournamentsRepository.retrieveBy(A_TOURNAMENT_ID) } returns fantaTournament
    every { retrievePlayersPointsRepository.retrieveByYear(A_TOURNAMENT_YEAR) } returns foundPlayersPoints
    every { retrieveFantaTeamRepository.retrieveByFantaTournamentId(A_TOURNAMENT_ID) } returns teams

    assertThat(service.retrieve(A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  @Test
  fun `retrieve includes teams without players with zero score`() {

    val fantaTournament = ValidFantaTournament(id = A_TOURNAMENT_ID,
                                               startingTournamentId = 1,
                                               endingTournamentId = 2,
                                               tournamentYear = A_TOURNAMENT_YEAR)
    val playerPoints = aPlayerPoints(playerId = A_PLAYER_ID,
                                     playerName = A_PLAYER_NAME,
                                     pointsByTournament = mapOf(1 to 3.0, 2 to 6.0))
    val foundPlayersPoints = FoundPlayersPoints(listOf(playerPoints))
    val domainTeam1 = aDomainTeam(ownerId = AN_OWNER_ID,
                                  players = mapOf(A_PLAYER_ID to setOf(TournamentRange(1, null))))
    val domainTeam2 = aDomainTeam(ownerId = ANOTHER_OWNER_ID,
                                  players = emptyMap())
    val teams = Teams(listOf(domainTeam1, domainTeam2))

    val teamDto1 = TeamDto(owner = AN_OWNER_ID,
                           players = listOf(PlayerPointsDto(fullName = A_PLAYER_NAME, fantaPoints = 9.0)),
                           totalScore = 9.0)
    val teamDto2 = TeamDto(owner = ANOTHER_OWNER_ID, players = emptyList(), totalScore = 0.0)
    val expected = FoundFantaTournamentResults(tournament = FantaTournamentDto(listOf(teamDto1, teamDto2)))

    every { retrieveFantaTournamentsRepository.retrieveBy(A_TOURNAMENT_ID) } returns fantaTournament
    every { retrievePlayersPointsRepository.retrieveByYear(A_TOURNAMENT_YEAR) } returns foundPlayersPoints
    every { retrieveFantaTeamRepository.retrieveByFantaTournamentId(A_TOURNAMENT_ID) } returns teams

    assertThat(service.retrieve(A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  @Test
  fun `retrieve returns the sum of points for players with a hole in tournaments`() {

    val fantaTournament = ValidFantaTournament(id = A_TOURNAMENT_ID,
                                               startingTournamentId = 1,
                                               endingTournamentId = 6,
                                               tournamentYear = A_TOURNAMENT_YEAR)
    val playerPoints1 = aPlayerPoints(playerId = A_PLAYER_ID,
                                      playerName = A_PLAYER_NAME,
                                      pointsByTournament = mapOf(1 to 3.0, 2 to 6.0, 3 to 10.0, 4 to 6.0, 5 to 3.0, 6 to 2.0))
    val playerPoints2 = aPlayerPoints(playerId = ANOTHER_PLAYER_ID,
                                      playerName = ANOTHER_PLAYER_NAME,
                                      pointsByTournament = mapOf(1 to 10.0, 4 to 7.0, 5 to 1.0, 6 to 10.0))
    val foundPlayersPoints = FoundPlayersPoints(listOf(playerPoints1, playerPoints2))
    val domainTeam1 = aDomainTeam(ownerId = AN_OWNER_ID,
                                  players = mapOf(A_PLAYER_ID to setOf(TournamentRange(1, 3), TournamentRange(5, null)),
                                  ANOTHER_PLAYER_ID to setOf(TournamentRange(2, 4))))
    val teams = Teams(listOf(domainTeam1))

    val player1 = PlayerPointsDto(fullName = A_PLAYER_NAME, fantaPoints = 24.0)
    val player2 = PlayerPointsDto(fullName = ANOTHER_PLAYER_NAME, fantaPoints = 7.0)
    val teamDto1 = TeamDto(owner = AN_OWNER_ID, players = listOf(player1, player2), totalScore = 31.0)
    val expected = FoundFantaTournamentResults(tournament = FantaTournamentDto(listOf(teamDto1)))

    every { retrieveFantaTournamentsRepository.retrieveBy(A_TOURNAMENT_ID) } returns fantaTournament
    every { retrievePlayersPointsRepository.retrieveByYear(A_TOURNAMENT_YEAR) } returns foundPlayersPoints
    every { retrieveFantaTeamRepository.retrieveByFantaTournamentId(A_TOURNAMENT_ID) } returns teams

    assertThat(service.retrieve(A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  companion object {

    private const val A_TOURNAMENT_ID = 123
    private const val A_STARTING_TOURNAMENT_ID = 1
    private const val AN_ENDING_TOURNAMENT_ID = 2
    private const val A_TOURNAMENT_YEAR = 2000
    private const val A_TEAM_ID = 123
    private const val A_PLAYER_ID = "A_PLAYER_ID"
    private const val ANOTHER_PLAYER_ID = "ANOTHER_PLAYER_ID"
    private const val A_THIRD_PLAYER_ID = "A_THIRD_PLAYER_ID"
    private const val A_FOURTH_PLAYER_ID = "A_FOURTH_PLAYER_ID"
    private const val A_PLAYER_NAME = "A_PLAYER_NAME"
    private const val ANOTHER_PLAYER_NAME = "ANOTHER_PLAYER_NAME"
    private const val A_THIRD_PLAYER_NAME = "A_THIRD_PLAYER_NAME"
    private const val A_FOURTH_PLAYER_NAME = "A_FOURTH_PLAYER_NAME"
    private const val AN_OWNER_ID = "AN_OWNER_ID"
    private const val ANOTHER_OWNER_ID = "ANOTHER_OWNER_ID"
  }
}