package com.posadeus.fantatennis.domain.service.team

import com.posadeus.fantatennis.controller.model.team.*
import com.posadeus.fantatennis.domain.infrastructure.FantaTeamsRepository
import com.posadeus.fantatennis.domain.infrastructure.FantaTournamentsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CreateTeamServiceTest {

  private val fantaTeamsRepository: FantaTeamsRepository = mockk()
  private val fantaTournamentsRepository: FantaTournamentsRepository = mockk()

  private val service = CreateTeamService(fantaTeamsRepository, fantaTournamentsRepository)

  @Test
  fun `creation successful`() {

    val request = TeamToCreateDto(ownerId = "AN_OWNER_ID", tournament = TournamentCreationDto(id = 100))

    val fantaTournament: FantaTournament = ValidFantaTournament(id = 100,
                                                                startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                                                endingTournamentId = A_ENDING_TOURNAMENT_ID,
                                                                tournamentYear = A_TOURNAMENT_YEAR)
    val fantaTeam = FantaTeamOk(id = 1, ownerId = "AN_OWNER_ID")

    val expected: TeamCreation = TeamCreated(team = TeamCreatedDto(id = 1, ownerId = "AN_OWNER_ID"))

    every { fantaTournamentsRepository.retrieve(100) } returns fantaTournament
    every { fantaTeamsRepository.createTeam("AN_OWNER_ID", 100) } returns fantaTeam

    assertThat(service.create(request)).isEqualTo(expected)

    verify(exactly = 1) { fantaTournamentsRepository.retrieve(100) }
    verify(exactly = 1) { fantaTeamsRepository.createTeam("AN_OWNER_ID", 100) }
  }

  @Test
  fun `error from fantaTeamsRepository`() {

    val request = TeamToCreateDto(ownerId = AN_OWNER_ID, tournament = TournamentCreationDto(id = A_TOURNAMENT_ID))

    val expected = ErrorTeamCreation

    every { fantaTournamentsRepository.retrieve(100) } returns A_VALID_FANTA_TOURNAMENT
    every { fantaTeamsRepository.createTeam(AN_OWNER_ID, A_TOURNAMENT_ID) } returns FantaTeamError

    assertThat(service.create(request)).isEqualTo(expected)
  }

  companion object {

    private const val AN_OWNER_ID = "AN_OWNER_ID"
    private const val A_TOURNAMENT_ID = 100
    private const val A_STARTING_TOURNAMENT_ID = 1
    private const val A_ENDING_TOURNAMENT_ID = 2
    private const val A_TOURNAMENT_YEAR = 2024

    private val A_VALID_FANTA_TOURNAMENT = ValidFantaTournament(id = 100,
                                                                startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                                                endingTournamentId = A_ENDING_TOURNAMENT_ID,
                                                                tournamentYear = A_TOURNAMENT_YEAR)

  }
}


