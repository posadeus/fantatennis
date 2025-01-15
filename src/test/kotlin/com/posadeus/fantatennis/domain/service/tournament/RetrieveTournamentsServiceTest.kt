package com.posadeus.fantatennis.domain.service.tournament

import com.posadeus.fantatennis.controller.model.tournament.TournamentsDto
import com.posadeus.fantatennis.domain.infrastructure.FantaTournamentsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.FantaTournament.InvalidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.Test

class RetrieveTournamentsServiceTest {

  private val fantaTournamentsRepository: FantaTournamentsRepository = mockk()

  private val service = RetrieveTournamentsService(fantaTournamentsRepository)

  @Test
  fun `tournaments retrieved`() {

    val fantaTournaments = listOf(aValidFantaTournamentWithId(1),
                                  aValidFantaTournamentWithId(2),
                                  aValidFantaTournamentWithId(3))

    val tournaments = TournamentsDto(ids = listOf(1, 2, 3))
    val expected = FoundFantaTournamentsResults(tournaments = tournaments)

    every { fantaTournamentsRepository.retrieveAll() } returns fantaTournaments

    assertThat(service.retrieveAll()).isEqualTo(expected)
  }

  @Test
  fun `tournaments not found`() {

    val fantaTournaments = emptyList<FantaTournament>()

    val expected = NotFoundFantaTournaments

    every { fantaTournamentsRepository.retrieveAll() } returns fantaTournaments

    assertThat(service.retrieveAll()).isEqualTo(expected)
  }

  @Test
  fun `error during retrieve`() {

    val fantaTournaments = listOf(InvalidFantaTournament)

    val expected = ErrorFantaTournamentsResults

    every { fantaTournamentsRepository.retrieveAll() } returns fantaTournaments

    assertThat(service.retrieveAll()).isEqualTo(expected)
  }

  private fun aValidFantaTournamentWithId(id: Int) =
      ValidFantaTournament(id = id,
                           startingTournamentId = 1,
                           endingTournamentId = 2,
                           tournamentYear = 2024)
}