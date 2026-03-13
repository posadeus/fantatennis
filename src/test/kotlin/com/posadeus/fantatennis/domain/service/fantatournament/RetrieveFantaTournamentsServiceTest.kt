package com.posadeus.fantatennis.domain.service.fantatournament

import com.posadeus.fantatennis.controller.model.fantatournament.FantaTournamentsDto
import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTournamentsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournaments.Invalid
import com.posadeus.fantatennis.domain.model.FantaTournaments.Valid
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.Test

class RetrieveFantaTournamentsServiceTest {

  private val retrieveFantaTournamentsRepository: RetrieveFantaTournamentsRepository = mockk()

  private val service = RetrieveFantaTournamentsService(retrieveFantaTournamentsRepository)

  @Test
  fun `tournaments retrieved`() {

    val fantaTournaments = Valid(setOf(aValidFantaTournamentWithId(1),
                                       aValidFantaTournamentWithId(2),
                                       aValidFantaTournamentWithId(3)))

    val tournaments = FantaTournamentsDto(ids = listOf(1, 2, 3))
    val expected = FoundFantaTournamentsResults(tournaments = tournaments)

    every { retrieveFantaTournamentsRepository.retrieve() } returns fantaTournaments

    assertThat(service.retrieveAll()).isEqualTo(expected)
  }

  @Test
  fun `tournaments not found`() {

    val fantaTournaments = Valid(emptySet())

    val expected = NotFoundFantaTournaments

    every { retrieveFantaTournamentsRepository.retrieve() } returns fantaTournaments

    assertThat(service.retrieveAll()).isEqualTo(expected)
  }

  @Test
  fun `error during retrieve`() {

    val fantaTournaments = Invalid

    val expected = ErrorFantaTournamentsResults

    every { retrieveFantaTournamentsRepository.retrieve() } returns fantaTournaments

    assertThat(service.retrieveAll()).isEqualTo(expected)
  }

  private fun aValidFantaTournamentWithId(id: Int) =
      ValidFantaTournament(id = id,
                           startingTournamentId = 1,
                           endingTournamentId = 2,
                           tournamentYear = 2024)
}