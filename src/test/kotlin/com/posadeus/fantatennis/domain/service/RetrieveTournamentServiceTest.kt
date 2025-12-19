package com.posadeus.fantatennis.domain.service

import com.posadeus.fantatennis.controller.model.team.PlayerPointsDto
import com.posadeus.fantatennis.controller.model.tournament.TournamentDto
import com.posadeus.fantatennis.domain.infrastructure.RetrievePlayersPointsRepository
import com.posadeus.fantatennis.domain.infrastructure.TournamentsRegistryRepository
import com.posadeus.fantatennis.domain.model.TestPlayerPoints.aPlayerPoints
import com.posadeus.fantatennis.domain.model.TestTournamentRegistry.aTournamentRegistry
import com.posadeus.fantatennis.domain.model.TournamentResults.FoundTournamentResults
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test

class RetrieveTournamentServiceTest {

  private val tournamentRegistryRepository: TournamentsRegistryRepository = mockk()
  private val retrievePlayersPointsRepository: RetrievePlayersPointsRepository = mockk()

  private val service = RetrieveTournamentService(tournamentRegistryRepository, retrievePlayersPointsRepository)

  @Test
  fun `retrieve tournament succeed`() {

    val tournamentRegistry = aTournamentRegistry(name = A_TOURNAMENT_NAME, points = A_TOURNAMENT_POINTS)
    val playersPoints1 = aPlayerPoints(playerName = "A_FULL_NAME", totalPoints = 20.00)
    val playersPoints2 = aPlayerPoints(playerName = "ANOTHER_FULL_NAME", totalPoints = 10.00)
    val playersPoints3 = aPlayerPoints(playerName = "A_THIRD_FULL_NAME", totalPoints = 14.00)
    val playersPoints = listOf(playersPoints1, playersPoints2, playersPoints3)

    val playerPointsDto1 = PlayerPointsDto(fullName = "A_FULL_NAME", fantaPoints = 20.00)
    val playerPointsDto2 = PlayerPointsDto(fullName = "A_THIRD_FULL_NAME", fantaPoints = 14.00)
    val playerPointsDto3 = PlayerPointsDto(fullName = "ANOTHER_FULL_NAME", fantaPoints = 10.00)
    val expected = FoundTournamentResults(tournament = TournamentDto(tournamentName = A_TOURNAMENT_NAME,
                                                                     tournamentPoints = A_TOURNAMENT_POINTS,
                                                                     playersScore = listOf(playerPointsDto1,
                                                                                           playerPointsDto2,
                                                                                           playerPointsDto3)))

    every { tournamentRegistryRepository.retrieveBy(A_TOURNAMENT_ID) } returns tournamentRegistry
    every { retrievePlayersPointsRepository.retrieveBy(A_TOURNAMENT_ID) } returns playersPoints

    assertThat(service.retrieve(A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  companion object {

    private const val A_TOURNAMENT_ID = 1
    private const val A_TOURNAMENT_POINTS = 250
    private const val A_TOURNAMENT_NAME = "A_TOURNAMENT_NAME"
  }
}