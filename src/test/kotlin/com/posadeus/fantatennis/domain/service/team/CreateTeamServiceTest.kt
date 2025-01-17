package com.posadeus.fantatennis.domain.service.team

import com.posadeus.fantatennis.controller.model.team.*
import com.posadeus.fantatennis.domain.infrastructure.FantaTeamsRepository
import com.posadeus.fantatennis.domain.infrastructure.FantaTournamentsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.FantaTournament.InvalidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CreateTeamServiceTest {

  private val fantaTeamsRepository: FantaTeamsRepository = mockk()
  private val fantaTournamentsRepository: FantaTournamentsRepository = mockk()

  private val service = CreateTeamService(fantaTeamsRepository, fantaTournamentsRepository)

  @Test
  fun `create team and assign to a tournament`() {

    val dto = TeamToCreateDto(ownerId = "", tournament = TournamentCreationDto(id = 2))

    val fantaTournament = ValidFantaTournament(id = 2,
                                               startingTournamentId = 1,
                                               endingTournamentId = 1,
                                               tournamentYear = 1)

    val expected = TeamCreated(team = TeamCreatedDto(id = 1, ownerId = ""))

    every { fantaTournamentsRepository.retrieve(2) } returns fantaTournament
    every { fantaTeamsRepository.createTeam("", fantaTournament) } returns FantaTeamOk(id = 1, ownerId = "")

    assertThat(service.create(dto)).isEqualTo(expected)
  }

  @Test
  fun `tournament not found`() {

    val dto = TeamToCreateDto(ownerId = "", tournament = TournamentCreationDto(id = 2))

    val expected = ErrorTeamCreation

    every { fantaTournamentsRepository.retrieve(2) } returns InvalidFantaTournament

    assertThat(service.create(dto)).isEqualTo(expected)

    verify { fantaTeamsRepository wasNot called }
  }

  @Test
  fun `team creation error`() {

    val dto = TeamToCreateDto(ownerId = "", tournament = TournamentCreationDto(id = 2))

    val fantaTournament = ValidFantaTournament(id = 2,
                                               startingTournamentId = 1,
                                               endingTournamentId = 1,
                                               tournamentYear = 1)

    val expected = ErrorTeamCreation

    every { fantaTournamentsRepository.retrieve(2) } returns fantaTournament
    every { fantaTeamsRepository.createTeam("", fantaTournament) } returns FantaTeamError

    assertThat(service.create(dto)).isEqualTo(expected)
  }
}