package com.posadeus.fantatennis.domain.service

import com.posadeus.fantatennis.controller.model.team.PlayerPointsDto
import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.domain.exception.InvalidAddPlayersException
import com.posadeus.fantatennis.domain.infrastructure.*
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.DomainTeam.NotFoundDomainTeam
import com.posadeus.fantatennis.domain.model.TestDomainPlayer.aDomainPlayer
import com.posadeus.fantatennis.domain.model.TestDomainTeam.aDomainTeam
import com.posadeus.fantatennis.domain.model.TestTournament.aTournament
import com.posadeus.fantatennis.domain.model.Tournament.InternalErrorTournament
import com.posadeus.fantatennis.domain.model.Tournament.NotFoundTournament
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AddPlayersTeamServiceTest {

  private val persistTeamPlayersRepository: PersistTeamPlayersRepository = mockk()
  private val retrieveFantaTeamRepository: RetrieveFantaTeamRepository = mockk()
  private val retrieveTournamentsRepository: RetrieveTournamentsRepository = mockk()
  private val retrievePlayersRepository: RetrievePlayersRepository = mockk()

  private val service = AddPlayersTeamService(persistTeamPlayersRepository,
                                              retrieveFantaTeamRepository,
                                              retrieveTournamentsRepository,
                                              retrievePlayersRepository)

  @Test
  fun `add players fails - team not found`() {

    val expected = TeamIdNotFoundTeam

    every { retrieveFantaTeamRepository.retrieveByTeamId(A_TEAM_ID) } returns NotFoundDomainTeam(A_TEAM_ID)

    assertThat(service.addPlayers(A_TEAM_ID, setOf(A_PLAYER_ID), A_STARTING_TOURNAMENT_ID)).isEqualTo(expected)
  }

  @Test
  fun `add players fails - tournament not found`() {

    val team = aDomainTeam(teamId = A_TEAM_ID)
    val tournament = NotFoundTournament
    val expected = ErrorTeam

    every { retrieveFantaTeamRepository.retrieveByTeamId(A_TEAM_ID) } returns team
    every { retrieveTournamentsRepository.retrieveBy(A_STARTING_TOURNAMENT_ID) } returns tournament

    assertThat(service.addPlayers(A_TEAM_ID, setOf(A_PLAYER_ID), A_STARTING_TOURNAMENT_ID)).isEqualTo(expected)
  }

  @Test
  fun `add players fails - tournament error`() {

    val team = aDomainTeam(teamId = A_TEAM_ID)
    val tournament = InternalErrorTournament
    val expected = ErrorTeam

    every { retrieveFantaTeamRepository.retrieveByTeamId(A_TEAM_ID) } returns team
    every { retrieveTournamentsRepository.retrieveBy(A_STARTING_TOURNAMENT_ID) } returns tournament

    assertThat(service.addPlayers(A_TEAM_ID, setOf(A_PLAYER_ID), A_STARTING_TOURNAMENT_ID)).isEqualTo(expected)
  }

  @Test
  fun `add players fails - one or more players not found`() {

    val team = aDomainTeam(teamId = A_TEAM_ID)
    val tournament = aTournament()
    val domainPlayers = setOf(aDomainPlayer(id = A_PLAYER_ID))
    val expected = ErrorTeam

    every { retrieveFantaTeamRepository.retrieveByTeamId(A_TEAM_ID) } returns team
    every { retrieveTournamentsRepository.retrieveBy(A_STARTING_TOURNAMENT_ID) } returns tournament
    every { retrievePlayersRepository.retrieve() } returns domainPlayers

    assertThat(service.addPlayers(A_TEAM_ID, setOf(A_PLAYER_ID, ANOTHER_PLAYER_ID), A_STARTING_TOURNAMENT_ID)).isEqualTo(expected)
  }

  @Test
  fun `add players fails - internal error`() {

    val team = aDomainTeam(teamId = A_TEAM_ID)
    val tournament = aTournament()
    val domainPlayers = setOf(DomainPlayer(id = A_PLAYER_ID,
                                           atpId = AN_ATP_PLAYER_ID,
                                           fullName = A_PLAYER_FULL_NAME))
    val addPlayersError = InvalidAddPlayersException("")
    val expected = ErrorTeam

    every { retrieveFantaTeamRepository.retrieveByTeamId(A_TEAM_ID) } returns team
    every { retrieveTournamentsRepository.retrieveBy(A_STARTING_TOURNAMENT_ID) } returns tournament
    every { retrievePlayersRepository.retrieve() } returns domainPlayers
    every { persistTeamPlayersRepository.persist(A_TEAM_ID, setOf(A_PLAYER_ID), A_STARTING_TOURNAMENT_ID) } throws addPlayersError

    assertThat(service.addPlayers(A_TEAM_ID, setOf(A_PLAYER_ID), A_STARTING_TOURNAMENT_ID)).isEqualTo(expected)
  }

  @Test
  fun `add players correctly`() {

    val team = aDomainTeam(teamId = A_TEAM_ID)
    val tournament = aTournament()
    val domainPlayers = setOf(DomainPlayer(id = A_PLAYER_ID,
                                           atpId = AN_ATP_PLAYER_ID,
                                           fullName = A_PLAYER_FULL_NAME))

    val expected = FoundTeam(team = TeamDto(players = listOf(PlayerPointsDto(fullName = A_PLAYER_FULL_NAME,
                                                                             fantaPoints = 0.0)),
                                            totalScore = 0.0))

    every { retrieveFantaTeamRepository.retrieveByTeamId(A_TEAM_ID) } returns team
    every { retrieveTournamentsRepository.retrieveBy(A_STARTING_TOURNAMENT_ID) } returns tournament
    every { retrievePlayersRepository.retrieve() } returns domainPlayers
    every { persistTeamPlayersRepository.persist(A_TEAM_ID, setOf(A_PLAYER_ID), A_STARTING_TOURNAMENT_ID) } returns Unit

    assertThat(service.addPlayers(A_TEAM_ID, setOf(A_PLAYER_ID), A_STARTING_TOURNAMENT_ID)).isEqualTo(expected)

    verify(exactly = 1) { persistTeamPlayersRepository.persist(A_TEAM_ID, setOf(A_PLAYER_ID), A_STARTING_TOURNAMENT_ID) }
  }

  companion object {

    private const val A_TEAM_ID = 1
    private const val A_STARTING_TOURNAMENT_ID = 123
    private const val A_PLAYER_FULL_NAME = "A_PLAYER_FULL_NAME"
    private const val A_PLAYER_ID = "A_PLAYER_ID"
    private const val ANOTHER_PLAYER_ID = "ANOTHER_PLAYER_ID"
    private const val AN_ATP_PLAYER_ID = "AN_ATP_PLAYER_ID"
  }
}