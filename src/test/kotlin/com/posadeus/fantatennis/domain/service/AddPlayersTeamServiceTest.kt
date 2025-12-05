package com.posadeus.fantatennis.domain.service

import com.posadeus.fantatennis.controller.model.team.PlayerPointsDto
import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.domain.exception.InvalidAddPlayersException
import com.posadeus.fantatennis.domain.infrastructure.AddPlayersToTeamRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.AddPlayers.InvalidAddPlayers.AddPlayersTeamNotFound
import com.posadeus.fantatennis.domain.model.AddPlayers.InvalidAddPlayers.PlayersNotFound
import com.posadeus.fantatennis.domain.model.AddPlayers.ValidAddPlayers
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AddPlayersTeamServiceTest {

  private val addPlayersToTeamRepository: AddPlayersToTeamRepository = mockk()

  private val service = AddPlayersTeamService(addPlayersToTeamRepository)

  @Test
  fun `add players correctly`() {

    val domainPlayers = setOf(DomainPlayer(id = A_PLAYER_ID,
                                           atpId = AN_ATP_PLAYER_ID,
                                           fullName = A_PLAYER_FULL_NAME))
    val validAddPlayers = ValidAddPlayers(players = domainPlayers)

    val expected = FoundTeam(team = TeamDto(players = listOf(PlayerPointsDto(fullName = A_PLAYER_FULL_NAME,
                                                                             fantaPoints = 0.0)),
                                            totalScore = 0.0))

    every { addPlayersToTeamRepository.add(A_TEAM_ID, setOf(A_PLAYER_ID), A_STARTING_TOURNAMENT_ID) } returns validAddPlayers

    assertThat(service.addPlayers(A_TEAM_ID, setOf(A_PLAYER_ID), A_STARTING_TOURNAMENT_ID)).isEqualTo(expected)
  }

  @Test
  fun `add players fails - team not found`() {

    val addPlayersError = AddPlayersTeamNotFound
    val expected = TeamIdNotFoundTeam

    every { addPlayersToTeamRepository.add(A_TEAM_ID, setOf(A_PLAYER_ID), A_STARTING_TOURNAMENT_ID) } returns addPlayersError

    assertThat(service.addPlayers(A_TEAM_ID, setOf(A_PLAYER_ID), A_STARTING_TOURNAMENT_ID)).isEqualTo(expected)
  }

  @Test
  fun `add players fails - one or more players not found`() {

    val addPlayersError = PlayersNotFound(missingPlayerIds = setOf(ANOTHER_PLAYER_ID))
    val expected = ErrorTeam

    every { addPlayersToTeamRepository.add(A_TEAM_ID, setOf(A_PLAYER_ID, ANOTHER_PLAYER_ID), A_STARTING_TOURNAMENT_ID) } returns addPlayersError

    assertThat(service.addPlayers(A_TEAM_ID, setOf(A_PLAYER_ID, ANOTHER_PLAYER_ID), A_STARTING_TOURNAMENT_ID)).isEqualTo(expected)
  }

  @Test
  fun `add players fails - internal error`() {

    val addPlayersError = InvalidAddPlayersException("")
    val expected = ErrorTeam

    every { addPlayersToTeamRepository.add(A_TEAM_ID, setOf(A_PLAYER_ID), A_STARTING_TOURNAMENT_ID) } throws addPlayersError

    assertThat(service.addPlayers(A_TEAM_ID, setOf(A_PLAYER_ID), A_STARTING_TOURNAMENT_ID)).isEqualTo(expected)
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