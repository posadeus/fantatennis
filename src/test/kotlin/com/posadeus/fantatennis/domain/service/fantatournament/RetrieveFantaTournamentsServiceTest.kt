package com.posadeus.fantatennis.domain.service.fantatournament

import com.posadeus.fantatennis.controller.model.tournament.TournamentsDto
import com.posadeus.fantatennis.domain.infrastructure.RetrieveAllFantaTournamentsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.FantaTournament.InvalidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.Test

class RetrieveFantaTournamentsServiceTest {

  private val retrieveAllFantaTournamentsRepository: RetrieveAllFantaTournamentsRepository = mockk()

  private val service = RetrieveFantaTournamentsService(retrieveAllFantaTournamentsRepository)

  @Test
  fun `tournaments retrieved`() {

    val fantaTournaments = setOf(aValidFantaTournamentWithId(1),
                                 aValidFantaTournamentWithId(2),
                                 aValidFantaTournamentWithId(3))

    val tournaments = TournamentsDto(ids = listOf(1, 2, 3))
    val expected = FoundFantaTournamentsResults(tournaments = tournaments)

    every { retrieveAllFantaTournamentsRepository.retrieve() } returns fantaTournaments

    assertThat(service.retrieveAll()).isEqualTo(expected)
  }

  @Test
  fun `tournaments not found`() {

    val fantaTournaments = setOf<FantaTournament>()

    val expected = NotFoundFantaTournaments

    every { retrieveAllFantaTournamentsRepository.retrieve() } returns fantaTournaments

    assertThat(service.retrieveAll()).isEqualTo(expected)
  }

  @Test
  fun `error during retrieve`() {

    val fantaTournaments = setOf(InvalidFantaTournament)

    val expected = ErrorFantaTournamentsResults

    every { retrieveAllFantaTournamentsRepository.retrieve() } returns fantaTournaments

    assertThat(service.retrieveAll()).isEqualTo(expected)
  }

  private fun aValidFantaTournamentWithId(id: Int) =
      ValidFantaTournament(id = id,
                           startingTournamentId = 1,
                           endingTournamentId = 2,
                           tournamentYear = 2024)
}