package com.posadeus.fantatennis.domain.service.team

import com.posadeus.fantatennis.controller.model.team.TeamCreatedDto
import com.posadeus.fantatennis.controller.model.team.TeamToCreateDto
import com.posadeus.fantatennis.domain.infrastructure.FantaTeamsRepository
import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTournamentRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.FantaTournament.InvalidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CreateTeamServiceTest {

  private val fantaTeamsRepository: FantaTeamsRepository = mockk()
  private val retrieveFantaTournamentsRepository: RetrieveFantaTournamentRepository = mockk()

  private val service = CreateTeamService(fantaTeamsRepository, retrieveFantaTournamentsRepository)

  @Test
  fun `creation succeeds with already present fanta tournament`() {

    val dto = TeamToCreateDto(ownerId = "AN_OWNER_ID", tournamentId = 100)

    val fantaTournament = ValidFantaTournament(id = 100,
                                               startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                               endingTournamentId = A_ENDING_TOURNAMENT_ID,
                                               tournamentYear = A_TOURNAMENT_YEAR)
    val fantaTeam = FantaTeamOk(id = 1, ownerId = "AN_OWNER_ID")

    val expected: TeamCreation = TeamCreated(team = TeamCreatedDto(id = 1, ownerId = "AN_OWNER_ID"))

    every { retrieveFantaTournamentsRepository.retrieve(100) } returns fantaTournament
    every { fantaTeamsRepository.createTeam("AN_OWNER_ID", fantaTournament) } returns fantaTeam

    assertThat(service.create(dto)).isEqualTo(expected)

    verify(exactly = 1) { retrieveFantaTournamentsRepository.retrieve(100) }
    verify(exactly = 1) { fantaTeamsRepository.createTeam("AN_OWNER_ID", fantaTournament) }
  }

  @Test
  fun `creation fails due to tournamentId not found`() {

    val dto = TeamToCreateDto(ownerId = AN_OWNER_ID, tournamentId = A_NOT_EXISTING_TOURNAMENT_ID)

    val fantaTournament: FantaTournament = InvalidFantaTournament

    val expected: TeamCreation = ErrorTeamCreation

    every { retrieveFantaTournamentsRepository.retrieve(A_NOT_EXISTING_TOURNAMENT_ID) } returns fantaTournament

    assertThat(service.create(dto)).isEqualTo(expected)

    verify(exactly = 1) { retrieveFantaTournamentsRepository.retrieve(A_NOT_EXISTING_TOURNAMENT_ID) }
    verify { fantaTeamsRepository wasNot called }
  }

  @Test
  fun `error from fantaTeamsRepository`() {

    val request = TeamToCreateDto(ownerId = AN_OWNER_ID, tournamentId = A_TOURNAMENT_ID)

    val expected = ErrorTeamCreation

    every { retrieveFantaTournamentsRepository.retrieve(A_TOURNAMENT_ID) } returns A_VALID_FANTA_TOURNAMENT
    every { fantaTeamsRepository.createTeam(AN_OWNER_ID, A_VALID_FANTA_TOURNAMENT) } returns FantaTeamError

    assertThat(service.create(request)).isEqualTo(expected)
  }

  companion object {

    private const val AN_OWNER_ID = "AN_OWNER_ID"
    private const val A_TOURNAMENT_ID = 100
    private const val A_NOT_EXISTING_TOURNAMENT_ID = 120
    private const val A_STARTING_TOURNAMENT_ID = 1
    private const val A_ENDING_TOURNAMENT_ID = 2
    private const val A_TOURNAMENT_YEAR = 2024

    private val A_VALID_FANTA_TOURNAMENT = ValidFantaTournament(id = A_TOURNAMENT_ID,
                                                                startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                                                endingTournamentId = A_ENDING_TOURNAMENT_ID,
                                                                tournamentYear = A_TOURNAMENT_YEAR)
  }
}


