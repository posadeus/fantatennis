package com.posadeus.fantatennis.domain.service.fantatournament

import com.posadeus.fantatennis.controller.model.tournament.TournamentCreatedDto
import com.posadeus.fantatennis.controller.model.tournament.TournamentToCreateDto
import com.posadeus.fantatennis.domain.infrastructure.FantaTournamentsRepository
import com.posadeus.fantatennis.domain.model.ErrorTournamentCreation
import com.posadeus.fantatennis.domain.model.FantaTournament.InvalidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import com.posadeus.fantatennis.domain.model.SuccessTournamentCreated
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CreateFantaTournamentServiceTest {

  private val fantaTournamentsRepository: FantaTournamentsRepository = mockk()

  private val service = CreateFantaTournamentService(fantaTournamentsRepository)

  @Test
  fun `create tournament successfully`() {

    val dto = TournamentToCreateDto(startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                    endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                    tournamentYear = A_TOURNAMENT_YEAR)
    val fantaTournament = ValidFantaTournament(id = A_TOURNAMENT_ID,
                                               startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                               endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                               tournamentYear = A_TOURNAMENT_YEAR)

    val expected = SuccessTournamentCreated(TournamentCreatedDto(id = A_TOURNAMENT_ID,
                                                                 startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                                                 endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                                                 tournamentYear = A_TOURNAMENT_YEAR))

    every { fantaTournamentsRepository.create(dto) } returns fantaTournament

    assertThat(service.create(dto)).isEqualTo(expected)
  }

  @Test
  fun `create tournament fails due to error from repository`() {

    val dto = TournamentToCreateDto(startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                    endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                    tournamentYear = A_TOURNAMENT_YEAR)
    val fantaTournament = InvalidFantaTournament

    val expected = ErrorTournamentCreation

    every { fantaTournamentsRepository.create(dto) } returns fantaTournament

    assertThat(service.create(dto)).isEqualTo(expected)
  }

  companion object {

    private const val A_STARTING_TOURNAMENT_ID = 1
    private const val AN_ENDING_TOURNAMENT_ID = 2
    private const val A_TOURNAMENT_YEAR = 2222
    private const val A_TOURNAMENT_ID = 123
  }
}