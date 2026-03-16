package com.posadeus.fantatennis.domain.service.fantatournament

import com.posadeus.fantatennis.domain.infrastructure.*
import com.posadeus.fantatennis.domain.model.DomainTeam.NotFoundDomainTeam
import com.posadeus.fantatennis.domain.model.FantaTournament.InvalidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournamentResults.ErrorFantaTournamentResults
import com.posadeus.fantatennis.domain.model.FantaTournamentResults.NotFoundFantaTournamentId
import com.posadeus.fantatennis.domain.model.PlayersPoints.FoundPlayersPoints
import com.posadeus.fantatennis.domain.model.PlayersPoints.InternalErrorPlayersPoints
import com.posadeus.fantatennis.domain.model.Teams
import com.posadeus.fantatennis.domain.model.TestDomainTeam.aDomainTeam
import com.posadeus.fantatennis.domain.model.TestPlayerPoints.aPlayerPoints
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RetrieveFantaTournamentServiceTest {

  private val retrieveFantaTournamentResultsRepository: RetrieveFantaTournamentResultsRepository = mockk()
  private val retrieveFantaTournamentsRepository: RetrieveFantaTournamentsRepository = mockk()
  private val retrievePlayersPointsRepository: RetrievePlayersPointsRepository = mockk()
  private val retrieveFantaTeamRepository: RetrieveFantaTeamRepository = mockk()

  private val service = RetrieveFantaTournamentService(retrieveFantaTournamentResultsRepository,
                                                       retrieveFantaTournamentsRepository,
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
  fun `retrieve fails due to no teams found in FantaTeamRepository`() {

    val fantaTournament = ValidFantaTournament(id = A_TOURNAMENT_ID,
                                               startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                               endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                               tournamentYear = A_TOURNAMENT_YEAR)
    val playerPoints = aPlayerPoints()
    val foundPlayersPoints = FoundPlayersPoints(listOf(playerPoints))
    val teams = Teams(emptyList())

    val expected = ErrorFantaTournamentResults

    every { retrieveFantaTournamentsRepository.retrieveBy(A_TOURNAMENT_ID) } returns fantaTournament
    every { retrievePlayersPointsRepository.retrieveByYear(A_TOURNAMENT_YEAR) } returns foundPlayersPoints
    every { retrieveFantaTeamRepository.retrieveByFantaTournamentId(A_TOURNAMENT_ID) } returns teams

    assertThat(service.retrieve(A_TOURNAMENT_ID)).isEqualTo(expected)
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

  companion object {

    private const val A_TOURNAMENT_ID = 123
    private const val A_STARTING_TOURNAMENT_ID = 1
    private const val AN_ENDING_TOURNAMENT_ID = 2
    private const val A_TOURNAMENT_YEAR = 2000
    private const val A_TEAM_ID = 123
  }
}