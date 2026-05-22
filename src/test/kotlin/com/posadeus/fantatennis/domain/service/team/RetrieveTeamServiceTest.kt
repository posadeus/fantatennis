package com.posadeus.fantatennis.domain.service.team

import com.posadeus.fantatennis.domain.infrastructure.*
import com.posadeus.fantatennis.domain.model.DomainTeam.FoundDomainTeam
import com.posadeus.fantatennis.domain.model.DomainTeam.NotFoundDomainTeam
import com.posadeus.fantatennis.domain.model.ErrorTeam
import com.posadeus.fantatennis.domain.model.FantaTournament.InvalidFantaTournament
import com.posadeus.fantatennis.domain.model.TeamIdNotFoundTeam
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RetrieveTeamServiceTest {

  private val retrieveFantaTeamRepository: RetrieveFantaTeamRepository = mockk()
  private val retrieveFantaTournamentsRepository: RetrieveFantaTournamentsRepository = mockk()
  private val retrievePlayersPointsRepository: RetrievePlayersPointsRepository = mockk()

  private val service = RetrieveTeamService(retrieveFantaTeamRepository,
                                            retrieveFantaTournamentsRepository,
                                            retrievePlayersPointsRepository)

  @Test
  fun `team not found when retrieveByTeamId returns NotFoundDomainTeam`() {

    every { retrieveFantaTeamRepository.retrieveByTeamId(A_TEAM_ID) } returns NotFoundDomainTeam(A_TEAM_ID)

    assertThat(service.retrieve(A_TEAM_ID)).isEqualTo(TeamIdNotFoundTeam)
  }

  @Test
  fun `error when retrieveBy returns InvalidFantaTournament`() {

    val foundDomainTeam = FoundDomainTeam(teamId = A_TEAM_ID,
                                          ownerId = AN_OWNER_ID,
                                          fantaTournamentId = A_FANTA_TOURNAMENT_ID,
                                          players = emptyMap())

    every { retrieveFantaTeamRepository.retrieveByTeamId(A_TEAM_ID) } returns foundDomainTeam
    every { retrieveFantaTournamentsRepository.retrieveBy(A_FANTA_TOURNAMENT_ID) } returns InvalidFantaTournament

    assertThat(service.retrieve(A_TEAM_ID)).isEqualTo(ErrorTeam)
  }

  companion object {

    private const val A_TEAM_ID = 1
    private const val AN_OWNER_ID = "AN_OWNER_ID"
    private const val A_FANTA_TOURNAMENT_ID = 42
  }
}
