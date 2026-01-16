package com.posadeus.fantatennis.domain.service

import com.posadeus.fantatennis.controller.model.team.PlayerPointsDto
import com.posadeus.fantatennis.controller.model.tournament.TournamentDto
import com.posadeus.fantatennis.domain.infrastructure.RetrievePlayersPointsRepository
import com.posadeus.fantatennis.domain.infrastructure.RetrieveTournamentsRepository
import com.posadeus.fantatennis.domain.model.PlayersPoints.FoundPlayersPoints
import com.posadeus.fantatennis.domain.model.PlayersPoints.InternalErrorPlayersPoints
import com.posadeus.fantatennis.domain.model.TestPlayerPoints.aPlayerPoints
import com.posadeus.fantatennis.domain.model.TestTournament.aTournament
import com.posadeus.fantatennis.domain.model.Tournament.InternalErrorTournament
import com.posadeus.fantatennis.domain.model.Tournament.NotFoundTournament
import com.posadeus.fantatennis.domain.model.TournamentResults.*
import io.mockk.*
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test

class RetrieveTournamentServiceTest {

  private val retrieveTournamentsRepository: RetrieveTournamentsRepository = mockk()
  private val retrievePlayersPointsRepository: RetrievePlayersPointsRepository = mockk()

  private val service = RetrieveTournamentService(retrieveTournamentsRepository, retrievePlayersPointsRepository)

  @Test
  fun `retrieve tournament fails due to internal error`() {

    val expected = ErrorTournamentResults

    every { retrieveTournamentsRepository.retrieveBy(A_TOURNAMENT_ID) } returns InternalErrorTournament

    assertThat(service.retrieve(A_TOURNAMENT_ID)).isEqualTo(expected)

    verify { retrievePlayersPointsRepository wasNot called }
  }

  @Test
  fun `retrieve tournament fails due to tournament not found`() {

    val expected = NotFoundTournamentId

    every { retrieveTournamentsRepository.retrieveBy(A_TOURNAMENT_ID) } returns NotFoundTournament

    assertThat(service.retrieve(A_TOURNAMENT_ID)).isEqualTo(expected)

    verify { retrievePlayersPointsRepository wasNot called }
  }

  @Test
  fun `retrieve tournament does not found playersPoints`() {

    val tournament = aTournament(name = A_TOURNAMENT_NAME, points = A_TOURNAMENT_POINTS)
    val playersPoints = InternalErrorPlayersPoints

    val expected = ErrorTournamentResults

    every { retrieveTournamentsRepository.retrieveBy(A_TOURNAMENT_ID) } returns tournament
    every { retrievePlayersPointsRepository.retrieveBy(A_TOURNAMENT_ID) } returns playersPoints

    assertThat(service.retrieve(A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  @Test
  fun `retrieve tournament succeed`() {

    val tournament = aTournament(name = A_TOURNAMENT_NAME, points = A_TOURNAMENT_POINTS)
    val playersPoints1 = aPlayerPoints(playerName = "A_FULL_NAME", totalPoints = 20.00)
    val playersPoints2 = aPlayerPoints(playerName = "ANOTHER_FULL_NAME", totalPoints = 10.00)
    val playersPoints3 = aPlayerPoints(playerName = "A_THIRD_FULL_NAME", totalPoints = 14.00)
    val playersPoints = listOf(playersPoints1, playersPoints2, playersPoints3)
    val foundPlayersPoints = FoundPlayersPoints(playersPoints = playersPoints)

    val playerPointsDto1 = PlayerPointsDto(fullName = "A_FULL_NAME", fantaPoints = 20.00)
    val playerPointsDto2 = PlayerPointsDto(fullName = "A_THIRD_FULL_NAME", fantaPoints = 14.00)
    val playerPointsDto3 = PlayerPointsDto(fullName = "ANOTHER_FULL_NAME", fantaPoints = 10.00)
    val expected = FoundTournamentResults(tournament = TournamentDto(tournamentName = A_TOURNAMENT_NAME,
                                                                     tournamentPoints = A_TOURNAMENT_POINTS,
                                                                     playersScore = listOf(playerPointsDto1,
                                                                                           playerPointsDto2,
                                                                                           playerPointsDto3)))

    every { retrieveTournamentsRepository.retrieveBy(A_TOURNAMENT_ID) } returns tournament
    every { retrievePlayersPointsRepository.retrieveBy(A_TOURNAMENT_ID) } returns foundPlayersPoints

    assertThat(service.retrieve(A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  companion object {

    private const val A_TOURNAMENT_ID = 1
    private const val A_TOURNAMENT_POINTS = 250
    private const val A_TOURNAMENT_NAME = "A_TOURNAMENT_NAME"
  }
}